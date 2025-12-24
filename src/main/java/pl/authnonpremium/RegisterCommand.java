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

        if (auth.isRegistered(player.getUniqueId())) {
            player.sendMessage("§cJuż jesteś zarejestrowany!");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage("§c/register <hasło> <powtórz hasło>");
            return true;
        }

        if (!args[0].equals(args[1])) {
            player.sendMessage("§cHasła nie są takie same!");
            return true;
        }

        auth.register(player.getUniqueId(), args[0]);
        auth.setLogged(player);
        player.sendMessage("§aZarejestrowano i zalogowano!");
        return true;
    }
}
