package net.lastcraft.alternate.object;

import net.lastcraft.alternate.api.Home;
import org.bukkit.Location;

public class CraftHome implements Home {

    private String name;
    private Location location;

    public CraftHome(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
