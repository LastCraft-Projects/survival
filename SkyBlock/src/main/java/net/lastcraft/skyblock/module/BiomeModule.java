package net.lastcraft.skyblock.module;

import lombok.Getter;
import net.lastcraft.base.sql.api.query.MysqlQuery;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.island.IslandBiome;
import net.lastcraft.skyblock.api.island.IslandModule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BiomeModule extends IslandModule {

    @Getter
    private final Set<IslandBiome> biomes = Collections.synchronizedSet(new HashSet<>());

    public BiomeModule(Island island) {
        super(island);
    }

    @Override
    public void load(ResultSet resultSet) throws SQLException {
        IslandBiome islandBiome = IslandBiome.getBiome(resultSet.getInt("biome"));
        if (islandBiome == null)
            return;

        biomes.add(islandBiome);
    }

    public void buyBiome(IslandBiome islandBiome) {
        biomes.add(islandBiome);
        DATABASE.execute(MysqlQuery.insertTo("IslandBiome")
                .set("biome", islandBiome.getId())
                .set("island", island.getIslandID()));
    }


    @Override
    public void delete() {
        int id = island.getIslandID();
        if (id == -1)
            return;

        DATABASE.execute("DELETE FROM `IslandBiome` WHERE `island` = ?;", id);
    }


}
