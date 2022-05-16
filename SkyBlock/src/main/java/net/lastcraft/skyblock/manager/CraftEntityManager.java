package net.lastcraft.skyblock.manager;

import net.lastcraft.skyblock.api.entity.IslandEntity;
import net.lastcraft.skyblock.api.manager.EntityManager;
import net.lastcraft.skyblock.api.territory.IslandTerritory;
import net.lastcraft.skyblock.craftisland.CraftIslandEntity;
import net.lastcraft.skyblock.utils.FaweUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CraftEntityManager implements EntityManager {

    @Override
    public List<IslandEntity> getEntities(IslandTerritory territory) {
        List<IslandEntity> entities = new ArrayList<>();
        FaweUtils.getEntities(territory).forEach(entity -> {
            CraftIslandEntity craftIslandEntity = new CraftIslandEntity(entity);
            if (craftIslandEntity.init())
                entities.add(craftIslandEntity);
        });
        return entities;
    }

    @Override
    public List<org.bukkit.entity.Player> getPlayers(IslandTerritory territory) {
        return getEntities(territory).stream()
                .filter(IslandEntity::isPlayer)
                .map(entity -> ((Player)entity.getBukkitEntity()))
                .filter(player -> player != null && player.isOnline())
                .collect(Collectors.toList());
    }
}
