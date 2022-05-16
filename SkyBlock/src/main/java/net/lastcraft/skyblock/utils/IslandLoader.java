package net.lastcraft.skyblock.utils;

import lombok.experimental.UtilityClass;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.gamer.IBaseGamer;
import net.lastcraft.base.sql.ConnectionConstants;
import net.lastcraft.base.sql.api.MySqlDatabase;
import net.lastcraft.base.sql.api.query.MysqlQuery;
import net.lastcraft.base.sql.api.query.QuerySymbol;
import net.lastcraft.base.sql.api.table.ColumnType;
import net.lastcraft.base.sql.api.table.TableColumn;
import net.lastcraft.base.sql.api.table.TableConstructor;
import net.lastcraft.dartaapi.achievements.achievement.AchievementPlayer;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.entity.SkyGamer;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.island.IslandModule;
import net.lastcraft.skyblock.api.island.IslandType;
import net.lastcraft.skyblock.api.island.member.IslandMember;
import net.lastcraft.skyblock.api.island.member.MemberType;
import net.lastcraft.skyblock.api.territory.IslandTerritory;
import net.lastcraft.skyblock.craftisland.CraftIsland;
import net.lastcraft.skyblock.craftisland.CraftIslandMember;
import net.lastcraft.skyblock.craftisland.CraftSkyGamer;
import net.lastcraft.skyblock.manager.CraftIslandManager;
import net.lastcraft.skyblock.module.ModuleData;

import java.sql.Timestamp;
import java.util.Map;

@UtilityClass
public class IslandLoader {
    private final CraftIslandManager ISLAND_MANAGER = (CraftIslandManager) SkyBlockAPI.getIslandManager();
    private boolean load = false;

    private final MySqlDatabase MY_SQL_DATABASE = MySqlDatabase.newBuilder()
            .data(SkyBlockAPI.getDatabase())
            .user("root")
            .host("mysql" + ConnectionConstants.DOMAIN.getValue())
            .password(ConnectionConstants.PASSWORD.getValue())
            .create();

    public MySqlDatabase getMySqlDatabase() {
        return MY_SQL_DATABASE;
    }

    public void init() { //создаем все базы
        if (load) {
            return;
        }

        new TableConstructor("Islands",
                new TableColumn("id", ColumnType.INT_11).primaryKey(true).autoIncrement(true).unigue(true),
                new TableColumn("i", ColumnType.INT_11),
                new TableColumn("j", ColumnType.INT_11),
                new TableColumn("owner", ColumnType.INT_11).primaryKey(true).unigue(true),
                new TableColumn("date", ColumnType.TIMESTAMP).setNull(false),
                new TableColumn("money", ColumnType.INT).setDefaultValue(0),
                new TableColumn("schematic", ColumnType.VARCHAR_16).setDefaultValue("normal"),
                new TableColumn("level", ColumnType.INT).setDefaultValue(0)
        ).create(MY_SQL_DATABASE);
        new TableConstructor("Members",
                new TableColumn("id", ColumnType.INT_11).primaryKey(true).autoIncrement(true),
                new TableColumn("island", ColumnType.INT_11),
                new TableColumn("player", ColumnType.INT_11),
                new TableColumn("rank", ColumnType.INT_11).setDefaultValue(MemberType.MEMBER.getData()),
                new TableColumn("date", ColumnType.TIMESTAMP).setNull(false)
        ).create(MY_SQL_DATABASE);
        new TableConstructor("SkyGamers",
                new TableColumn("id", ColumnType.INT_11).primaryKey(true).autoIncrement(true),
                new TableColumn("player", ColumnType.INT_11).primaryKey(true),
                new TableColumn("boarder", ColumnType.BOOLEAN).setDefaultValue(1)
        ).create(MY_SQL_DATABASE);

        //загружаем острова игроков
        MY_SQL_DATABASE.executeQuery("SELECT * FROM `Islands`;", (rs) -> {
            while (rs.next()) {
                int islandID = rs.getInt("id");
                int i = rs.getInt("i");
                int j = rs.getInt("j");
                int ownerId = rs.getInt("owner");
                int money = rs.getInt("money");
                int level = rs.getInt("level");
                IslandType islandType = IslandType.getType(rs.getString("schematic"));
                Timestamp date = rs.getTimestamp("date");

                Island island = new CraftIsland(islandID, i, j, ownerId, money, level, islandType, date.getTime());
                IslandTerritory territory = island.getTerritory();
                ISLAND_MANAGER.getPlayerIsland().put(ownerId, island);
                ISLAND_MANAGER.getTerritoryIsland().put(territory, island);
            }

            return Void.TYPE;
        });

        for (ModuleData value : ModuleData.values()) { //загружаем инфу из секций
            TableConstructor constructor = value.getConstructor();
            constructor.create(MY_SQL_DATABASE);
            MY_SQL_DATABASE.executeQuery("SELECT * FROM `" + constructor.getName() + "`;", (rs) -> {
                while (rs.next()) {
                    int islandId = rs.getInt("island");
                    Island island = ISLAND_MANAGER.getIslandById(islandId);
                    if (island == null) {
                        continue;
                    }

                    IslandModule module = island.getModule(value.getModuleClass());
                    if (module == null) {
                        continue;
                    }

                    module.load(rs);
                }
                return Void.TYPE;
            });
        }

        load = true;
    }

