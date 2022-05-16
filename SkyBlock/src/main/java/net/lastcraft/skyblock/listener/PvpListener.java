package net.lastcraft.skyblock.listener;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.Warp;
import net.lastcraft.alternate.api.events.UserChangeFlyStatusEvent;
import net.lastcraft.alternate.api.events.UserChangeGodModeEvent;
import net.lastcraft.alternate.api.events.UserEvent;
import net.lastcraft.alternate.api.events.UserTeleportByCommandEvent;
import net.lastcraft.alternate.api.manager.UserManager;
import net.lastcraft.alternate.object.CraftWarp;
import net.lastcraft.dartaapi.listeners.DListener;
import net.lastcraft.packetlib.nms.NmsAPI;
import net.lastcraft.packetlib.nms.interfaces.NmsManager;
import net.lastcraft.skyblock.SkyBlock;
import net.lastcraft.skyblock.utils.FaweUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PvpListener extends DListener<SkyBlock> {

    private final String warpPvPName = "pvp";

    private final NmsManager nmsManager = NmsAPI.getManager();
    private final UserManager userManager = AlternateAPI.getUserManager();

    private final World pvpWorld;
    private final CuboidRegion region;

    public PvpListener(SkyBlock javaPlugin, World pvpWorld) {
        super(javaPlugin);

        this.pvpWorld = pvpWorld;

        pvpWorld.setThundering(false);
        pvpWorld.setGameRuleValue("doTileDrops", "false");

        Warp warp = AlternateAPI.getWarpManager().getWarp(warpPvPName);
        if (warp == null) {
            warp = new CraftWarp(warpPvPName, 1, new Location(pvpWorld, 0, 68, -1), false);
            AlternateAPI.getWarpManager().addWarp(warp);
        }

        Location up = new Location(pvpWorld, -6, 65, 4);
        Location down = new Location(pvpWorld, 6, 80, -8);
        region = FaweUtils.getRegion(up, down);
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectile(ProjectileHitEvent e) { //запретить удочкой по мобу
        Projectile projectile = e.getEntity();
        Entity entity = e.getHitEntity();
        if (entity == null || !(projectile.getShooter() instanceof Player)) {
            return;
        }

        if (!isLobbyWorld(entity) && !isSaveZona(entity)) {
            return;
        }

        projectile.remove();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFoodChange(FoodLevelChangeEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player))
            return;

        if (!isLobbyWorld(entity) && !isSaveZona(entity)) {
            return;
        }

        Player player = (Player) e.getEntity();
        e.setCancelled(player.getFoodLevel() >= e.getFoodLevel());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;

        Player player = (Player) e.getEntity();
        if (!isLobbyWorld(player) && !isSaveZona(player)) {
            return;
        }

        if (e.getCause() == EntityDamageEvent.DamageCause.FIRE ||
                e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK)
            nmsManager.disableFire(player);

        e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onChangeWorld(PlayerChangedWorldEvent e) { //при смене мира офаем то, что нужно офнуть
        Player player = e.getPlayer();

        if (pvpWorld != null && player.getLocation().getWorld().getName().equalsIgnoreCase(pvpWorld.getName())) {
            User user = userManager.getUser(player);
            if (user == null) {
                return;
            }

            user.setGod(false, true);
            user.setFly(false, true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onChangeGod(UserChangeGodModeEvent e) {
        onDisable(e);
    }

    @EventHandler(ignoreCancelled = true)
    public void onChangeFly(UserChangeFlyStatusEvent e) {
        onDisable(e);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleportEvent(UserTeleportByCommandEvent e) {
        switch (e.getCommand()) {
            case TPPOS:
            case CHUNK:
            case TOP:
            case JUMP:
                onDisable(e);
                break;
        }

    }

    private void onDisable(UserEvent e) {
        User user = e.getUser();
        Player player = user.getPlayer();
        if (player == null) {
            return;
        }

        if (pvpWorld != null && player.getLocation().getWorld().getName().equalsIgnoreCase(pvpWorld.getName())) {
            e.setCancelled(true);
        }
    }

    private boolean isSaveZona(Entity entity) {
        Location location = entity.getLocation();
        Vector vector = new Vector(location.getX(), location.getY(), location.getZ());
        return region != null && region.contains(vector);
    }

    private boolean isLobbyWorld(Entity entity) {
        return entity.getWorld().getName().equalsIgnoreCase("lobby");
    }
}
