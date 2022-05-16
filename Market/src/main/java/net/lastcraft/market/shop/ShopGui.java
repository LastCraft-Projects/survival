package net.lastcraft.market.shop;

import lombok.Getter;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.DInventory;
import net.lastcraft.api.inventory.InventoryAPI;
import net.lastcraft.base.locale.Language;

import java.util.List;

@Getter
public class ShopGui {
    private static final InventoryAPI INVENTORY_API = LastCraft.getInventoryAPI();

    private final Language lang;
    private final String name;
    private final DInventory dInventory;
    private final List<ShopItem> items;

    public ShopGui(Language lang, String name, String nameGui, List<ShopItem> items) {
        this.lang = lang;
        this.name = name;
        this.dInventory = INVENTORY_API.createInventory(lang.getMessage(nameGui), 5); //5 строк
        this.items = items;

        this.items.forEach(shopItem -> dInventory.setItem(shopItem.getSlot(), shopItem.getDItem()));
    }
}
