package ru.yourname.clans.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import ru.yourname.clans.Clan;
import ru.yourname.clans.ClanMember;
import ru.yourname.clans.ClanRole;
import ru.yourname.clans.ClansPlugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyClanMenu {
    private final ClansPlugin plugin;
    private final Player viewer;
    private final String clanName;

    public MyClanMenu(ClansPlugin plugin, Player viewer, String clanName) {
        this.plugin = plugin;
        this.viewer = viewer;
        this.clanName = clanName;
    }

    public void open() {
        Clan clan = plugin.getClanManager().getClan(clanName);
        if (clan == null) { viewer.sendMessage(ChatColor.RED + "Клан не найден."); return; }

        MyClanHolder holder = new MyClanHolder(clanName);
        Inventory inv = Bukkit.createInventory(holder, 54, ChatColor.DARK_GRAY + "• Клан, игроки: " + clan.getMembersRaw().size());
        holder.setInventory(inv);

        int slot = 0;
        for (ClanMember member : clan.getMembersRaw().values()) {
            if (slot >= 45) break;
            inv.setItem(slot++, buildMemberIcon(member));
        }

        ClanMember me = clan.getMembersRaw().get(viewer.getUniqueId());
        if (me != null && (me.getRole() == ClanRole.LEADER || me.getRole() == ClanRole.OFFICER)) {
            inv.setItem(49, ClanListMenu.namedItem(Material.ANVIL, ChatColor.LIGHT_PURPLE + "Настройки клана"));
        }
        inv.setItem(53, ClanListMenu.namedItem(Material.ARROW, ChatColor.YELLOW + "Назад"));

        viewer.openInventory(inv);
    }

    private ItemStack buildMemberIcon(ClanMember member) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(member.getUuid());
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(op);

        String roleColor = member.getRole() == ClanRole.LEADER ? "§d" : member.getRole() == ClanRole.OFFICER ? "§b" : "§7";
        String status = op.isOnline() ? "§a[Онлайн]" : "§7[Оффлайн]";
        meta.setDisplayName(roleColor + "[" + roleName(member.getRole()) + "] " + op.getName() + " 
