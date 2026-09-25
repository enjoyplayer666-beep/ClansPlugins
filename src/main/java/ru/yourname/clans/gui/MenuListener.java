package ru.yourname.clans.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import ru.yourname.clans.Clan;
import ru.yourname.clans.ClansPlugin;
import ru.yourname.clans.JoinType;
import ru.yourname.clans.listeners.ChatInputListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuListener implements Listener {
    private final ClansPlugin plugin;
    private final Map<UUID, Boolean> selectingIcon = new HashMap<>();

    public MenuListener(ClansPlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder topHolder = event.getView().getTopInventory().getHolder();
        Player player = (Player) event.getWhoClicked();

        if (topHolder instanceof ClanListHolder) {
            event.setCancelled(true);
            handleClanList(event, player, (ClanListHolder) topHolder);
        } else if (topHolder instanceof MyClanHolder) {
            event.setCancelled(true);
            handleMyClan(event, player, (MyClanHolder) topHolder);
        } else if (topHolder instanceof ClanSettingsHolder) {
            handleSettings(event, player, (ClanSettingsHolder) topHolder);
        }
    }

    private void handleClanList(InventoryClickEvent e, Player player, ClanListHolder holder) {
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        int slot = e.getRawSlot();

        if (slot == 45) { new ClanListMenu(plugin, player, holder.getPage() - 1).open(); return; }
        if (slot == 53) { new ClanListMenu(plugin, player, holder.getPage() + 1).open(); return; }
        if (slot == 49) {
            Clan myClan = plugin.getClanManager().getClanByPlayer(player.getUniqueId());
            if (myClan != null) {
                new MyClanMenu(plugin, player, myClan.getName()).open();
            } else {
                player.closeInventory();
                plugin.getChatInputListener().startCreateClan(player);
            }
            return;
        }

        ItemMeta meta = clicked.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            String clanName = ChatColor.stripColor(meta.getDisplayName());
            Clan clan = plugin.getClanManager().getClan(clanName);
            if (clan != null) new MyClanMenu(plugin, player, clan.getName()).open();
        }
    }

    private void handleMyClan(InventoryClickEvent e, Player player, MyClanHolder holder) {
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null) return;
        int slot = e.getRawSlot();

        if (slot == 53) { new ClanListMenu(plugin, player, 0).open(); return; }
        if (slot == 49) { new ClanSettingsMenu(plugin, player, holder.getClanName()).open(); return; }

        if (clicked.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) clicked.getItemMeta();
            if (meta.getOwningPlayer() != null) {
                String targetName = meta.getOwningPlayer().getName();
                player.closeInventory();
                player.sendMessage(ChatColor.YELLOW + "Используй /c kick " + targetName + " чтобы выгнать игрока.");
            }
        }
    }

    private void handleSettings(InventoryClickEvent e, Player player, ClanSettingsHolder holder) {
        Clan clan = plugin.getClanManager().getClan(holder.getClanName());
        if (clan == null) return;

        boolean clickedTop = e.getClickedInventory() != null && e.getClickedInventory().equals(e.getView().getTopInventory());
        if (e.isShiftClick()) e.setCancelled(true);

        if (clickedTop) {
            e.setCancelled(true);
            ItemStack clicked = e.getCurrentItem();
            if (clicked == null || clicked.getItemMeta() == null || !clicked.getItemMeta().hasDisplayName()) return;
            String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

            switch (name) {
                case "Изменить иконку":
                    selectingIcon.put(player.getUniqueId(), true);
                    player.sendMessage(ChatColor.YELLOW + "Кликни по предмету в своём инвентаре, чтобы установить иконку клана.");
                    break;
                case "Изменить название":
                    player.closeInventory();
                    plugin.getChatInputListener().startAction(player, ChatInputListener.ActionType.RENAME, 30);
                    player.sendMessage(ChatColor.YELLOW + "Напишите новое название клана в чат.");
                    break;
                case "Изменить описание":
                    player.closeInventory();
                    plugin.getChatInputListener().startAction(player, ChatInputListener.ActionType.DESCRIPTION, 60);
                    player.sendMessage(ChatColor.YELLOW + "Напишите новое описание клана в чат.");
                    break;
                case "Тип вступления":
                    clan.setJoinType(clan.getJoinType() == JoinType.INVITE ? JoinType.FREE : JoinType.INVITE);
                    plugin.getClanManager().save();
                    new ClanSettingsMenu(plugin, player, clan.getName()).open();
                    break;
                case "Дружественный огонь":
                    clan.setFriendlyFire(!clan.isFriendlyFire());
                    plugin.getClanManager().save();
                    new ClanSettingsMenu(plugin, player, clan.getName()).open();
                    break;
                case "Распустить клан":
                    plugin.getClanManager().deleteClan(clan);
                    player.sendMessage(ChatColor.RED + "Клан распущен.");
                    player.closeInventory();
                    break;
                case "Назад":
                    new MyClanMenu(plugin, player, clan.getName()).open();
                    break;
            }
        } else {
            if (selectingIcon.getOrDefault(player.getUniqueId(), false)) {
                e.setCancelled(true);
                ItemStack clickedItem = e.getCurrentItem();
                if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                    clan.setIcon(clickedItem.getType());
                    plugin.getClanManager().save();
                    selectingIcon.put(player.getUniqueId(), false);
                    player.sendMessage(ChatColor.GREEN + "Иконка клана изменена на " + clickedItem.getType().name());
                    new ClanSettingsMenu(plugin, player, clan.getName()).open();
                }
            }
        }
    }
}
