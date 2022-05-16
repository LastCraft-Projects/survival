package net.lastcraft.skyblock.api.event;

import net.lastcraft.skyblock.api.event.absract.IslandMemberEvent;
import net.lastcraft.skyblock.api.island.Island;

public class IslandAddMemberEvent extends IslandMemberEvent {

    public IslandAddMemberEvent(Island island, int memberID) {
        super(island, memberID);
    }
}
