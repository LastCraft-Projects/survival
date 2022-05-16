package net.lastcraft.skyblock.api.island;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.base.sql.api.MySqlDatabase;
import net.lastcraft.skyblock.utils.IslandLoader;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class IslandModule {

    protected static final MySqlDatabase DATABASE = IslandLoader.getMySqlDatabase();
    protected static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();

    protected final Island island;

    protected IslandModule(Island island) {
        this.island = island;
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    /**
     * при включении модуля загрузить
     */
    public abstract void load(ResultSet resultSet) throws SQLException;

    /**
     * добавить инфу в БД при создании острова
     */
    public void preLoad() {
        //nothing
    }

    /**
     * удалить все данные (если остров удаляется)
     */
    public abstract void delete();

    /**
     * при ресете острова делается
     */
    public void resetIsland() {
        //nothing
    }
}
