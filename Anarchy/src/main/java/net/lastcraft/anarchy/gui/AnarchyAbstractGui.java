package net.lastcraft.anarchy.gui;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.DInventory;
import net.lastcraft.api.inventory.InventoryAPI;
import net.lastcraft.base.locale.Language;
import org.bukkit.entity.Player;

public abstract class AnarchyAbstractGui {

    protected static final InventoryAPI INVENTORY_API = LastCraft.getInventoryAPI();

    protected final DInventory inventory;
    protected final Language lang;

    protected AnarchyAbstractGui(String name, int rows, Language lang) {
        inventory = INVENTORY_API.createInventory(name, rows);
        this.lang = lang;
        this.setItems();
    }

    protected abstract void setItems();

    public final void open(Player player) {
        inventory.openInventory(player);
    }
}
