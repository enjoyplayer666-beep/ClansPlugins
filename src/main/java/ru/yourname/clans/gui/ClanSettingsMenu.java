package ru.yourname.clans.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import ru.yourname.clans.Clan;
import ru.yourname.clans.ClansPlugin;
import ru.yourname.clans.JoinType;

public class ClanSettingsMenu {
    private final ClansPlugin plugin;
    private final Player viewer;
    private final String clanName;

    public ClanSettingsMenu(ClansPlugin plugin, Player viewer, String clanName) {
        this.plugin = plugin;
        this.viewer = viewer;
        this.clanName = clanName;
    }

    public void open() {
        Clan clan = plugin.getClanManager().getClan(clanName);
        if (clan == null) return;

        ClanSettingsHolder holder = new ClanSettingsHolder(clanName);
        Inventory inv = Bukkit.createInventory(holder, 45, ChatColor.DARK_GRAY + "• Клан, настройки");
        holder.setInventory(inv);

        inv.setItem(11, ClanListMenu.namedItem(Material.NAME_TAG, ChatColor.LIGHT_PURPLE + "Изменить название"));
        inv.setItem(13, ClanListMenu.namedItem(clan.getIcon(), ChatColor.LIGHT_PURPLE + "Изменить иконку",
                ChatColor.GRAY + "Нажми по предмету инвентаря."));
        inv.setItem(15, ClanListMenu.namedItem(Material.WRITABLE_BOOK, ChatColor.LIGHT_PURPLE + "Изменить описание"));
        inv.setItem(29, ClanListMenu.namedItem(Material.IRON_DOOR, ChatColor.LIGHT_PURPLE + "Тип вступления",
                ChatColor.GRAY + "Текущий: " + (clan.getJoinType() == JoinType.INVITE ? "По приглашению" : "Свободный")));
        inv.setItem(31, ClanListMenu.namedItem(Material.SHIELD, ChatColor.LIGHT_PURPLE + "Дружественный огонь",
                ChatColor.GRAY + "Статус: " + (clan.isFriendlyFire() ? "§aВКЛ" : "§cВЫКЛ")));
        inv.setItem(33, ClanListMenu.namedItem(Material.BARRIER, ChatColor.RED + "Распустить клан"));
        inv.setItem(40, ClanListMenu.namedItem(Material.ARROW, ChatColor.YELLOW + "Назад"));

        viewer.openInventory(inv);
    }
}
