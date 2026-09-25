package ru.yourname.clans;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class Clan {
    private String name;
    private UUID owner;
    private UUID createdBy;
    private long createdDate;
    private JoinType joinType = JoinType.INVITE;
    private String description = "Нет описания...";
    private Material icon = Material.WHITE_BANNER;
    private int rating = 0;
    private int kills = 0;
    private int deaths = 0;
    private boolean friendlyFire = false;

    private final Map<UUID, ClanMember> members = new LinkedHashMap<>();

    public Clan(String name, UUID owner) {
        this.name = name;
        this.owner = owner;
        this.createdBy = owner;
        this.createdDate = System.currentTimeMillis();
        members.put(owner, new ClanMember(owner, ClanRole.LEADER, System.currentTimeMillis()));
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public UUID getOwner() { return owner; }
    public void setOwner(UUID owner) { this.owner = owner; }

    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }

    public long getCreatedDate() { return createdDate; }
    public void setCreatedDate(long createdDate) { this.createdDate = createdDate; }

    public JoinType getJoinType() { return joinType; }
    public void setJoinType(JoinType joinType) { this.joinType = joinType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Material getIcon() { return icon; }
    public void setIcon(Material icon) { this.icon = icon; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public int getKills() { return kills; }
    public void setKills(int kills) { this.kills = kills; }

    public int getDeaths() { return deaths; }
    public void setDeaths(int deaths) { this.deaths = deaths; }

    public boolean isFriendlyFire() { return friendlyFire; }
    public void setFriendlyFire(boolean friendlyFire) { this.friendlyFire = friendlyFire; }

    public Map<UUID, ClanMember> getMembersRaw() { return members; }

    public String getStatus() {
        if (rating >= 10000) return "Легенды";
        if (rating >= 6000) return "Неудержимые";
        if (rating >= 3000) return "Профи";
        if (rating >= 1000) return "Опытные";
        return "Новички";
    }

    public ChatColor getDisplayColor() {
        return ChatColor.WHITE;
    }
}
