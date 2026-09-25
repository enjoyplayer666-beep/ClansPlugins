package ru.yourname.clans;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InviteManager {
    private final Map<UUID, String> invites = new HashMap<>();

    public void invite(UUID player, String clanName) { invites.put(player, clanName.toLowerCase()); }
    public String getInvite(UUID player) { return invites.get(player); }
    public void removeInvite(UUID player) { invites.remove(player); }
    public boolean hasInvite(UUID player) { return invites.containsKey(player); }
}
