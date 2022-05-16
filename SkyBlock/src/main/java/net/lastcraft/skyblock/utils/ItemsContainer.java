package net.lastcraft.skyblock.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lastcraft.base.gamer.constans.Group;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum ItemsContainer {

    DEFAULT(Group.DEFAULT, Arrays.asList(
            new ItemStack(Material.APPLE),
            new ItemStack(Material.LAVA_BUCKET),
            new ItemStack(Material.BREAD, 3),
            new ItemStack(Material.MELON_SEEDS),
            new ItemStack(Material.PUMPKIN_SEEDS),
            new ItemStack(Material.CACTUS),
            new ItemStack(Material.SUGAR_CANE),
            new ItemStack(Material.BONE),
            new ItemStack(Material.TORCH, 2),
            new ItemStack(Material.BROWN_MUSHROOM),
            new ItemStack(Material.RED_MUSHROOM),
            new ItemStack(Material.ICE, 2)
    ));

    private final Group group;
    private final List<ItemStack> items;

    public static ItemsContainer getItems(Group group) {
        return Arrays.stream(values())
                .filter((container) -> container.getGroup() == group)
                .findFirst()
                .orElse(DEFAULT);
    }
}
