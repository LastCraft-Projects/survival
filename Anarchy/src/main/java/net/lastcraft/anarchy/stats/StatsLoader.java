package net.lastcraft.anarchy.stats;

import lombok.experimental.UtilityClass;
import net.lastcraft.base.sql.ConnectionConstants;
import net.lastcraft.base.sql.api.MySqlDatabase;
import net.lastcraft.base.sql.api.table.ColumnType;
import net.lastcraft.base.sql.api.table.TableColumn;
import net.lastcraft.base.sql.api.table.TableConstructor;

@UtilityClass
public class StatsLoader {

    private final MySqlDatabase MYSQL_DATABASE = MySqlDatabase.newBuilder()
            .host("s1" + ConnectionConstants.DOMAIN.getValue())
            .password(ConnectionConstants.PASSWORD.getValue())
            .user("root")
            .data("anarchy")
            .create();

    public static MySqlDatabase getMysqlDatabase() {
        return MYSQL_DATABASE;
    }

    public StatsPlayer getData(int playerID) {
        return MYSQL_DATABASE.executeQuery("SELECT * FROM `Stats` WHERE `playerID`= ? LIMIT 1;", (rs) -> {
            int kills = 0;
            int death = 0;

            if (rs.next()) {
                kills = rs.getInt("kills");
                death = rs.getInt("death");
            } else {
                MYSQL_DATABASE.execute("INSERT INTO `Stats` (`playerID`, `kills`, `death`) VALUES (?, 0, 0);",
                        playerID);
            }

            return new StatsPlayer(kills, death);
        }, playerID);
    }

    public void saveData(int playerID, int kills, int death) {
        MYSQL_DATABASE.execute("UPDATE `Stats` SET `kills`= ?,`death`= ? WHERE `playerID`= ? LIMIT 1;",
                kills, death, playerID);
    }

    public void init() {
        new TableConstructor("Stats",
                new TableColumn("playerID", ColumnType.INT_11).primaryKey(true),
                new TableColumn("kills", ColumnType.INT_11),
                new TableColumn("death", ColumnType.INT_11)
        ).create(MYSQL_DATABASE);
    }
}
