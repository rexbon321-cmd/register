package pl.authnonpremium;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class AuthManager implements Listener {

    private final File file;
    private final YamlConfiguration config;
    private final Set<String> loggedPlayers = new HashSet<>();

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

    public boolean isRegistered(String name) {
        return config.contains(name);
    }

    public void register(String name, String password) {
        config.set(name, HashUtil.hash(password));
        save();
    }

    public boolean login(String name, String password) {
        String stored = config.getString(name);
        return stored != null && stored.equals(HashUtil.hash(password));
    }

    public void setLogged(Player player) {
        loggedPlayers.add(player.getName());
    }

    public boolean isLogged(Player player) {
        return loggedPlayers.contains(player.getName());
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().sendMessage("§c/login <hasło> lub /register <hasło>");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        loggedPlayers.remove(e.getPlayer().getName());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!isLogged(e.getPlayer())) e.setCancelled(true);
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
