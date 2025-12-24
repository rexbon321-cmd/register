package pl.authnonpremium;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AuthManager implements Listener {

    private final File file;
    private final YamlConfiguration config;

    private final Set<UUID> loggedPlayers = new HashSet<>();
    private final Map<UUID, Location> lastLocation = new HashMap<>();

    public AuthManager(AuthPlugin plugin) {
        file = new File(plugin.getDataFolder(), "users.yml");

        if (!file.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public boolean isRegistered(UUID uuid) {
        return config.contains(uuid.toString());
    }

    public void register(UUID uuid, String password) {
        config.set(uuid.toString(), HashUtil.hash(password));
        save();
    }

    public boolean login(UUID uuid, String password) {
        String stored = config.getString(uuid.toString());
        return stored != null && stored.equals(HashUtil.hash(password));
    }

    public void setLogged(Player player) {
        loggedPlayers.add(player.getUniqueId());

        if (lastLocation.containsKey(player.getUniqueId())) {
            player.teleport(lastLocation.get(player.getUniqueId()));
            lastLocation.remove(player.getUniqueId());
        }
    }

    public boolean isLogged(Player player) {
        return loggedPlayers.contains(player.getUniqueId());
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ===================== EVENTS ===================== */

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        // PREMIUM → auto login
        if (player.getUniqueId().version() == 4) {
            loggedPlayers.add(player.getUniqueId());
            return;
        }

        lastLocation.put(player.getUniqueId(), player.getLocation());
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());

        if (isRegistered(player.getUniqueId())) {
            player.sendMessage("§eZaloguj się: §6/login <hasło>");
        } else {
            player.sendMessage("§eZarejestruj się: §6/register <hasło> <powtórz hasło>");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        loggedPlayers.remove(e.getPlayer().getUniqueId());
        lastLocation.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!isLogged(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player player) {
            if (!isLogged(player)) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (!isLogged(e.getPlayer()) &&
                !e.getMessage().startsWith("/login") &&
                !e.getMessage().startsWith("/register")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cMusisz się zalogować!");
        }
    }
}
