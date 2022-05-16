package net.lastcraft.skyblock.craftisland;

import net.lastcraft.base.util.Pair;
import net.lastcraft.dartaapi.utils.core.MathUtil;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.manager.TerritoryManager;
import net.lastcraft.skyblock.api.territory.Chunk;
import net.lastcraft.skyblock.api.territory.IslandTerritory;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class CraftIslandTerritory implements IslandTerritory {

    private static final World WORLD = SkyBlockAPI.getSkyBlockWorld();
    private static final int SIZE = SkyBlockAPI.getSize();
    private static final TerritoryManager MANAGER = SkyBlockAPI.getTerritoryManager();

    private int i, j;
    private Chunk chunks[][] = new CraftChunk[SIZE][];

    public CraftIslandTerritory(int i, int j) {
        for (int ii = 0; ii < SIZE; ++ii)
            chunks[ii] = new CraftChunk[SIZE];

        MANAGER.getTerritories().put(MathUtil.pairInt(i, j), this);
        MANAGER.getUsedIslands().add(MathUtil.pairInt(i, j));

        MANAGER.add(i - 1, j);
        MANAGER.add(i + 1, j);
        MANAGER.add(i, j + 1);
        MANAGER.add(i, j - 1);

        MANAGER.setUsed(i, j);

        this.i = i;
        this.j = j;

        for (int k = 0; k < SIZE; ++k) {
            for (int q = 0; q < SIZE; ++q) {
                chunks[k][q] = CraftChunk.getChunk(new Location(WORLD,
                        16 * (SIZE * i + k), 0, 16 * (SIZE * j + q)));
            }
        }
    }

    @Override
    public Pair<Integer, Integer> getCord() {
        return new Pair<>(i, j);
    }

    @Override
    public Pair<Location, Location> getCordAngel() {
        Chunk up = chunks[0][0];
        Chunk down = chunks[SIZE - 1][SIZE - 1];
        return new Pair<>(up.getCordAngel().getFirst(), down.getCordAngel().getSecond());
    }

    @Override
    public Chunk getMiddleChunk() {
        return chunks[SIZE / 2][SIZE / 2];
    }

    @Override
    public Chunk[][] getChunks() {
        return chunks;
    }

    @Override
    public List<org.bukkit.Chunk> getBukkitChunks() {
        List<org.bukkit.Chunk> chunks = new ArrayList<>();
        for (int i = 0; i < SIZE; ++i)
            for (int j = 0; j < SIZE; ++j)
                chunks.addAll(this.chunks[i][j].getBukkitChunk());

        return chunks;
    }

    @Override
    public Pair<Integer, Integer> getMiddle() {
        return new Pair<>(SIZE / 2, SIZE / 2);
    }

    @Override
    public boolean containsChunk(Chunk chunk) {
        return canInteract(chunk.getMiddle());
    }

    @Override
    public boolean canInteract(Location location) {
        for (int i = 0; i < SIZE; ++i)
            for (int j = 0; j < SIZE; ++j)
                if (chunks[i][j].canInteract(location))
                    return true;

        return false;
    }

    public void remove() {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                chunks[i][j].remove();
    }
}
