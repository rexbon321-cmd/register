package pl.authnonpremium;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {

    private final AuthManager auth;

    public RegisterCommand(AuthManager auth) {
        this.auth = auth;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) return true;

        if (auth.isRegistered(player.getName())) {
            player.sendMessage("§cJuż jesteś zarejestrowany!");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("§c/register <hasło>");
            return true;
        }

        auth.register(player.getName(), args[0]);
        player.sendMessage("§aZarejestrowano pomyślnie!");
        return true;
    }
}
