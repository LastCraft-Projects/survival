package net.lastcraft.skyblock.craftisland;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.base.gamer.IBaseGamer;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.island.member.IgnoredPlayer;
import net.lastcraft.skyblock.module.IgnoreModule;
import org.bukkit.entity.Player;

import java.util.Date;

public class CraftIgnoredPlayer implements IgnoredPlayer {

    private static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();

    private final Island island;
    private final int playerID;
    private final int playerIDBloced; //кто добавил в блок
    private final Date date; //дата добавления в блок

    public CraftIgnoredPlayer(Island island, int playerID, long date, int playerIDBloced) {
        this.island = island;
        this.playerID = playerID;
        this.date = new Date(date);
        this.playerIDBloced = playerIDBloced;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getPlayerID() {
        return playerID;
    }

    @Override
    public Island getIsland() {
        return island;
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
        BukkitGamer gamer = GAMER_MANAGER.getGamer(playerID);
        if (gamer != null)
            return gamer.getPlayer();
        return null;
    }

    @Override
    public IBaseGamer getBlockedPlayer() {
        return GAMER_MANAGER.getOrCreate(playerIDBloced);
    }

    @Override
    public void removeFromIgnoreList() {
        if (island == null) {
            return;
        }

        IgnoreModule ignoreModule = island.getModule(IgnoreModule.class);
        if (ignoreModule == null) {
            return;
        }

        ignoreModule.removeIgnore(this);
    }
}
