package net.lastcraft.siteshop.guis;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.InventoryAPI;
import net.lastcraft.api.inventory.MultiInventory;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.sound.SoundAPI;
import net.lastcraft.api.sound.SoundType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.siteshop.item.SSItem;
import net.lastcraft.siteshop.item.SSItemManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class BuySGui {

    private static final InventoryAPI INVENTORY_API = LastCraft.getInventoryAPI();
    private static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();
    private static final SoundAPI SOUND_API = LastCraft.getSoundAPI();

    private final MultiInventory inventory;
    private final Language language;

    public BuySGui(Language language) {
        this.inventory = INVENTORY_API.createMultiInventory(language.getMessage("SITE_SHOP_ITEMS_NAME"), 5);
        this.language = language;
    }

    public void setItems(SSItemManager ssItemManager) {
        int slot = 10;
        int page = 0;

        for (SSItem ssItem : ssItemManager.getItems().valueCollection()) {
            inventory.setItem(page, slot++, INVENTORY_API.createItem(ssItem.getIcon(language, false),
                    (player, clickType, i) -> {
                BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
                if (gamer == null) {
                    SOUND_API.play(player, SoundType.NO);
                    return;
                }

                if (!SSGui.findSlots(player, ssItem)) {
                    SOUND_API.play(player, SoundType.NO);
                    gamer.sendMessageLocale("SITE_SHOP_NO_SLOTS");
                    return;
                }

                if (!gamer.changeGold(-ssItem.getPrice())) {
                    player.closeInventory();
                    return;
                }

                SOUND_API.play(player, SoundType.LEVEL_UP);
                gamer.sendMessageLocale("SITE_SHOP_ITEM_ALERT");

                PlayerInventory inventory = player.getInventory();
                ssItem.getPurchasedItems().forEach(inventory::addItem);
            }));

            if ((slot - 8) % 9 == 0)
                slot += 2;

            if (slot >= 35) {
                slot = 10;
                page++;
            }
        }

        INVENTORY_API.pageButton(language, page + 1, inventory, 38, 42);
    }

    public void open(Player player) {
        if (inventory != null) {
            inventory.openInventory(player);
        }
    }
}
