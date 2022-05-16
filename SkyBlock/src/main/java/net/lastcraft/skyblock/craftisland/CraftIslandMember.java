package net.lastcraft.skyblock.craftisland;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.base.gamer.IBaseGamer;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.island.member.IslandMember;
import net.lastcraft.skyblock.api.island.member.MemberType;
import org.bukkit.entity.Player;

import java.util.Date;

public class CraftIslandMember implements IslandMember {

    private static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();

    private final Island island;
    private final int playerID;
    private final Date date; //дата принятия заявки

    private MemberType memberType;

    public CraftIslandMember(Island island, int playerID, MemberType memberType, long date) {
        this.island = island;
        this.playerID = playerID;
        this.memberType = memberType;
        this.date = new Date(date);
    }

    @Override
    public String getName() {
        return getGamer().getName();
    }

    @Override
    public int getPlayerID() {
        return playerID;
    }

    @Override
    public MemberType getType() {
        return memberType;
    }

    @Override
    public void setMemberType(MemberType memberType) {
        if (this.memberType == memberType)
            return;

        this.memberType = memberType;
        island.changeMemberType(this, memberType);
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public IBaseGamer getGamer() {
        return GAMER_MANAGER.getOrCreate(playerID);
    }

    @Override
    public boolean isOnline() {
        return getGamer().isOnline();
    }

    @Override
    public Player getPlayer() {
        Player player = null;

        if (isOnline()) {
            BukkitGamer bukkitGamer = (BukkitGamer) getGamer();
            player = bukkitGamer.getPlayer();
        }

        return player;
    }

    @Override
    public Island getIsland() {
        return island;
    }

    @Override
    public void remove() {
        island.removePlayerFromIsland(playerID);
    }
}
