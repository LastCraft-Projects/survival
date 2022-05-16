package net.lastcraft.skyblock.api.event.world;

import lombok.Getter;
import lombok.Setter;
import net.lastcraft.skyblock.api.event.absract.IslandBlockEvent;
import net.lastcraft.skyblock.api.island.Island;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@Getter
@Setter
public class IslandBlockBreakEvent extends IslandBlockEvent {

    private boolean dropItems = true;

    public IslandBlockBreakEvent(Island island, Block block, Player player) {
        super(island, block, player);
    }
}
