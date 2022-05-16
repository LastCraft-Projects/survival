package net.lastcraft.skyblock.api.event;

import lombok.Getter;
import net.lastcraft.skyblock.api.event.absract.IslandEvent;
import net.lastcraft.skyblock.api.island.Island;

public class IslandLevelUpEvent extends IslandEvent {

    @Getter
    private final int level;

    protected IslandLevelUpEvent(Island island, int level) {
        super(island);
        this.level = level;
    }
}
