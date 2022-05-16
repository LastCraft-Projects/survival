package net.lastcraft.siteshop.guis;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.DInventory;
import net.lastcraft.api.inventory.InventoryAPI;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.sound.SoundAPI;
import net.lastcraft.api.sound.SoundType;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.locale.Language;
import net.lastcraft.siteshop.ItemsLoader;
import net.lastcraft.siteshop.item.PlayerSSItem;
import net.lastcraft.siteshop.item.SSItem;
import net.lastcraft.siteshop.item.SSItemManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class SSGui {

    private static final InventoryAPI INVENTORY_API = LastCraft.getInventoryAPI();
    private static final SoundAPI SOUND_API = LastCraft.getSoundAPI();

    private final SSItemManager itemManager;
    private final BukkitGamer gamer;
    private final DInventory inventory;

    private final List<PlayerSSItem> items = new ArrayList<>();

    public SSGui(SSItemManager itemManager, BukkitGamer gamer) {
        this.itemManager = itemManager;
        this.gamer = gamer;

        this.inventory = INVENTORY_API.createInventory(
                gamer.getPlayer(),
                gamer.getLanguage().getMessage("SITE_SHOP_ITEMS_GUI_NAME"), 5);
    }

    public void loadFromMysql() {
        items.addAll(itemManager.getItemsLoader().loadItem(gamer, itemManager));
    }

    public void updateGui() {
        Language lang = gamer.getLanguage();

        if (items.isEmpty()) {
            inventory.setItem(2 * 9 + 4, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.GLASS_BOTTLE)
                    .setName(lang.getMessage("SITE_SHOP_ITEM_EMPTY_NAME"))
                    .setLore(lang.getList("SITE_SHOP_ITEM_EMPTY_LORE"))
                    .removeFlags()
                    .build(), (player, clickType, i) -> SOUND_API.play(player, SoundType.NO)));
            return;
        }

        int slot = 10;
        ItemsLoader itemsLoader = itemManager.getItemsLoader();
        for (PlayerSSItem playerSSItem : items) {
            SSItem ssItem = playerSSItem.getSsItem();

            int finalSlot = slot;
            inventory.setItem(finalSlot, INVENTORY_API.createItem(ssItem.getIcon(lang, true),
                    (player, clickType, i) -> {
                if (!findSlots(player, ssItem)) {
                    SOUND_API.play(player, SoundType.NO);
                    gamer.sendMessageLocale("SITE_SHOP_NO_SLOTS");
                    return;
                }
                if (!playerSSItem.isAllowed()) {
                    return;
                }
                playerSSItem.giveToPlayer(gamer, itemsLoader);
                SOUND_API.play(player, SoundType.DESTROY);
                inventory.removeItem(finalSlot);
            }));

            slot++;

            if ((slot - 1) % 8 == 0)
                slot += 2;
        }
    }

    public static boolean findSlots(Player player, SSItem ssItem) {
        int size = ssItem.getPurchasedItems().size();
        for (ItemStack itemStack : player.getInventory().getStorageContents()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                size--;
            }
            if (size <= 0) {
                return true;
            }
        }
        return false;
    }

    public void open() {
        Player player = gamer.getPlayer();
        if (player == null || !player.isOnline()) {
            return;
        }

        inventory.openInventory(player);
    }
}
