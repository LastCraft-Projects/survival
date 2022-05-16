package net.lastcraft.alternate.api;

import org.bukkit.Location;

public interface Home {

    /**
     * получить имя дома
     * @return - имя
     */
    String getName();

    /**
     * получить локацию дома
     * @return - локация
     */
    Location getLocation();
}
