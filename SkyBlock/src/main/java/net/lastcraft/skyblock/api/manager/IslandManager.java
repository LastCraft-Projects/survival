package net.lastcraft.skyblock.api.manager;

import net.lastcraft.base.gamer.IBaseGamer;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.island.IslandType;
import net.lastcraft.skyblock.api.territory.IslandTerritory;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;

public interface IslandManager {

    /**
     * получить исланд игрока
     * @param player - чей остров
     * @param location - локация из мира
     * @param territory - территория
     * @return - объект острова or null
     */
    Island getIsland(String name);

    Island getIsland(int playerID);

    Island getIsland(Player player);

    Island getIsland(IBaseGamer gamer);

    Island getIsland(Location location);

    Island getIsland(IslandTerritory territory);

    Island getIsland(Entity entity);

    Island getIslandById(int islandId);

    /**
     * узнать, есть ли остров у игрока
     * @param player - игрок
     * @return - да или нет
     */
    boolean hasIsland(Player player);
    boolean hasIsland(int playerID);

    /**
     * создать новый остров
     * @param player - кому создать
     */
    Island createIsland(Player player, IslandType islandType);

    /**
     * получить все острова
     * @return - список островов
     */
    Map<IslandTerritory, Island> getTerritoryIsland();

    /**
     * список овнер - остров
     * @return мапа
     */
    Map<Integer, Island> getPlayerIsland();

    /**
     * список овнер - остров
     * @return мапа
     */
    Map<Integer, Island> getMemberIsland();

    /**
     * удалить остров
     */
    void delete(Island island);
}
