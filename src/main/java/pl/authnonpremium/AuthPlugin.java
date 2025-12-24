package pl.authnonpremium;

import org.bukkit.plugin.java.JavaPlugin;

public class AuthPlugin extends JavaPlugin {

    private static AuthPlugin instance;
    private AuthManager authManager;

    @Override
    public void onEnable() {
        instance = this;
        authManager = new AuthManager(this);

        getCommand("register").setExecutor(new RegisterCommand(authManager));
        getCommand("login").setExecutor(new LoginCommand(authManager));

        getServer().getPluginManager().registerEvents(authManager, this);
        getLogger().info("AuthNonPremium uruchomiony!");
    }

    public static AuthPlugin getInstance() {
        return instance;
    }
}
