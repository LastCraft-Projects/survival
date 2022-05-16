package net.lastcraft.skyblock.api.event;

import net.lastcraft.skyblock.api.event.absract.IslandEvent;
import net.lastcraft.skyblock.api.island.Island;
import org.bukkit.event.Cancellable;

public class IslandAsyncResetEvent extends IslandEvent implements Cancellable {

    private boolean cancel;

    public IslandAsyncResetEvent(Island island) {
        super(island, true);
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean flag) {
        this.cancel = flag;
    }
}
