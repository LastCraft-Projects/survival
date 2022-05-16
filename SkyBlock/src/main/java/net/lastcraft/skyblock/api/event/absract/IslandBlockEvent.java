package net.lastcraft.skyblock.api.event.absract;

import lombok.Getter;
import net.lastcraft.skyblock.api.island.Island;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@Getter
public abstract class IslandBlockEvent extends IslandEvent implements Cancellable {

    private final Block block;
    private final Player player;
    private boolean cancel;

    protected IslandBlockEvent(Island island, Block block, Player player) {
        super(island, false);
        this.block = block;
        this.player = player;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
