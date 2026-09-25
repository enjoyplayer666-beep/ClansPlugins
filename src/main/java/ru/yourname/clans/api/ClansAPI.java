package ru.yourname.clans.api;

import ru.yourname.clans.Clan;
import ru.yourname.clans.ClansPlugin;

import java.util.UUID;

public class ClansAPI {
    public static String getClanTag(UUID uuid) {
        Clan clan = ClansPlugin.getInstance().getClanManager().getClanByPlayer(uuid);
        return clan == null ? "" : clan.getName();
    }

    public static Clan getClan(UUID uuid) {
        return ClansPlugin.getInstance().getClanManager().getClanByPlayer(uuid);
    }
}
