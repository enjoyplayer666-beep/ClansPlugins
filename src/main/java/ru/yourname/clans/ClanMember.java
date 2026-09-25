package ru.yourname.clans;

import java.util.UUID;

public class ClanMember {
    private final UUID uuid;
    private ClanRole role;
    private final long joinDate;
    private int kills = 0;
    private int deaths = 0;
    private int invited = 0;
    private int kicked = 0;

    public ClanMember(UUID uuid, ClanRole role, long joinDate) {
        this.uuid = uuid;
        this.role = role;
        this.joinDate = joinDate;
    }

    public UUID getUuid() { return uuid; }
    public ClanRole getRole() { return role; }
    public void setRole(ClanRole role) { this.role = role; }
    public long getJoinDate() { return joinDate; }
    public int getKills() { return kills; }
    public void setKills(int kills) { this.kills = kills; }
    public int getDeaths() { return deaths; }
    public void setDeaths(int deaths) { this.deaths = deaths; }
    public int getInvited() { return invited; }
    public void setInvited(int invited) { this.invited = invited; }
    public int getKicked() { return kicked; }
    public void setKicked(int kicked) { this.kicked = kicked; }
}
