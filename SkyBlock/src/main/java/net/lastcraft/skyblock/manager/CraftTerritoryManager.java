package net.lastcraft.skyblock.manager;

import net.lastcraft.base.util.Pair;
import net.lastcraft.dartaapi.utils.core.MathUtil;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.manager.TerritoryManager;
import net.lastcraft.skyblock.api.territory.IslandTerritory;
import net.lastcraft.skyblock.craftisland.CraftIslandTerritory;
import org.bukkit.Location;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CraftTerritoryManager implements TerritoryManager {

    private final Map<Long, IslandTerritory> territories = new ConcurrentHashMap<>();
    private final HashSet<Long> usedIslands = new HashSet<>();

    private final Queue<Pair<Integer, Integer>> open = new LinkedList<>();

    @Override
    public void add(int i, int j) {
        if (open.size() >= 1000 || usedIslands.contains(MathUtil.pairInt(i, j))) {
            return;
        }

        open.add(new Pair<>(i, j));
    }

    @Override
    public void setUsed(int i, int j) {
        open.remove(new Pair<>(i, j));
    }

    @Override
    public Map<Long, IslandTerritory> getTerritories() {
        return territories;
    }

    @Override
    public Set<Long> getUsedIslands() {
        return usedIslands;
    }

    @Override
    public IslandTerritory get() {
        if (territories.size() == 0)
            add(0, 0);
        if (open.isEmpty() && territories.size() != 0)
            throw new ArrayStoreException("Queue of CraftTerritoryManager is empty!");

        Pair<Integer, Integer> pair = open.element();
        if (usedIslands.contains(MathUtil.pairInt(pair.getFirst(), pair.getSecond()))) {
            open.remove(pair);
            return get();
        }

        IslandTerritory territory = getTerritory(pair);
        open.remove(pair);

        return territory;
    }

    @Override
    public void remove(IslandTerritory territory) {
        CraftIslandTerritory craftIslandTerritory = (CraftIslandTerritory) territory;
        craftIslandTerritory.remove();
        int i = territory.getCord().getFirst();
        int j = territory.getCord().getSecond();
        usedIslands.remove(MathUtil.pairInt(i, j));
        territories.remove(MathUtil.pairInt(i, j));
        add(i, j);
    }

    @Override
    public IslandTerritory getTerritory(Pair<Integer, Integer> coordinates) {
        return getTerritory(coordinates.getFirst(), coordinates.getSecond());
    }

    @Override
    public IslandTerritory getTerritory(Location location) {
        int x = (int)Math.floor(location.getBlockX() / (16.0 * SkyBlockAPI.getSize()));
        int z = (int)Math.floor(location.getBlockZ() / (16.0 * SkyBlockAPI.getSize()));
        if (!territories.containsKey(MathUtil.pairInt(x, z)))
            return new CraftIslandTerritory(x, z);
        return territories.get(MathUtil.pairInt(x, z));
    }

    @Override
    public IslandTerritory getTerritory(int i, int j) {
        if (!territories.containsKey(MathUtil.pairInt(i, j)))
            return new CraftIslandTerritory(i, j);

        return territories.get(MathUtil.pairInt(i, j));
    }
}
