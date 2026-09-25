package ru.yourname.clans.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.yourname.clans.Clan;
import ru.yourname.clans.ClansPlugin;
import ru.yourname.clans.JoinType;

import java.text.SimpleDateFormat;
import java.util.*;

public class ClanListMenu {
    private final ClansPlugin plugin;
    private final Player viewer;
    private final int page;
    private static final int PER_PAGE = 28;
    private static final int[] SLOTS = {
        10,11,12,13,14,15,16,
        19,20,21,22,23,24,25,
        28,29,30,31,32,33,34,
        37,38,39,40,41,42,43
    };

    public ClanListMenu(ClansPlugin plugin, Player viewer, int page) {
        this.plugin = plugin;
        this.viewer = viewer;
        this.page = page;
    }

    public void open() {
        List<Clan> clans = new ArrayList<>(plugin.getClanManager().getAllClans());
        int maxPage = Math.max(1, (int) Math.ceil(clans.size() / (double) PER_PAGE));

        ClanListHolder holder = new ClanListHolder(page);
        Inventory inv = Bukkit.createInventory(holder, 54, ChatColor.DARK_GRAY + "• Кланы, страница: " + (page + 1) + "/" + maxPage);
        holder.setInventory(inv);

        ItemStack border = namedItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) inv.setItem(i, border);
        for (int i = 45; i < 54; i++) inv.setItem(i, border);
        for (int row = 1; row < 5; row++) { inv.setItem(row * 9, border); inv.setItem(row * 9 + 8, border); }

        int start = page * PER_PAGE;
        for (int i = 0; i < PER_PAGE && (start + i) < clans.size(); i++) {
            inv.setItem(SLOTS[i], buildClanIcon(clans.get(start + i)));
        }

        Clan myClan = plugin.getClanManager().getClanByPlayer(viewer.getUniqueId());
        if (myClan != null) {
            inv.setItem(49, namedItem(Material.NETHER_STAR, ChatColor.LIGHT_PURPLE + "Мой клан",
                    ChatColor.GRAY + "Просмотр своего клана."));
        } else {
            inv.setItem(49, namedItem(Material.NAME_TAG, ChatColor.LIGHT_PURPLE + "Создать свой клан",
                    ChatColor.GRAY + "Нажми если хочешь клан."));
        }

        if (page > 0) inv.setItem(45, namedItem(Material.ARROW, ChatColor.YELLOW + "Назад"));
        if (page < maxPage - 1) inv.setItem(53, namedItem(Material.ARROW, ChatColor.YELLOW + "Вперёд"));

        viewer.openInventory(inv);
    }

    private ItemStack buildClanIcon(Clan clan) {
        ItemStack item = new ItemStack(clan.getIcon());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(clan.getDisplayColor() + clan.getName());

        List<String> lore = new ArrayList<>();
        OfflinePlayer creator = Bukkit.getOfflinePlayer(clan.getCreatedBy());
        OfflinePlayer owner = Bukkit.getOfflinePlayer(clan.getOwner());
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy, HH:mm");

        lore.add(ChatColor.GRAY + "Создал " + ChatColor.WHITE + creator.getName() + ChatColor.GRAY + ", дата " + sdf.format(new 
