package net.lastcraft.anarchy.listener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
@Getter
public class Scavenger {

    private final List<ItemStack> items;
    private final float exp;
    private final int level;

    public void give(Player player) {
        player.setExp(exp);
        player.setLevel(level);

        if (items == null)
            return;

        items.forEach(itemStack -> player.getInventory().addItem(itemStack));
    }
}
