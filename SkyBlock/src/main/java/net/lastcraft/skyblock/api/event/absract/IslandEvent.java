package net.lastcraft.skyblock.api.event.absract;

import lombok.Getter;
import net.lastcraft.api.event.DEvent;
import net.lastcraft.skyblock.api.island.Island;

@Getter
public abstract class IslandEvent extends DEvent {

    private final Island island;

    protected IslandEvent(Island island, boolean async) {
        super(async);
        this.island = island;
    }

    protected IslandEvent(Island island) {
        this.island = island;
    }
}
