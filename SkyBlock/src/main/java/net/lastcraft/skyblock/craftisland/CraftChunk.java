package net.lastcraft.skyblock.craftisland;

import net.lastcraft.base.util.Pair;
import net.lastcraft.dartaapi.utils.core.MathUtil;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.territory.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CraftChunk implements Chunk {
    private static final Map<Long, Chunk> CHUNKS_MAP = new ConcurrentHashMap<>();
    private static final World WORLD = SkyBlockAPI.getSkyBlockWorld();

    private int x, y;

    private CraftChunk(Location location) {
        this((int)location.getX(), (int)location.getZ());
        CHUNKS_MAP.put(MathUtil.pairInt(x, y), this);
    }

    private CraftChunk(int x, int z) {
        this.x = (int)Math.floor(x / 16.0);
        this.y = (int)Math.floor(z / 16.0);
        CHUNKS_MAP.put(MathUtil.pairInt(this.x, this.y), this);
    }

    public CraftChunk(Block block) {
        this(block.getLocation());
    }

    @Override
    public World getWorld() {
        return WORLD;
    }

    @Override
    public Location getMiddle() {
        return new Location(WORLD, x * 16 + 8, 128, y * 16 + 8);
    }


    @Override
    public Pair<Integer, Integer> getCord() {
        return new Pair<>(x, y);
    }

    @Override
    public Pair<Location, Location> getCordAngel() {
        Location up = new Location(WORLD, 16 * x, 255,16 * y);
        Location down = new Location(WORLD, 16 * x + 16, 0, 16 * y + 16);
        return new Pair<>(up, down);
    }

    @Override
    public boolean canInteract(Location location) {
        return location.getX() >= (16 * x) && location.getX() < (16 * x + 16)
                && location.getZ() >= (16 * y) && location.getZ() < (16 * y + 16);
    }

    @Override
    public List<Location> getSurround() {
        List<Location> locations = new ArrayList<>();
        for (int i = 16 * x; i < 16 * x + 16; ++i)
            for (int j = 16 * y; j < 16 * y + 16; ++j)
                locations.add(new Location(WORLD, i, 0, j));

        return locations;
    }

    @Override
    public List<Location> getLocations() {
        List<Location> locations = new ArrayList<>();
        for (int x = 0; x < 16; ++x)
            for (int y = 0; y < 256; ++y)
                for (int z = 0; z < 16; ++z)
                    locations.add(new Location(WORLD, x + 16 * this.x, y, z + 16 * this.y));

        return locations;
    }

    @Override
    public List<org.bukkit.Chunk> getBukkitChunk() {
        List<org.bukkit.Chunk> chunks = new ArrayList<>();
        for (Location location : getLocations()) {
            org.bukkit.Chunk chunk = location.getChunk();
            if (chunks.contains(chunk))
                continue;
            chunks.add(chunk);
        }
        return chunks;
    }

    @Override
    public void remove() {
        CHUNKS_MAP.remove(MathUtil.pairInt(x, y));
    }

    static Chunk getChunk(Location location) {
        int x = (int)Math.floor(location.getX() / 16.0);
        int y = (int)Math.floor(location.getZ() / 16.0);

        if (!CHUNKS_MAP.containsKey(MathUtil.pairInt(x, y)))
            return new CraftChunk(location);

        return CHUNKS_MAP.get(MathUtil.pairInt(x, y));
    }
}
