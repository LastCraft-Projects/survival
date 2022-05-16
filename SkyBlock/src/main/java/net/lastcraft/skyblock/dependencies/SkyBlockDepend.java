package net.lastcraft.skyblock.dependencies;

import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.manager.IslandManager;

public abstract class SkyBlockDepend {

    protected final DependManager manager;
    protected static final IslandManager ISLAND_MANAGER = SkyBlockAPI.getIslandManager();

    protected SkyBlockDepend(DependManager manager) {
        this.manager = manager;
    }

    protected abstract void init();

    protected abstract void loadConfig();

    protected abstract void disable();
}
