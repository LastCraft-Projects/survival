package net.lastcraft.skyblock.api.event;

import net.lastcraft.skyblock.api.event.absract.IslandMemberEvent;
import net.lastcraft.skyblock.api.island.Island;

public class IslandRemoveMemberEvent extends IslandMemberEvent {

    public IslandRemoveMemberEvent(Island island, int memberID) {
        super(island, memberID);
    }
}
