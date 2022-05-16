package net.lastcraft.skyblock.api.event.absract;

import net.lastcraft.skyblock.api.island.Island;
import org.bukkit.event.Cancellable;

public abstract class IslandMemberEvent extends IslandEvent implements Cancellable {

    private final int memberID;
    private boolean cancel;

    protected IslandMemberEvent(Island island, int memberID) {
        super(island);
        this.memberID = memberID;
    }

    public int getMemberID() {
        return memberID;
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
