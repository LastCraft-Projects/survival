package net.lastcraft.skyblock.listener;

import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import net.lastcraft.skyblock.SkyBlock;
import net.lastcraft.skyblock.api.event.absract.IslandListener;
import net.lastcraft.skyblock.api.event.world.IslandBlockBreakEvent;
import net.lastcraft.skyblock.api.event.world.IslandBlockPlaceEvent;
import net.lastcraft.skyblock.api.island.Island;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class EventRewrite extends IslandListener {

    public EventRewrite(SkyBlock plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Location location = block.getLocation();
        Player player = e.getPlayer();

        if (!isSkyBlockWorld(location))
            return;

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island == null)
            return;

        if (!island.hasMember(player))
            return;

        IslandBlockBreakEvent event = new IslandBlockBreakEvent(island, block, player);
        BukkitUtil.callEvent(event);

        if (!event.isDropItems())
            e.setDropItems(false);

        if (event.isCancelled())
            e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        Location location = block.getLocation();
        Player player = e.getPlayer();

        if (!isSkyBlockWorld(location))
            return;

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island == null)
            return;

        if (!island.hasMember(player))
            return;

        Block against = e.getBlockAgainst();
        BlockState blockState = e.getBlockReplacedState();
        ItemStack itemStack = e.getItemInHand();
        EquipmentSlot slot = e.getHand();

        IslandBlockPlaceEvent event = new IslandBlockPlaceEvent(island, block,
                player, against, blockState, itemStack, slot);
        BukkitUtil.callEvent(event);

        if (event.isCancelled())
            e.setCancelled(true);
    }
}
