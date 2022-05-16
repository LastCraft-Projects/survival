package net.lastcraft.skyblock.module;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import net.lastcraft.base.gamer.IBaseGamer;
import net.lastcraft.base.sql.api.query.MysqlQuery;
import net.lastcraft.base.sql.api.query.QuerySymbol;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.island.IslandModule;
import net.lastcraft.skyblock.api.island.member.IgnoredPlayer;
import net.lastcraft.skyblock.craftisland.CraftIgnoredPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class IgnoreModule extends IslandModule {

    @Getter
    private final TIntObjectMap<IgnoredPlayer> ignoreList = new TIntObjectHashMap<>();

    public IgnoreModule(Island island) {
        super(island);
    }

    @Override
    public void load(ResultSet rs) throws SQLException {
        int ignored = rs.getInt("ignored");
        int who = rs.getInt("whoBanned");
        Timestamp date = rs.getTimestamp("date");
        ignoreList.put(ignored, new CraftIgnoredPlayer(island, ignored, date.getTime(), who));
    }

    public void removeIgnore(IgnoredPlayer ignoredPlayer) {
        if (ignoredPlayer == null)
            return;

        final int id = island.getIslandID();
        if (id == -1)
            return;

        int playerID = ignoredPlayer.getPlayerID();

        ignoreList.remove(playerID);
        DATABASE.execute(MysqlQuery.deleteFrom("IslandIgnoreList")
                .where("island", QuerySymbol.EQUALLY, id)
                .where("ignored", QuerySymbol.EQUALLY, playerID)
                .limit());
    }

    public void addIgnored(IBaseGamer gamer, IBaseGamer blocked) {
        if (gamer == null || blocked == null)
            return;

        final int id = island.getIslandID();
        if (id == -1)
            return;

        int playerID = gamer.getPlayerID();
        int playerIDBlocked = blocked.getPlayerID();

        ignoreList.put(playerID, new CraftIgnoredPlayer(island, playerID, System.currentTimeMillis(), playerIDBlocked));

        DATABASE.execute(MysqlQuery.insertTo("IslandIgnoreList")
                .set("island", id)
                .set("ignored", playerID)
                .set("date", new Timestamp(System.currentTimeMillis()))
                .set("whoBanned", playerIDBlocked));
    }

    @Override
    public void delete() {
        final int id = island.getIslandID();
        if (id == -1)
            return;

        DATABASE.execute("DELETE FROM `IslandIgnoreList` WHERE `island` = ?;", id);
    }
}
