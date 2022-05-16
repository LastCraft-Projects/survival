package net.lastcraft.skyblock.api;

import net.lastcraft.alternate.Alternate;
import net.lastcraft.api.manager.GuiManager;
import net.lastcraft.skyblock.api.manager.EntityManager;
import net.lastcraft.skyblock.api.manager.IslandManager;
import net.lastcraft.skyblock.api.manager.SkyGamerManager;
import net.lastcraft.skyblock.api.manager.TerritoryManager;
import net.lastcraft.skyblock.manager.*;
import org.bukkit.Bukkit;
import org.bukkit.World;

public final class SkyBlockAPI {

    private static final int SIZE = 13; //размеры острова (макс) size x size
    private static final int MIN_SIZE = 3;

    private static final String SKY_BLOCK_WORLD_NAME = "SkyBlockWorld";
    private static final String PREFIX = "§6SkyBlock §8| §f";
    //private static final String DATABASE = "skyblock";

    private static IslandManager islandManager;
    private static TerritoryManager territoryManager;
    private static SkyGamerManager skyGamerManager;
    private static GuiManager<SkyBlockGui> skyGuiManager;
    private static EntityManager entityManager;

    /**
     * Интерфейс для работы с Island'ами
     * @return - islandManager
     */
    public static IslandManager getIslandManager() {
        if (islandManager == null)
            islandManager = new CraftIslandManager();

        return islandManager;
    }

    /**
     * Интерфейс для работы с Territory
     * @return - territoryManager
     */
    public static TerritoryManager getTerritoryManager() {
        if (territoryManager == null)
            territoryManager = new CraftTerritoryManager();

        return territoryManager;
    }

    /**
     * Интерфейс для работы с SkyGamer
     * @return - skyGamerManager
     */
    public static SkyGamerManager getSkyGamerManager() {
        if (skyGamerManager == null)
            skyGamerManager = new CraftSkyGamerManager();

        return skyGamerManager;
    }

    /**
     * интерфейс для работы с GUI игроков
     * @return - skyGuiManager
     */
    public static GuiManager<SkyBlockGui> getSkyGuiManager() {
        if (skyGuiManager == null)
            skyGuiManager = new CraftSkyGuiManager();

        return skyGuiManager;
    }

    public static EntityManager getEntityManager() {
        if (entityManager == null)
            entityManager = new CraftEntityManager();
        return entityManager;
    }

    public static int getSize() {
        return SIZE;
    }

    public static int getMinSize() {
        return MIN_SIZE;
    }

    public static String getPrefix() {
        return PREFIX;
    }

    public static String getSkyBlockWorldName() {
        return SKY_BLOCK_WORLD_NAME;
    }

    public static World getSkyBlockWorld() {
        return Bukkit.getWorld(SKY_BLOCK_WORLD_NAME);
    }

    public static String getDatabase() {
        return Alternate.getConfigData().getDataBase();
    }
}
