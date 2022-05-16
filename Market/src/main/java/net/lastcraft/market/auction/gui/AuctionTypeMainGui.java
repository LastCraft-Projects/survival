package net.lastcraft.market.auction.gui;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.DInventory;
import net.lastcraft.api.inventory.InventoryAPI;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.locale.Language;
import net.lastcraft.dartaapi.guis.CustomItems;
import net.lastcraft.market.auction.AuctionItemType;
import net.lastcraft.market.auction.AuctionManager;
import org.bukkit.entity.Player;

public class AuctionTypeMainGui {

    private static final InventoryAPI INVENTORY_API = LastCraft.getInventoryAPI();
    private static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();

    private final AuctionManager manager;
    private final DInventory inventory;
    private final Language lang;

    public AuctionTypeMainGui(AuctionManager auctionManager, Language lang) {
        this.manager = auctionManager;
        this.lang = lang;
        this.inventory = INVENTORY_API.createInventory(lang.getMessage("AUCTION_MAINGUI_NAME"), 6);

        setItems();
    }

    private void setItems() {
        int slot = 10;
        for (AuctionItemType type : AuctionItemType.values()) {
            if (type == AuctionItemType.ALL)
                continue;
            inventory.setItem(slot++, INVENTORY_API.createItem(ItemUtil.getBuilder(type.getItem())
                    .removeFlags()
                    .setName("§a" + type.getName(lang))
                    .setLore(lang.getList("AUCTION_SUB_TYPE_LORE"))
                    .build(), (player, clickType, i) -> {
                BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
                if (gamer == null)
                    return;
                manager.openTypeGui(gamer, type);
            }));

            if ((slot - 8) % 9 == 0)
                slot += 2;

        }

        inventory.setItem(49, INVENTORY_API.createItem(CustomItems.getBack2(lang), (player, clickType, i) -> {
            BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
            if (gamer == null)
                return;
            manager.openMainGui(gamer);
        }));
    }

    public void open(Player player) {
        if (inventory == null)
            return;
        inventory.openInventory(player);
    }
}
