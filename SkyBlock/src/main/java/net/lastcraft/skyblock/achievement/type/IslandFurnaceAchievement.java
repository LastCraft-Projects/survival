package net.lastcraft.skyblock.achievement.type;

import lombok.Getter;
import net.lastcraft.dartaapi.achievements.achievement.AchievementPlayer;
import org.bukkit.inventory.ItemStack;

@Getter
public class IslandFurnaceAchievement extends IslandAchievement {

    public static final String KEY = "furnace";

    private final int amount;

    public IslandFurnaceAchievement(int id, ItemStack itemStack) {
        this(id, itemStack, 1);
    }

    public IslandFurnaceAchievement(int id, ItemStack itemStack, int amount) {
        super(id, itemStack, "ISLAND_ACHIEVEMENT_FURNACE_NAME",
                "ISLAND_ACHIEVEMENT_" + itemStack.getType().name().toUpperCase() + "_LORE");
        this.amount = amount;
    }

    @Override
    public int getPercent(AchievementPlayer achievementPlayer) {
        return getCompleteAmount(achievementPlayer, KEY) * 100 / amount;
    }
}
