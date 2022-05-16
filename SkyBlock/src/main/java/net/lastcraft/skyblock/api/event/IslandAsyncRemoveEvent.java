package net.lastcraft.skyblock.api.event;

import net.lastcraft.skyblock.api.event.absract.IslandEvent;
import net.lastcraft.skyblock.api.island.Island;

public class IslandAsyncRemoveEvent extends IslandEvent{

    public IslandAsyncRemoveEvent(Island island) {
        super(island, true);
    }
}
