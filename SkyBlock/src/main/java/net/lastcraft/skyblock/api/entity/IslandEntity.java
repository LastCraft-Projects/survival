package net.lastcraft.skyblock.api.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public interface IslandEntity {

    /**
     * получить тип энтити
     * @return - тип
     */
    EntityType getType();

    /**
     * удалить из мира
     */
    void remove();

    boolean isAnimal();

    boolean isMonster();

    boolean isPlayer();

    Location getLocation();

    Entity getBukkitEntity();
}
