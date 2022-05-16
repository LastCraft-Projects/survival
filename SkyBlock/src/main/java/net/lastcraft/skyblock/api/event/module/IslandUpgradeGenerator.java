package net.lastcraft.skyblock.api.event.module;

import lombok.Getter;
import lombok.Setter;
import net.lastcraft.skyblock.api.event.absract.IslandEvent;
import net.lastcraft.skyblock.api.island.Island;
import org.bukkit.event.Cancellable;

@Getter
@Setter
public class IslandUpgradeGenerator extends IslandEvent implements Cancellable {

    private boolean cancelled;

    public IslandUpgradeGenerator(Island island) {
        super(island);
    }
}
