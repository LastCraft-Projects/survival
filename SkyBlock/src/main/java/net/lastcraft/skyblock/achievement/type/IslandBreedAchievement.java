package net.lastcraft.skyblock.achievement.type;

import lombok.Getter;
import net.lastcraft.dartaapi.achievements.achievement.AchievementPlayer;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

@Getter
public class IslandBreedAchievement extends IslandAchievement{

    public static final String KEY = "breed";

    private final EntityType entityType;
    private final int amount;

    public IslandBreedAchievement(int id, EntityType entityType) {
        this(id, entityType, 1);
    }

    public IslandBreedAchievement(int id, EntityType entityType, int amount) {
        super(id, new ItemStack(Material.MONSTER_EGG), "ISLAND_ACHIEVEMENT_BREED_NAME",
                "ISLAND_ACHIEVEMENT_" + entityType.name().toUpperCase() + "_LORE");
        this.entityType = entityType;
        this.amount = amount;
    }

    @Override
    public int getPercent(AchievementPlayer achievementPlayer) {
        return getCompleteAmount(achievementPlayer, KEY) * 100 / amount;
    }
}
