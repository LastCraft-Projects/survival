package net.lastcraft.skyblock.api.island;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Biome;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum IslandType {

    DEFAULT(Material.GRASS, "normal", Biome.PLAINS),

    END(Material.ENDER_STONE, "end", Biome.PLAINS),
    HELL(Material.NETHERRACK, "hell", Biome.PLAINS),
    HOUSE(Material.HAY_BLOCK, "house", Biome.PLAINS),
    ROCKS(Material.COBBLESTONE, "rocks", Biome.PLAINS),
    RUINES(Material.MOSSY_COBBLESTONE, "ruines", Biome.PLAINS),
    TAIGA(Material.SNOW_BLOCK, "taiga", Biome.TAIGA_COLD),
    ;

    private final Material item;
    private final String nameFile;
    private final Biome biome;

    public String getKeyName(){
        return "ISLAND_" + name() + "_NAME";
    }

    public String getKeyLore(){
        return "ISLAND_" + name() + "_LORE";
    }


    public static IslandType getType(String name) {
        return Arrays.stream(values())
                .filter(islandType -> islandType.nameFile.equalsIgnoreCase(name))
                .findFirst()
                .orElse(IslandType.DEFAULT);
    }
}
