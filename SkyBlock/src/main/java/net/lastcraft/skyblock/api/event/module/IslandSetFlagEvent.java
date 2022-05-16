package net.lastcraft.skyblock.api.event.module;

import lombok.Getter;
import lombok.Setter;
import net.lastcraft.skyblock.api.event.absract.IslandEvent;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.island.IslandFlag;
import org.bukkit.event.Cancellable;

@Getter
public class IslandSetFlagEvent extends IslandEvent implements Cancellable {

    private final IslandFlag flag;
    private final boolean result;

    @Setter
    private boolean cancelled;

    public IslandSetFlagEvent(Island island, IslandFlag flag, boolean result) {
        super(island);
        this.flag = flag;
        this.result = result;
    }
}
