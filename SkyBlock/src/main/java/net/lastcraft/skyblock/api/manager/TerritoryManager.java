package net.lastcraft.skyblock.api.manager;

import net.lastcraft.base.util.Pair;
import net.lastcraft.skyblock.api.territory.IslandTerritory;
import org.bukkit.Location;

import java.util.Map;
import java.util.Set;

public interface TerritoryManager {

    /**
     * получить свободную теру
     * @return - свободная тера
     */
    IslandTerritory get();

    IslandTerritory getTerritory(Pair<Integer, Integer> coordinates);
    IslandTerritory getTerritory(Location location);
    IslandTerritory getTerritory(int i, int j);

    Set<Long> getUsedIslands();
    Map<Long, IslandTerritory> getTerritories();

    /**
     * освободить территорию или занять
     */
    void add(int i, int j);
    void setUsed(int i, int j);

    /**
     * удалить территорию из памяти
     * @param territory - территория
     */
    void remove(IslandTerritory territory);
}
