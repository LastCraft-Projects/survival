package net.lastcraft.skyblock.api.event.world;

import lombok.Getter;
import net.lastcraft.skyblock.api.event.absract.IslandBlockEvent;
import net.lastcraft.skyblock.api.island.Island;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

@Getter
public class IslandBlockPlaceEvent extends IslandBlockEvent {

    private final Block placedAgainst;
    private final BlockState replacedBlockState;
    private final ItemStack itemInHand;
    private final EquipmentSlot hand;

    public IslandBlockPlaceEvent(Island island, Block block, Player player, Block placedAgainst,
                                 BlockState replacedBlockState, ItemStack itemInHand, EquipmentSlot hand) {
        super(island, block, player);
        this.placedAgainst = placedAgainst;
        this.replacedBlockState = replacedBlockState;
        this.itemInHand = itemInHand;
        this.hand = hand;
    }
}
