package net.lastcraft.skyblock.api.event.world;

import lombok.Getter;
import net.lastcraft.api.event.player.PlayerEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class MineBlockBreakEvent extends PlayerEvent implements Cancellable {

    @Getter
    private final Block block;
    private boolean cancel = false;

    public MineBlockBreakEvent(Player player, Block block) {
        super(player);
        this.block = block;
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
