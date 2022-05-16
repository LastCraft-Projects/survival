package net.lastcraft.alternate.trade;

import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.locale.Language;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

class Trade {
    private static final int[] slots = { 4, 13, 22, 31, 40, 49, 36, 37, 38, 39, 41, 42, 43, 44, 46,
                                        47, 48, 49, 50, 51, 52 };
    private static final ItemStack PANEL = ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
            .setName(" ")
            .setDurability((short)15)
            .build();

    private final Player player1;
    private final Player player2;
    private final Language lang1;
    private final Language lang2;

    private final boolean init;

    private Inventory inventory1;
    private Inventory inventory2;
    private boolean ready1;
    private boolean ready2;

    Trade(Player player1, Player player2, Language lang1, Language lang2) {
        this.player1 = player1;
        this.player2 = player2;
        this.lang1 = lang1;
        this.lang2 = lang2;

        this.ready2 = false;
        this.ready1 = false;

        player1.closeInventory();
        player2.closeInventory();

        createInventories();

        if (inventory1 == null || inventory2 == null) {
            this.init = false;
            return;
        }
        this.init = true;

        player1.openInventory(inventory1);
        player2.openInventory(inventory2);
    }

    boolean isInit() {
        return init;
    }

    Inventory getInventory1() {
        return inventory1;
    }

    Inventory getInventory2() {
        return inventory2;
    }

    private void createInventories() {
        this.inventory1 = Bukkit.createInventory(player1, 54, "Trade " + player1.getName() + "-" + player2.getName());
        this.inventory2 = Bukkit.createInventory(player2, 54, "Trade " + player1.getName() + "-" + player2.getName());

        for (int slot : slots) {
            inventory1.setItem(slot, PANEL);
            inventory2.setItem(slot, PANEL);
        }

        update();
    }

    private void update() {
        if (ready1 && ready2) {
            close();
            return;
        }

    }

    private void close() {

    }
}