    public boolean isLoad() {
        return load;
    }

    public SkyGamer getSkyGamer(AchievementPlayer achievementPlayer) {
        BukkitGamer gamer = achievementPlayer.getGamer();
        if (gamer == null)
            return null;

        return MY_SQL_DATABASE.executeQuery("SELECT * FROM `SkyGamers` WHERE `player` = ? LIMIT 1;", (rs) -> {
            boolean border = false;

            if (rs.next())
                border = rs.getBoolean("boarder");

            return (SkyGamer) new CraftSkyGamer(gamer.getName(), achievementPlayer, border);
        }, gamer.getPlayerID());
    }

    public int addIsland(int i, int j, int owner, IslandType islandType) {
        return MY_SQL_DATABASE.execute("INSERT INTO `Islands` (i, j, owner, date, schematic) VALUES (?, ?, ?, ?, ?);",
                i, j, owner, new Timestamp(System.currentTimeMillis()), islandType.getNameFile());
    }

    public int getIslandID(Island island) {
        IBaseGamer gamer = island.getOwner();
        int owner = gamer.getPlayerID();
        return MY_SQL_DATABASE.executeQuery("SELECT `id` FROM `Islands` WHERE `owner` = ? LIMIT 1;", (rs) -> {
            int id = -1;
            if (rs.next())
                id = rs.getInt(1);

            return id;
        }, owner);
    }

    public void addMember(Island island, int playerID) {
        final int id = island.getIslandID();
        MY_SQL_DATABASE.execute("INSERT INTO `Members` (`island`, `player`, `date`) VALUES (?, ?, ?);",
                id, playerID, new Timestamp(System.currentTimeMillis()));
    }

    public void removeMember(Island island, int playerID) {
        final int id = island.getIslandID();
        MY_SQL_DATABASE.execute("DELETE FROM `Members` WHERE `island` = ? && `player` = ?;", id, playerID);
    }

    public void changeMoney(Island island, int money) {
        final int id = island.getIslandID();
        MY_SQL_DATABASE.execute("UPDATE `Islands` SET `money`=`money`+? WHERE `id`= ?  LIMIT 1;", money, id);
    }

    public void removeIsland(Island island) {
        final int id = island.getIslandID();
        MY_SQL_DATABASE.execute("DELETE FROM `Members` WHERE `island` = ?;", id);
        MY_SQL_DATABASE.execute("DELETE FROM `Islands` WHERE `id` = ?;", id);
    }

    public void loadMembers(Island island, Map<Integer, IslandMember> members) {
        MY_SQL_DATABASE.executeQuery("SELECT * FROM `Members` WHERE `island` = ?", (rs) -> {
            while (rs.next()) {
                int data = rs.getInt("rank");
                MemberType memberType = MemberType.getMemberType(data);
                if (memberType == MemberType.NOBODY)
                    continue;

                Timestamp date = rs.getTimestamp("date");
                int playerID = rs.getInt("player");
                IslandMember islandMember = new CraftIslandMember(island, playerID, memberType, date.getTime());
                members.put(playerID, islandMember);
                ISLAND_MANAGER.addMember(island, playerID, false);
            }
            return Void.TYPE;
        }, island.getIslandID());
    }

    public void changeMemberType(Island island, int playerID, MemberType newMemberType) {
        MY_SQL_DATABASE.execute(MysqlQuery.update("Members")
                .set("rank", newMemberType.getData())
                .where("island", QuerySymbol.EQUALLY, island.getIslandID())
                .where("player", QuerySymbol.EQUALLY, playerID));
    }

    public void close() {
        MY_SQL_DATABASE.close();
        load = false;
    }
}
