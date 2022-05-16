package net.lastcraft.skyblock.listener;

import net.lastcraft.skyblock.SkyBlock;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.event.absract.IslandListener;
import net.lastcraft.skyblock.dependencies.DependManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class ProtectLobbyListener extends IslandListener {

    private final DependManager dependManager;

    public ProtectLobbyListener(SkyBlock skyBlock) {
        super(skyBlock);
        dependManager = skyBlock.getDependManager();

        World world = Bukkit.getWorld("lobby");
        if (world == null)
            return;

        world.setThundering(false);
        world.setGameRuleValue("doTileDrops", "false");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLava(PlayerBucketEmptyEvent e) {
        if (!isLobbyWorld(e.getPlayer()))
            return;

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        if (!isLobbyWorld(e.getBlock().getWorld()))
            return;

        if (dependManager.isMineLocation(e.getBlock()))
            return;
        e.setCancelled(true);
    }

    /*
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPhysic(BlockPhysicsEvent e) {
        if (!isLobbyWorld(e.getBlock().getWorld()))
            return;

        e.setCancelled(true);
    }
    */

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!isLobbyWorld(e.getPlayer()))
            return;

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent e) {
        if (!isLobbyWorld(e.getToBlock().getWorld()))
            return;

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLeavesDecayEvent(LeavesDecayEvent e) {
        if (!isLobbyWorld(e.getBlock().getWorld()))
            return;

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent e) {
        if (!isLobbyWorld(e.getBlock().getWorld()))
            return;

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockSpread(BlockSpreadEvent e) {
        if (!isLobbyWorld(e.getBlock().getWorld()))
            return;

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockSpread(BlockIgniteEvent e) {
        if (!isLobbyWorld(e.getBlock().getWorld()))
            return;

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent e) {
        if (!isLobbyWorld(e.getBlock().getWorld()))
            return;

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFade(BlockFadeEvent e) {
        if (!isLobbyWorld(e.getBlock().getWorld()))
            return;

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onForm(BlockFormEvent e) {
        if (!isLobbyWorld(e.getBlock().getWorld()))
            return;

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWeatherChange(WeatherChangeEvent e) {
        if (!isLobbyWorld(e.getWorld()))
            return;

        e.setCancelled(e.toWeatherState());

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onArmorInterract(PlayerArmorStandManipulateEvent e) {
        if (!isLobbyWorld(e.getPlayer()))
            return;

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpawnMob(EntitySpawnEvent e) {
        Entity entity = e.getEntity();

        if (SkyBlockAPI.getSkyBlockWorldName().equalsIgnoreCase(entity.getWorld().getName()))
            return;

        e.setCancelled(entity instanceof Creature);
    }

    private boolean isLobbyWorld(Entity entity) {
        return isLobbyWorld(entity.getWorld());
    }

    private boolean isLobbyWorld(Location location) {
        return isLobbyWorld(location.getWorld());
    }

    private boolean isLobbyWorld(World world) {
        String name = world.getName();
        return name.equalsIgnoreCase("lobby") || name.equalsIgnoreCase("PvPWorld");
    }
}
