package ru.yourname.clans;

import org.bukkit.plugin.java.JavaPlugin;
import ru.yourname.clans.commands.ClanCommand;
import ru.yourname.clans.gui.MenuListener;
import ru.yourname.clans.listeners.ChatInputListener;
import ru.yourname.clans.listeners.StatsListener;

public class ClansPlugin extends JavaPlugin {

    private static ClansPlugin instance;
    private ClanManager clanManager;
    private InviteManager inviteManager;
    private ChatInputListener chatInputListener;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        clanManager = new ClanManager(this);
        inviteManager = new InviteManager();
        chatInputListener = new ChatInputListener(this);

        getCommand("clan").setExecutor(new ClanCommand(this));
        getServer().getPluginManager().registerEvents(chatInputListener, this);
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getServer().getPluginManager().registerEvents(new StatsListener(this), this);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ClansExpansion(this).register();
            getLogger().info("PlaceholderAPI найден, плейсхолдеры %clans_tag% и %clans_rating% зарегистрированы.");
        }

        getLogger().info("ClansPlugin включен!");
    }

    @Override
    public void onDisable() {
        if (clanManager != null) clanManager.save();
    }

    public static ClansPlugin getInstance() { return instance; }
    public ClanManager getClanManager() { return clanManager; }
    public InviteManager getInviteManager() { return inviteManager; }
    public ChatInputListener getChatInputListener() { return chatInputListener; }
}
