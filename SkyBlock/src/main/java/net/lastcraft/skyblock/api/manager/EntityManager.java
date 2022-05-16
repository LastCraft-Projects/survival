package net.lastcraft.skyblock.api.manager;

import net.lastcraft.skyblock.api.entity.IslandEntity;
import net.lastcraft.skyblock.api.territory.IslandTerritory;
import org.bukkit.entity.Player;

import java.util.List;

public interface EntityManager {

    List<IslandEntity> getEntities(IslandTerritory territory);

    List<Player> getPlayers(IslandTerritory territory);
}
