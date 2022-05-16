package net.lastcraft.siteshop;

import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.sql.ConnectionConstants;
import net.lastcraft.base.sql.api.MySqlDatabase;
import net.lastcraft.base.sql.api.query.MysqlQuery;
import net.lastcraft.base.sql.api.query.QuerySymbol;
import net.lastcraft.base.sql.api.table.ColumnType;
import net.lastcraft.base.sql.api.table.TableColumn;
import net.lastcraft.base.sql.api.table.TableConstructor;
import net.lastcraft.siteshop.item.PlayerSSItem;
import net.lastcraft.siteshop.item.SSItem;
import net.lastcraft.siteshop.item.SSItemManager;

import java.util.ArrayList;
import java.util.List;

public final class ItemsLoader {

    private final MySqlDatabase database;

    public ItemsLoader(String dataBase) {
        database = MySqlDatabase.newBuilder()
                .user("root")
                .host("s1" + ConnectionConstants.DOMAIN.getValue())
                .password(ConnectionConstants.PASSWORD.getValue())
                .data(dataBase)
                .create();
        init();
    }

    private void init() {
        new TableConstructor("SiteShopItems",
                new TableColumn("id", ColumnType.INT_11).autoIncrement(true).primaryKey(true),
                new TableColumn("item_id", ColumnType.INT_11),
                new TableColumn("player_id", ColumnType.INT_11)
        ).create(database);
    }

    public List<PlayerSSItem> loadItem(BukkitGamer gamer, SSItemManager itemManager) {
        int playerId = gamer == null ? -1 : gamer.getPlayerID();
        if (playerId == -1) {
            return new ArrayList<>();
        }

        return database.executeQuery(MysqlQuery.selectFrom("SiteShopItems")
                .where("player_id", QuerySymbol.EQUALLY, playerId), (rs) -> {
            List<PlayerSSItem> items = new ArrayList<>();

            while (rs.next()) {
                int itemId = rs.getInt("item_id");
                SSItem ssItem = itemManager.getItem(itemId);
                if (ssItem == null) {
                    continue;
                }

                items.add(new PlayerSSItem(ssItem));
            }

            return items;
        });
    }

    public void giveToPlayer(BukkitGamer gamer, SSItem ssItem) {
        if (gamer == null) {
            return;
        }

        database.execute(MysqlQuery.deleteFrom("SiteShopItems")
                .where("item_id", QuerySymbol.EQUALLY, ssItem.getId())
                .where("player_id", QuerySymbol.EQUALLY, gamer.getPlayerID())
                .limit());
    }

    public void close() {
        if (database != null) {
            database.close();
        }
    }
}
