package net.lastcraft.skyblock.api.island;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Biome;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum IslandBiome {
    OCEAN(1, Biome.DEEP_OCEAN, Material.WATER_BUCKET, 5000),
    PLAINS(2, Biome.PLAINS, Material.GRASS, 5000),
    DESERT(3, Biome.DESERT, Material.SAND, 5000),
    FOREST(4, Biome.FOREST, Material.SAPLING, 5000),
    ROOFED_FOREST(5, Biome.ROOFED_FOREST, Material.DIRT, 5000),
    TAIGA(6, Biome.TAIGA_COLD, Material.SNOW_BLOCK, 5000),
    SWAMPLAND(7, Biome.SWAMPLAND, Material.WATER_LILY, 5000),
    MUSHROOM_ISLAND(8, Biome.MUSHROOM_ISLAND, Material.RED_MUSHROOM, 5000),
    JUNGLE(9, Biome.JUNGLE, Material.LEAVES, 5000),
    MESA(10, Biome.MESA, Material.HARD_CLAY, 5000),
    ;

    private final int id;
    private final Biome biome;
    private final Material material;
    private final int price;

    public static IslandBiome getBiome(int id) {
        return Arrays.stream(values())
                .filter((container) -> container.getId() == id)
                .findFirst()
                .orElse(null);
    }
}
