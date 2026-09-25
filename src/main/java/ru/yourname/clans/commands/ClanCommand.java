package ru.yourname.clans.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.yourname.clans.*;
import ru.yourname.clans.gui.ClanListMenu;
import ru.yourname.clans.gui.MyClanMenu;

import java.util.*;
import java.util.stream.Collectors;

public class ClanCommand implements CommandExecutor, TabCompleter {
    private final ClansPlugin plugin;

    public ClanCommand(ClansPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Только для игроков.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            new ClanListMenu(plugin, player, 0).open();
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "top": {
                int page = 1;
                if (args.length > 1) {
                    try { page = Integer.parseInt(args[1]); } catch (Exception ignored) {}
                }
                sendTop(player, page);
                break;
            }
            case "invite":
                if (args.length < 2) { player.sendMessage(ChatColor.RED + "Используй: /c invite <ник>"); break; }
                invite(player, args[1]);
                break;
            case "accept":
                accept(player);
                break;
            case "decline":
                decline(player);
                break;
            case "kick":
                if (args.length < 2) { player.sendMessage(ChatColor.RED + "Используй: /c kick <ник>"); break; }
                kick(player, args[1]);
                break;
            case "leave":
                leave(player);
                break;
            case "disband":
                disband(player);
                break;
            case "open":
                if (args.length < 2) {
                    Clan clan = plugin.getClanManager().getClanByPlayer(player.getUniqueId());
                    if (clan == null) { player.sendMessage(ChatColor.RED + "Вы не состоите в клане."); break; }
                    new MyClanMenu(plugin, player, clan.getName()).open();
                } else {
                    openOther(player, args[1]);
                }
                break;
            case "pvp":
                toggleFriendlyFire(player);
                break;
            case "broadcast":
                if (args.length < 2) { player.sendMessage(ChatColor.RED + "Используй: /c broadcast <текст>"); break; }
                broadcast(player, String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                break;
            default:
                new ClanListMenu(plugin, player, 0).open();
        }
        return true;
    }

    private void invite(Player sender, String targetName) {
        Clan clan = plugin.getClanManager().getClanByPlayer(sender.getUniqueId());
        if (clan == null) { sender.sendMessage(ChatColor.RED + "Вы не состоите в клане."); return; }
        ClanMember me = clan.getMembersRaw().get(sender.getUniqueId());
        if (me.getRole() == ClanRole.MEMBER) { sender.sendMessage(ChatColor.RED + "Недостаточно прав."); return; }

        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) { sender.sendMessage(ChatColor.RED + "Игрок не найден."); return; }
        if (plugin.getClanManager().getClanByPlayer(target.getUniqueId()) != null) {
            sender.sendMessage(ChatColor.RED + "Игрок уже в клане."); return;
        }

