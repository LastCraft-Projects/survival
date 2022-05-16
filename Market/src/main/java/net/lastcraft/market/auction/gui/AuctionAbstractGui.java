package net.lastcraft.market.auction.gui;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.InventoryAPI;
import net.lastcraft.api.inventory.MultiInventory;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.base.locale.Language;
import net.lastcraft.market.auction.AuctionManager;
import org.bukkit.entity.Player;

public abstract class AuctionAbstractGui {
    protected static final InventoryAPI INVENTORY_API = LastCraft.getInventoryAPI();
    protected static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();

    protected final AuctionManager manager;
    protected final Language lang;
    protected final MultiInventory inventory;

    AuctionAbstractGui(AuctionManager manager, Language lang, String name) {
        this.manager = manager;
        this.lang = lang;
        inventory = INVENTORY_API.createMultiInventory(name, 6);
        update();
    }

    public void update() {
        if (inventory == null)
            return;

        setItems();
    }

    public void open(Player player) {
        if (inventory == null)
            return;

        inventory.openInventory(player);
    }

    protected abstract void setItems();
}
