package net.lastcraft.skyblock.api.territory;

import net.lastcraft.base.util.Pair;

import java.util.List;

public interface IslandTerritory extends Territory {

    /**
     * получить центральный чанк
     * @return - чанк
     */
    Chunk getMiddleChunk();

    /**
     * список всех чанков этой территории
     * @return - чанки
     */
    Chunk[][] getChunks();

    List<org.bukkit.Chunk> getBukkitChunks();

    Pair<Integer, Integer> getMiddle();

    /**
     * принадлежит ли чанк этой локации
     * @param chunk - чанк
     * @return - да или нет
     */
    boolean containsChunk(Chunk chunk);
}
