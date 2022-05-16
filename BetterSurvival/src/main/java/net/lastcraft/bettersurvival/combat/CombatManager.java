package net.lastcraft.bettersurvival.combat;

import com.google.common.collect.ImmutableSet;
import gnu.trove.impl.sync.TSynchronizedIntObjectMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.lastcraft.alternate.Alternate;
import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.events.UserTeleportByCommandEvent;
import net.lastcraft.alternate.api.manager.UserManager;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.bettersurvival.BetterSurvival;
import net.lastcraft.dartaapi.listeners.DListener;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class CombatManager extends DListener<BetterSurvival> {

    private final ImmutableSet<String> blockedCommands = ImmutableSet.<String>builder()
            .add("spawn", "warp", "home", "rtp", "randomteleport", "randomtp", "call", "tpaccept", "is", "island")
            .build();
    private final ImmutableSet<String> blockedWorlds = ImmutableSet.<String>builder()
            .add("lobby")
            .build();

    private final UserManager userManager = AlternateAPI.getUserManager();

    private final TIntObjectMap<CombatData> idToCombatData = new TSynchronizedIntObjectMap<>(new TIntObjectHashMap<>());

    public CombatManager(BetterSurvival javaPlugin) {
        super(javaPlugin);
    }

    private CombatData getCombatData(Player player) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return null;
        }

        CombatData combatData = idToCombatData.get(gamer.getPlayerID());

        if (combatData == null) {
            combatData = new CombatData(player);
            idToCombatData.put(gamer.getPlayerID(), combatData);
        }

        return combatData;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CombatData combatData = getCombatData(player);
        if (combatData == null)
            return;

        if (combatData.isInPvp()) {
            player.damage(10000);
        }
    }

    @EventHandler
    public void onTeleport(UserTeleportByCommandEvent event) {
        Player player = event.getUser().getPlayer();
        if (player == null) {
            return;
        }
        CombatData combatData = getCombatData(player);
        if (combatData == null) {
            return;
        }

        if (combatData.isInPvp()) {
            player.sendMessage(Alternate.getConfigData().getPrefix() + "§fТелепортации во время PVP запрещены!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().substring(1);
        CombatData combatData = getCombatData(event.getPlayer());
        if (combatData == null) {
            return;
        }

        if (blockedCommands.contains(command.toLowerCase()) && combatData.isInPvp()) {
            event.getPlayer().sendMessage(Alternate.getConfigData().getPrefix() + "§fНельзя использовать эту команду во время PVP!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        Entity entity = event.getEntity();
        if (blockedWorlds.contains(entity.getWorld().getName())) {
            return;
        }

        Player player = (Player) entity;
        User user = userManager.getUser(player);
        if (user == null || user.isGod()) {
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK ||
                event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {

            Player damagerPlayer = getDamager(((EntityDamageByEntityEvent) event).getDamager());
            if (damagerPlayer == null) {
                return;
            }

            CombatData damagerData = getCombatData(damagerPlayer);
            if (damagerData != null) {
                damagerData.handleCombat(true, javaPlugin);
            }

            CombatData combatData = getCombatData(player);
            if (combatData == null) {
                return;
            }
            combatData.setLastDamager(damagerPlayer);
            combatData.handleCombat(false, javaPlugin);
        }
    }


    private static Player getDamager(Entity entity) {
        if (entity instanceof Player) {
            return (Player) entity;
        }
        Entity damager = getDamagerEntity(entity);
        if (damager == null) {
            return null;
        }
        if (damager instanceof Player) {
            return (Player) damager;
        }
        return null;
    }

    private static Entity getDamagerEntity(Entity entity) {
        if (entity instanceof Projectile) {
            Projectile projectile = (Projectile) entity;
            return (projectile.getShooter() instanceof Entity) ? (Entity) projectile.getShooter() : null;
        }
        if (entity instanceof AreaEffectCloud) {
            AreaEffectCloud effectCloud = (AreaEffectCloud) entity;
            return (effectCloud.getSource() instanceof Entity) ? (Entity) effectCloud.getSource() : null;
        }
        return entity;
    }

}
