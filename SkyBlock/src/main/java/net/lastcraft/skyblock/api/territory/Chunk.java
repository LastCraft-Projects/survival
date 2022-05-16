package net.lastcraft.skyblock.api.territory;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

public interface Chunk extends Territory {

    World getWorld();

    Location getMiddle();

    List<Location> getSurround();

    List<Location> getLocations();

    List<org.bukkit.Chunk> getBukkitChunk();

    void remove();
}
