package ru.yourname.clans;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class ClansExpansion extends PlaceholderExpansion {
    private final ClansPlugin plugin;
    public ClansExpansion(ClansPlugin plugin) { this.plugin = plugin; }

    @Override public @NotNull String getIdentifier() { return "clans"; }
    @Override public @NotNull String getAuthor() { return "YourName"; }
    @Override public @NotNull String getVersion() { return "1.0"; }
    @Override public boolean persist() { return true; }

    @Override
    public String onPlaceholder(OfflinePlayer player, @NotNull String params) {
        Clan clan = plugin.getClanManager().getClanByPlayer(player.getUniqueId());
        if (params.equalsIgnoreCase("tag")) return clan == null ? "" : clan.getName();
        if (params.equalsIgnoreCase("rating")) return clan == null ? "0" : String.valueOf(clan.getRating());
        return "";
    }
}
