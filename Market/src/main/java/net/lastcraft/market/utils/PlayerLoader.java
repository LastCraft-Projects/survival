package net.lastcraft.market.utils;

import lombok.experimental.UtilityClass;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.base.gamer.IBaseGamer;
import net.lastcraft.base.sql.ConnectionConstants;
import net.lastcraft.base.sql.api.MySqlDatabase;
import net.lastcraft.base.sql.api.query.MysqlQuery;
import net.lastcraft.base.sql.api.query.QuerySymbol;
import net.lastcraft.base.sql.api.table.ColumnType;
import net.lastcraft.base.sql.api.table.TableColumn;
import net.lastcraft.base.sql.api.table.TableConstructor;
import net.lastcraft.market.Market;
import net.lastcraft.market.api.MarketPlayer;
import net.lastcraft.market.player.CraftMarketPlayer;

@UtilityClass
public class PlayerLoader {

    private final String TABLE = "MarketPlayer";
    private final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();
    private final MySqlDatabase MY_SQL_DATABASE = MySqlDatabase.newBuilder()
            .data(Market.getDatabase())
            .host("mysql" + ConnectionConstants.DOMAIN.getValue())
            .password(ConnectionConstants.PASSWORD.getValue())
            .user("root")
            .create();

    public MySqlDatabase getMySqlDatabase() {
        return MY_SQL_DATABASE;
    }

    public MarketPlayer get(String name) {
        IBaseGamer gamer = GAMER_MANAGER.getOrCreate(name);
        if (gamer == null)
            return null;

        int playerID = gamer.getPlayerID();
        return MY_SQL_DATABASE.executeQuery("SELECT * FROM `" + TABLE + "` WHERE `playerID` = ? LIMIT 1;", (rs) -> {
            double money = 0;

            if (rs.next())
                money = rs.getDouble("money");
            else
                MY_SQL_DATABASE.execute(MysqlQuery.insertTo(TABLE)
                        .set("playerID", playerID)
                        .set("money", 0));

            return (MarketPlayer) new CraftMarketPlayer(gamer, money);
        }, playerID);
    }

    public void updateMoney(MarketPlayer marketPlayer, double money) {
        int playerID = marketPlayer.getPlayerID();

        MY_SQL_DATABASE.execute(MysqlQuery.update(TABLE)
                .add("money", money)
                .where("playerID", QuerySymbol.EQUALLY, playerID));
    }

    public void init() {
        new TableConstructor(TABLE,
                new TableColumn("playerID", ColumnType.INT_11).primaryKey(true),
                new TableColumn("money", ColumnType.DOUBLE)
        ).create(MY_SQL_DATABASE);
    }
}
