package ru.yourname.clans.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitTask;
import ru.yourname.clans.Clan;
import ru.yourname.clans.ClansPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatInputListener implements Listener {
    private final ClansPlugin plugin;
    private final Map<UUID, PendingAction> pending = new HashMap<>();

    public enum ActionType { CREATE_CLAN, RENAME, DESCRIPTION }

    private static class PendingAction {
        ActionType type;
        BukkitTask timeoutTask;
        PendingAction(ActionType type, BukkitTask task) { this.type = type; this.timeoutTask = task; }
    }

    public ChatInputListener(ClansPlugin plugin) { this.plugin = plugin; }

    public void startCreateClan(Player player) {
        startAction(player, ActionType.CREATE_CLAN, 30);
        player.sendMessage(ChatColor.YELLOW + "Кланы " + ChatColor.GRAY + "› " + ChatColor.WHITE + "Напиши в чат название для нового клана.");
        player.sendMessage(ChatColor.YELLOW + "Кланы " + ChatColor.GRAY + "› " + ChatColor.RED + "Напишите отмена в чат чтобы выйти.");
    }

    public void startAction(Player player, ActionType type, long seconds) {
        UUID uuid = player.getUniqueId();
        if (pending.containsKey(uuid)) pending.get(uuid).timeoutTask.cancel();
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (pending.containsKey(uuid)) {
                pending.remove(uuid);
                player.sendMessage(ChatColor.RED + "Время действия истекло.");
            }
        }, 20L * seconds);
        pending.put(uuid, new PendingAction(type, task));
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!pending.containsKey(uuid)) return;

        event.setCancelled(true);
        String message = event.getMessage().trim();
        PendingAction action = pending.get(uuid);

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (message.equalsIgnoreCase("отмена")) {
                cleanup(uuid, action);
                player.sendMessage(ChatColor.RED + "Действие отменено.");
                return;
            }
            switch (action.type) {
                case CREATE_CLAN: handleCreateClan(player, message, action); break;
                case RENAME: handleRename(player, message, action); break;
                case DESCRIPTION: handleDescription(player, message, action); break;
            }
        });
    }

    private void handleCreateClan(Player player, String name, PendingAction action) {
        if (name.length() < 2 || name.length() > 16) {
            player.sendMessage(ChatColor.RED + "Название должно быть от 2 до 16 символов.");
            return;
        }
        if (plugin.getClanManager().existsClan(name)) {
            player.sendMessage(ChatColor.RED + "Клан с таким названием уже существует.");
            return;
        }
        if (plugin.getClanManager().getClanByPlayer(player.getUniqueId()) != null) {
            player.sendMessage(ChatColor.RED + "Вы уже состоите в клане.");
            cleanup(player.getUniqueId(), action);
            return;
        }
        plugin.getClanManager().createClan(name, player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Клан \"" + name + "\" успешно создан!");
        cleanup(player.getUniqueId(), action);
    }

    private void handleRename(Player player, String newName, PendingAction action) {
        Clan clan = plugin.getClanManager().getClanByPlayer(player.getUniqueId());
        if (clan == null) { player.sendMessage(ChatColor.RED + "Вы не в клане."); cleanup(player.getUniqueId(), action); return; }
        if (newName.length() < 2 || newName.length() > 16) {
            player.sendMessage(ChatColor.RED + "Название 2-16 символов."); return;
        }
        if (plugin.getClanManager().existsClan(newName)) {
            player.sendMessage(ChatColor.RED + "Такое название уже занято."); return;
        }
        plugin.getClanManager().renameClan(clan, newName);
        player.sendMessage(ChatColor.GREEN + "Название клана изменено на " + newName);
        cleanup(player.getUniqueId(), action);
    
