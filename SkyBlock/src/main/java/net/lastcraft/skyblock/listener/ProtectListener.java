package net.lastcraft.skyblock.listener;

import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.events.StateChangeEvent;
import net.lastcraft.alternate.api.events.UserChangeFlyStatusEvent;
import net.lastcraft.alternate.api.events.UserChangeGodModeEvent;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.skyblock.SkyBlock;
import net.lastcraft.skyblock.api.event.absract.IslandListener;
import net.lastcraft.skyblock.api.island.Island;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ProtectListener extends IslandListener {

    private final List<Material> notInterractMaterial = Arrays.asList(
            Material.CHEST, Material.FURNACE, Material.BURNING_FURNACE, Material.JUKEBOX, Material.TRAPPED_CHEST,
            Material.LAVA_BUCKET, Material.WATER_BUCKET, Material.BUCKET, Material.DRAGON_EGG, Material.ITEM_FRAME,
            Material.FLOWER_POT, Material.DISPENSER, Material.DROPPER, Material.HOPPER, Material.BEACON,
            Material.ARMOR_STAND, Material.BREWING_STAND, Material.SHULKER_SHELL, Material.BLACK_SHULKER_BOX,
            Material.ORANGE_SHULKER_BOX, Material.BLUE_SHULKER_BOX, Material.BROWN_SHULKER_BOX,
            Material.CYAN_SHULKER_BOX, Material.GRAY_SHULKER_BOX, Material.GREEN_SHULKER_BOX,
            Material.LIGHT_BLUE_SHULKER_BOX, Material.LIME_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX,
            Material.SILVER_SHULKER_BOX, Material.WHITE_SHULKER_BOX, Material.RED_SHULKER_BOX,
            Material.YELLOW_SHULKER_BOX, Material.PINK_SHULKER_BOX, Material.PURPLE_SHULKER_BOX
    );

    public ProtectListener(SkyBlock skyBlock) {
        super(skyBlock);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Location location = e.getBlock().getLocation();

        disabled(player, location, e);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        Location location = e.getBlockPlaced().getLocation();

        disabled(player, location, e);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDisableChestUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        Block block = e.getClickedBlock();
        if (block == null)
            return;

        Location location = block.getLocation();

        disabled(player, location, block.getType(), e);
    }

    @EventHandler
    public void onDisableBucket(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Location location = player.getLocation();

        ItemStack item = e.getItem();
        if (item == null)
            return;

        disabled(player, location, item.getType(), e);
    }

    @EventHandler
    public void onToggle(StateChangeEvent e) {
        if (!(e instanceof UserChangeFlyStatusEvent || e instanceof UserChangeGodModeEvent))
            return;

        User user = e.getUser();
        Player player = user.getPlayer();
        if (player == null)
            return;

        Location location = player.getLocation();
        if (!isSkyBlockWorld(location.getWorld()))
            return;


        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island == null)
            return;

        if (island.hasMember(player))
            return;

        gamer.sendMessageLocale("ISLAND_NOT_YOU_COMMAND");
        e.setCancelled(true);
    }

    private void disabled(Player player, Location location, Cancellable event) {
        if (!isSkyBlockWorld(location.getWorld()))
            return;

        Island island = ISLAND_MANAGER.getIsland(location);
        if (!(island != null && island.canBuild(player, location)))
            event.setCancelled(true);
    }

    private void disabled(Player player, Location location, Material material, Cancellable event) {
        if (!isSkyBlockWorld(location.getWorld()))
            return;

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            event.setCancelled(true);
            return;
        }

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island == null)
            return;

        if (island.hasMember(player))
            return;

        if (!notInterractMaterial.contains(material))
            return;

        gamer.sendMessageLocale("ISLAND_NOT_YOU");
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockForm(EntityChangeBlockEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player))
            return;

        Block block = e.getBlock();
        if (block == null || block.getType() != Material.SOIL)
            return;

        Location location = block.getLocation();
        if (!isSkyBlockWorld(location.getWorld()))
            return;

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island == null)
            return;

        Player player = (Player) entity;

        if (island.hasMember(player))
            return;

        e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onArmorStand(PlayerArmorStandManipulateEvent e) {
        Player player = e.getPlayer();
        ArmorStand armorStand = e.getRightClicked();

        if (armorStand == null)
            return;

        Location location = armorStand.getLocation();
        if (!isSkyBlockWorld(location))
            return;

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island == null)
            return;

        if (island.hasMember(player))
            return;

        e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemFrame(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        Entity entity = e.getRightClicked();

        if (entity == null)
            return;

        Location location = entity.getLocation();
        if (!isSkyBlockWorld(location))
            return;

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island == null)
            return;

        if (island.hasMember(player))
            return;

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onArmorStandDamager(EntityDamageByEntityEvent e) { //отключить урон по стойкам
        Entity entity = e.getEntity();
        Entity damager = e.getDamager();

        if (!(entity instanceof ArmorStand) && !(entity instanceof ItemFrame))
            return;

        Location location = entity.getLocation();
        if (!isSkyBlockWorld(location))
            return;

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island == null)
            return;

        Player player = FlagsListener.getDamager(damager, e);
        if (player == null)
            return;

        if (island.hasMember(player))
            return;

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            e.setCancelled(true);
            return;
        }

        gamer.sendMessageLocale("ISLAND_NOT_YOU");
        e.setCancelled(true);
    }

    @EventHandler
    public void onPaintingBreak(HangingBreakByEntityEvent e) { //запретить стрелять в картины и рамки
        Entity entity = e.getRemover();
        Hanging hanging = e.getEntity();

        if (!(hanging instanceof Painting) && !(hanging instanceof ItemFrame))
            return;

        Location location = hanging.getLocation();
        if (!isSkyBlockWorld(location))
            return;

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island == null)
            return;

        Player player = FlagsListener.getDamager(entity, e);
        if (player == null)
            return;

        if (island.hasMember(player))
            return;

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            e.setCancelled(true);
            return;
        }

        gamer.sendMessageLocale("ISLAND_NOT_YOU");
        e.setCancelled(true);
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent e) {
        disable(e.getBlock(), e.getBlocks(), e);
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent e) {
        disable(e.getBlock(), e.getBlocks(), e);
    }

    private void disable(Block piston, List<Block> blocks, Cancellable cancellable) {
        Island island = ISLAND_MANAGER.getIsland(piston.getLocation());
        if (island == null)
            return;

        for (Block block : blocks) {
            if (island.containsLocation(block.getLocation()))
                continue;

            cancellable.setCancelled(true);
            break;
        }
    }

    //todo запретить чарами на ботинки замораживать воду
}
