package net.lastcraft.skyblock.command;

import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import net.lastcraft.skyblock.api.event.IslandAddMemberEvent;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.island.member.MemberType;
import net.lastcraft.skyblock.craftisland.CraftSkyGamer;
import org.bukkit.entity.Player;

public final class AcceptCommand extends RequestCommand {

    public AcceptCommand() {
        super("accept", "принять", "request");
    }

    @Override
    void accept(Player sender, Player who) {
        //who приглашает
        //sender принимает
        Island island = ISLAND_MANAGER.getIsland(who);
        if (island == null) {
            return;
        }

        CraftSkyGamer senderUser = (CraftSkyGamer) MANAGER.getSkyGamer(sender);
        BukkitGamer senderGamer = GAMER_MANAGER.getGamer(sender);
        BukkitGamer gamerWho = GAMER_MANAGER.getGamer(who);

        IslandAddMemberEvent event = new IslandAddMemberEvent(island, senderGamer.getPlayerID());
        BukkitUtil.callEvent(event);

        if (event.isCancelled())
            return;

        sendMessage(senderGamer, "ISLAND_ACCEPT",
                who.getDisplayName(), island.getOwner().getDisplayName());
        sendMessage(gamerWho, "ISLAND_ACCEPT_WHO",
                sender.getDisplayName());

        senderUser.getRequests().remove(who.getName());

        island.addPlayerToIsland(sender, MemberType.MEMBER);
    }
}
