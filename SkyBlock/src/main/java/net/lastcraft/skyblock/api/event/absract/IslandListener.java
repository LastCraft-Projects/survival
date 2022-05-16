package net.lastcraft.skyblock.api.event.absract;

import net.lastcraft.dartaapi.listeners.DListener;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.manager.IslandManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class IslandListener extends DListener<JavaPlugin> {

    protected static final IslandManager ISLAND_MANAGER = SkyBlockAPI.getIslandManager();

    protected IslandListener(JavaPlugin javaPlugin) {
        super(javaPlugin);
    }

    protected static boolean isSkyBlockWorld(World world) {
        return world != null && world.getName().equalsIgnoreCase(SkyBlockAPI.getSkyBlockWorldName());
    }

    protected static boolean isSkyBlockWorld(Location location) {
        return isSkyBlockWorld(location.getWorld());
    }
}
