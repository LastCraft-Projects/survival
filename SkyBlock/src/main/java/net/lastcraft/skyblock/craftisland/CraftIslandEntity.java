package net.lastcraft.skyblock.craftisland;

import com.sk89q.worldedit.entity.Entity;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.entity.IslandEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

public class CraftIslandEntity implements IslandEntity {

    private static final World WORLD = SkyBlockAPI.getSkyBlockWorld();

    private final Entity entity;
    private WeakReference<org.bukkit.entity.Entity> entityRef;

    public CraftIslandEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public EntityType getType() {
        if (entity.getState() == null)
            return EntityType.UNKNOWN;

        return EntityType.fromName(entity.getState().getTypeId());
    }

    @Override
    public void remove() {
        if (isPlayer())
            return;
        entity.remove();
    }

    @Override
    public boolean isAnimal() {
        EntityType entityType = getType();
        return entityType == EntityType.ZOMBIE_HORSE
                || entityType == EntityType.WOLF
                || entityType == EntityType.SKELETON_HORSE
                || entityType == EntityType.SHEEP
                || entityType == EntityType.RABBIT
                || entityType == EntityType.POLAR_BEAR
                || entityType == EntityType.PIG
                || entityType == EntityType.PARROT
                || entityType == EntityType.OCELOT
                || entityType == EntityType.MUSHROOM_COW
                || entityType == EntityType.MULE
                || entityType == EntityType.LLAMA
                || entityType == EntityType.HORSE
                || entityType == EntityType.DONKEY
                || entityType == EntityType.COW
                || entityType == EntityType.CHICKEN;
    }

    @Override
    public boolean isMonster() {
        EntityType entityType = getType();
        return entityType == EntityType.BLAZE
                || entityType == EntityType.CAVE_SPIDER
                || entityType == EntityType.CREEPER
                || entityType == EntityType.ELDER_GUARDIAN
                || entityType == EntityType.ENDERMITE
                || entityType == EntityType.ENDERMAN
                || entityType == EntityType.EVOKER
                || entityType == EntityType.GIANT
                || entityType == EntityType.GUARDIAN
                || entityType == EntityType.HUSK
                || entityType == EntityType.ILLUSIONER
                || entityType == EntityType.PIG_ZOMBIE
                || entityType == EntityType.SILVERFISH
                || entityType == EntityType.SKELETON
                || entityType == EntityType.SPIDER
                || entityType == EntityType.VEX
                || entityType == EntityType.WITCH
                || entityType == EntityType.VINDICATOR
                || entityType == EntityType.WITHER
                || entityType == EntityType.WITHER_SKELETON
                || entityType == EntityType.ZOMBIE_VILLAGER
                || entityType == EntityType.ZOMBIE
                || entityType == EntityType.STRAY;
    }

    @Override
    public boolean isPlayer() {
        return getBukkitEntity() instanceof Player;
    }

    @Override
    public Location getLocation() {
        com.sk89q.worldedit.util.Location location = entity.getLocation();
        return new Location(WORLD,
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch());
    }

    @Override
    public org.bukkit.entity.Entity getBukkitEntity() {
        return this.entityRef.get();
    }

    public boolean init() {
        try {
            Field field = entity.getClass().getDeclaredField("entityRef");
            field.setAccessible(true);
            entityRef = (WeakReference<org.bukkit.entity.Entity>) field.get(entity);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