        plugin.getInviteManager().invite(target.getUniqueId(), clan.getName());
        sender.sendMessage(ChatColor.GREEN + "Вы пригласили " + target.getName() + " в клан.");
        target.sendMessage(ChatColor.YELLOW + "Вас пригласили в клан " + clan.getName() + "! §a/c accept §eчтобы принять.");
        me.setInvited(me.getInvited() + 1);
        plugin.getClanManager().save();
    }

    private void accept(Player player) {
        String clanName = plugin.getInviteManager().getInvite(player.getUniqueId());
        if (clanName == null) { player.sendMessage(ChatColor.RED + "У вас нет приглашений."); return; }
        Clan clan = plugin.getClanManager().getClan(clanName);
        if (clan == null) { player.sendMessage(ChatColor.RED + "Клан больше не существует."); return; }
        if (plugin.getClanManager().getClanByPlayer(player.getUniqueId()) != null) {
            player.sendMessage(ChatColor.RED + "Вы уже в клане."); return;
        }

        plugin.getClanManager().addMember(clan, player.getUniqueId());
        plugin.getInviteManager().removeInvite(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Вы вступили в клан " + clan.getName() + "!");
        broadcastToClan(clan, ChatColor.GREEN + player.getName() + " вступил в клан!");
    }

    private void decline(Player player) {
        if (!plugin.getInviteManager().hasInvite(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "У вас нет приглашений."); return;
        }
        plugin.getInviteManager().removeInvite(player.getUniqueId());
        player.sendMessage(ChatColor.YELLOW + "Приглашение отклонено.");
    }

    private void kick(Player sender, String targetName) {
        Clan clan = plugin.getClanManager().getClanByPlayer(sender.getUniqueId());
        if (clan == null) { sender.sendMessage(ChatColor.RED + "Вы не состоите в клане."); return; }
        ClanMember me = clan.getMembersRaw().get(sender.getUniqueId());
        if (me.getRole() == ClanRole.MEMBER) { sender.sendMessage(ChatColor.RED + "Недостаточно прав."); return; }

        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        ClanMember targetMember = clan.getMembersRaw().get(target.getUniqueId());
        if (targetMember == null) { sender.sendMessage(ChatColor.RED + "Игрок не в вашем клане."); return; }
        if (targetMember.getRole() == ClanRole.LEADER) { sender.sendMessage(ChatColor.RED + "Нельзя выгнать лидера."); return; }

        plugin.getClanManager().removeMember(clan, target.getUniqueId());
        me.setKicked(me.getKicked() + 1);
        plugin.getClanManager().save();
        sender.sendMessage(ChatColor.GREEN + "Игрок " + targetName + " выгнан из клана.");
        if (target.isOnline() && target.getPlayer() != null) {
            target.getPlayer().sendMessage(ChatColor.RED + "Вас выгнали из клана.");
        }
    }

    private void leave(Player player) {
        Clan clan = plugin.getClanManager().getClanByPlayer(player.getUniqueId());
        if (clan == null) { player.sendMessage(ChatColor.RED + "Вы не состоите в клане."); return; }
        ClanMember me = clan.getMembersRaw().get(player.getUniqueId());
        if (me.getRole() == ClanRole.LEADER) {
            player.sendMessage(ChatColor.RED + "Лидер не может покинуть клан, используйте /c disband.");
            return;
        }
        plugin.getClanManager().removeMember(clan, player.getUniqueId());
        player.sendMessage(ChatColor.YELLOW + "Вы покинули клан.");
        broadcastToClan(clan, ChatColor.YELLOW + player.getName() + " покинул клан.");
    }

    private void disband(Player player) {
        Clan clan = plugin.getClanManager().getClanByPlayer(player.getUniqueId());
        if (clan == null) { player.sendMessage(ChatColor.RED + "Вы не состоите в клане."); return; }
        if (!clan.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Только владелец может распустить клан.");
            return;
        }
        broadcastToClan(clan, ChatColor.RED + "Клан был распущен лидером.");
        plugin.getClanManager().deleteClan(clan);
        player.sendMessage(ChatColor.GREEN + "Клан распущен.");
    }

    private void openOther(Player player, String clanName) {
        if (!plugin.getClanManager().existsClan(clanName)) {
            player.sendMessage(ChatColor.RED + "Клан не найден."); return;
        }
        new MyClanMenu(plugin, player, clanName).open();
    }

    private void toggleFriendlyFire(Player player) {
        Clan clan = plugin.getClanManager().getClanByPlayer(player.getUniqueId());
        if (clan == null) { player.sendMessage(ChatColor.RED + "Вы не состоите в клане."); return; }
        ClanMember me = clan.getMembersRaw().get(player.getUniqueId());
        if (me.getRole() == ClanRole.MEMBER) { player.sendMessage(ChatColor.RED + "Недостаточно прав."); return; }
        clan.setFriendlyFire(!clan.isFriendlyFire());
        plugin.getClanManager().save();
        player.sendMessage(ChatColor.YELLOW + "Дружественный огонь: " + (clan.isFriendlyFire() ? "§aВКЛ" : "§cВЫКЛ"));
    }

    private void broadcast(Player player, String message) {
        Clan clan = plugin.getClanManager().getClanByPlayer(player.getUniqueId());
        if (clan == null) { player.sendMessage(ChatColor.RED + "Вы не состоите в клане."); return; }
        broadcastToClan(clan, ChatColor.LIGHT_PURPLE + "[Клан] " + player.getName() + ": " + ChatColor.WHITE + message);
    }

    private void broadcastToClan(Clan clan, String message) {
        for (UUID uuid : clan.getMembersRaw().keySet()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) p.sendMessage(message);
        }
    }

    private void sendTop(Player player, int page) {
        List<Clan> top = plugin.getClanManager().getTopClans();
        int perPage = 10;
        int maxPage = Math.max(1, (int) Math.ceil(top.size() / (double) perPage));
        if (page < 1) page = 1;
        if (page > maxPage) page = maxPage;

        player.sendMessage(ChatColor.DARK_PURPLE + "———[Топ кланов]———");
        int start = (page - 1) * perPage;
        for (int i = start; i < Math.min(start + perPage, top.size()); i++) {
            Clan clan = top.get(i);
            OfflinePlayer owner = Bukkit.getOfflinePlayer(clan.getOwner());
            player.sendMessage(ChatColor.GOLD + "" + (i + 1) + ". " + ChatColor.WHITE + clan.getName()
                    + " - " + ChatColor.AQUA + owner.getName()
                    + " " + ChatColor.YELLOW + "[" + clan.getRating() + " КР]");
        }
        if (page < maxPage) {
            player.sendMessage(ChatColor.GRAY + "Следующая страница: /c top " + (page + 1));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> subs = 
