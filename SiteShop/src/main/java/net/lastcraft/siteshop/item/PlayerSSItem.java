package net.lastcraft.siteshop.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.siteshop.ItemsLoader;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

@RequiredArgsConstructor
@Getter
public final class PlayerSSItem {

    private final SSItem ssItem;

    private boolean allowed = true;

    public void giveToPlayer(BukkitGamer gamer, ItemsLoader itemsLoader) {
        allowed = false;

        Player player = gamer.getPlayer();
        if (player == null || !player.isOnline()) {
            return;
        }

        gamer.sendMessageLocale("SITE_SHOP_ITEM_ALERT");
        itemsLoader.giveToPlayer(gamer, ssItem);

        PlayerInventory inventory = player.getInventory();
        ssItem.getPurchasedItems().forEach(inventory::addItem);
    }
}
