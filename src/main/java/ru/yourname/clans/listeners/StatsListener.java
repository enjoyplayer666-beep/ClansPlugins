package ru.yourname.clans.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import ru.yourname.clans.Clan;
import ru.yourname.clans.ClanMember;
import ru.yourname.clans.ClansPlugin;

public class StatsListener implements Listener {
    private final ClansPlugin plugin;

    public StatsListener(ClansPlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null) return;

        Clan victimClan = plugin.getClanManager().getClanByPlayer(victim.getUniqueId());
        Clan killerClan = plugin.getClanManager().getClanByPlayer(killer.getUniqueId());

        if (killerClan != null) {
            ClanMember km = killerClan.getMembersRaw().get(killer.getUniqueId());
            km.setKills(km.getKills() + 1);
            killerClan.setKills(killerClan.getKills() + 1);
            killerClan.setRating(killerClan.getRating() + 10);
        }
        if (victimClan != null) {
            ClanMember vm = victimClan.getMembersRaw().get(victim.getUniqueId());
            vm.setDeaths(vm.getDeaths() + 1);
            victimClan.setDeaths(victimClan.getDeaths() + 1);
        }
        plugin.getClanManager().save();
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;
        Player victim = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        Clan victimClan = plugin.getClanManager().getClanByPlayer(victim.getUniqueId());
        Clan damagerClan = plugin.getClanManager().getClanByPlayer(damager.getUniqueId());
        if (victimClan != null && victimClan == damagerClan && !victimClan.isFriendlyFire()) {
            event.setCancelled(true);
        }
    }
}
