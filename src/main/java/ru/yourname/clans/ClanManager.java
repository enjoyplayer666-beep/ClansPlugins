package ru.yourname.clans;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ClanManager {
    private final ClansPlugin plugin;
    private final Map<String, Clan> clans = new LinkedHashMap<>();
    private final Map<UUID, String> playerClan = new HashMap<>();
    private final File file;
    private FileConfiguration config;

    public ClanManager(ClansPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "clans.yml");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        load();
    }

    public void load() {
        config = YamlConfiguration.loadConfiguration(file);
        clans.clear();
        playerClan.clear();
        if (config.getConfigurationSection("clans") == null) return;

        for (String key : config.getConfigurationSection("clans").getKeys(false)) {
            String path = "clans." + key;
            String name = config.getString(path + ".name");
            UUID owner = UUID.fromString(config.getString(path + ".owner"));
            Clan clan = new Clan(name, owner);
            clan.setCreatedBy(UUID.fromString(config.getString(path + ".createdBy", owner.toString())));
            clan.setCreatedDate(config.getLong(path + ".createdDate"));
            clan.setJoinType(JoinType.valueOf(config.getString(path + ".joinType", "INVITE")));
            clan.setDescription(config.getString(path + ".description", "Нет описания..."));
            Material mat = Material.matchMaterial(config.getString(path + ".icon", "WHITE_BANNER"));
            clan.setIcon(mat != null ? mat : Material.WHITE_BANNER);
            clan.setRating(config.getInt(path + ".rating", 0));
            clan.setKills(config.getInt(path + ".kills", 0));
            clan.setDeaths(config.getInt(path + ".deaths", 0));
            clan.setFriendlyFire(config.getBoolean(path + ".friendlyFire", false));
            clan.getMembersRaw().clear();

            if (config.getConfigurationSection(path + ".members") != null) {
                for (String uuidStr : config.getConfigurationSection(path + ".members").getKeys(false)) {
                    String mpath = path + ".members." + uuidStr;
                    UUID uuid = UUID.fromString(uuidStr);
                    ClanRole role = ClanRole.valueOf(config.getString(mpath + ".role", "MEMBER"));
                    long joinDate = config.getLong(mpath + ".joinDate");
                    ClanMember member = new ClanMember(uuid, role, joinDate);
                    member.setKills(config.getInt(mpath + ".kills", 0));
                    member.setDeaths(config.getInt(mpath + ".deaths", 0));
                    member.setInvited(config.getInt(mpath + ".invited", 0));
                    member.setKicked(config.getInt(mpath + ".kicked", 0));
                    clan.getMembersRaw().put(uuid, member);
                    playerClan.put(uuid, name.toLowerCase());
                }
            }
            clans.put(name.toLowerCase(), clan);
        }
    }

    public void save() {
        config = new YamlConfiguration();
        for (Clan clan : clans.values()) {
            String path = "clans." + clan.getName().toLowerCase();
            config.set(path + ".name", clan.getName());
            config.set(path + ".owner", clan.getOwner().toString());
            config.set(path + ".createdBy", clan.getCreatedBy().toString());
            config.set(path + ".createdDate", clan.getCreatedDate());
            config.set(path + ".joinType", clan.getJoinType().name());
            config.set(path + ".description", clan.getDescription());
            config.set(path + ".icon", clan.getIcon().name());
            config.set(path + ".rating", clan.getRating());
            config.set(path + ".kills", clan.getKills());
            config.set(path + ".deaths", clan.getDeaths());
            config.set(path + ".friendlyFire", clan.isFriendlyFire());
            for (ClanMember member : clan.getMembersRaw().values()) {
                String mpath = path + ".members." + member.getUuid().toString();
                config.set(mpath + ".role", member.getRole().name());
                config.set(mpath + ".joinDate", member.getJoinDate());
                config.set(mpath + ".kills", member.getKills());
                config.set(mpath + ".deaths", member.getDeaths());
                config.set(mpath + ".invited", member.getInvited());
                config.set(mpath + ".kicked", member.getKicked());
            }
        }
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public 
