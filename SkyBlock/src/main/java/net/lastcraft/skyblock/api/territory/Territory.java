package net.lastcraft.skyblock.api.territory;

import net.lastcraft.base.util.Pair;
import org.bukkit.Location;

public interface Territory {

    /**
     * угловые координаты территории
     * @return - коррдинаты
     */
    Pair<Location, Location> getCordAngel();

    /**
     * получить координаты территории
     * @return - координаты
     */
    Pair<Integer, Integer> getCord();

    /**
     * принадлежит ли эта локация этой территории
     * @param location - локация
     * @return - территория
     */
    boolean canInteract(Location location);
}
