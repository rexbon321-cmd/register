package pl.authnonpremium;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoginCommand implements CommandExecutor {

    private final AuthManager auth;

    public LoginCommand(AuthManager auth) {
        this.auth = auth;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) return true;

        if (!auth.isRegistered(player.getUniqueId())) {
            player.sendMessage("§cNajpierw się zarejestruj!");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("§c/login <hasło>");
            return true;
        }

        if (auth.login(player.getUniqueId(), args[0])) {
            auth.setLogged(player);
            player.sendMessage("§aZalogowano!");
        } else {
            player.sendMessage("§cBłędne hasło!");
        }
        return true;
    }
}
