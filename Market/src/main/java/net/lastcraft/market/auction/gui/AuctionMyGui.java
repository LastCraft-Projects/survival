package net.lastcraft.market.auction.gui;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.InventoryAPI;
import net.lastcraft.api.inventory.MultiInventory;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.sound.SoundAPI;
import net.lastcraft.api.sound.SoundType;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.TimeUtil;
import net.lastcraft.market.api.AuctionItem;
import net.lastcraft.market.auction.AuctionManager;
import net.lastcraft.market.utils.MarketUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AuctionMyGui {

    //private static final ItemStack GLASS = ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
    //        .setDurability((short) 14)
    //        .build();

    private static final InventoryAPI INVENTORY_API = LastCraft.getInventoryAPI();
    private static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();
    private static final SoundAPI SOUND_API = LastCraft.getSoundAPI();

    private final AuctionManager manager;
    private BukkitGamer gamer;
    private MultiInventory inventory;
    private Player player;

    public AuctionMyGui(AuctionManager auctionManager, Player player) {
        this.manager = auctionManager;
        gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        this.player = player;
        Language lang = gamer.getLanguage();
        inventory = INVENTORY_API.createMultiInventory(player,
                lang.getMessage("AUCTION_GUI_NAME", player.getName()), 6);

        update();
    }

    public void update() {
        if (gamer == null || inventory == null)
            return;

        setItems();
    }

    private void setItems() {
        inventory.clearInventories();

        Language lang = gamer.getLanguage();

        int slot = 10;
        int page = 0;
        for (AuctionItem auctionItem : manager.getAllItems().values()
                .stream()
                .filter(auctionItem -> auctionItem.getOwner().getPlayerID() == gamer.getPlayerID())
                .sorted(Comparator.comparingLong(value -> value.getDate().getTime()))
                .collect(Collectors.toList())) {
            boolean expired = auctionItem.isExpired();
            String time = TimeUtil.leftTime(lang,auctionItem.getDate().getTime()
                    + TimeUnit.DAYS.toMillis(3), true);

            inventory.setItem(page, slot++, INVENTORY_API.createItem(ItemUtil
                    .getBuilder(auctionItem.getItem())
                    .glowing(expired)
                    .setLore(lang.getList("AUCTION_LORE_MYGUI", auctionItem.getPrice(),
                            (expired ? lang.getMessage("AUCTION_NO_AUCTION") : time)))
                    .build(), (player, clickType, i) -> {

                if (clickType.isLeftClick())
                    return;

                BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
                if (gamer == null)
                    return;

                if (MarketUtil.isFull(player.getInventory())) {
                    SOUND_API.play(player, SoundType.NO);
                    gamer.sendMessageLocale("INVENTORY_IS_FULL");
                    player.closeInventory();
                    return;
                }

                if (!auctionItem.isAvailable())
                    return;

                auctionItem.remove();
                //manager.updateGuis();
                gamer.sendMessageLocale("AUCTION_REMOVED_ITEM");
                player.getInventory().addItem(auctionItem.getItem());
            }));

            if ((slot - 8) % 9 == 0)
                slot += 2;

            if (slot >= 44) {
                slot = 10;
                page++;
            }
        }

        MarketUtil.setBack(manager, inventory, lang);

        if (slot == 10 && page == 0) {
            inventory.setItem(0, 22, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.BARRIER)
                    .setName("§c" + lang.getMessage("AUCTION_NO_ITEMS_NAME"))
                    .setLore(lang.getList("AUCTION_NO_ITEMS_LORE"))
                    .build(), (player, clickType, i) -> SOUND_API.play(player, SoundType.NO)));
            return;
        }

        INVENTORY_API.pageButton(lang, page + 1, inventory, 47, 51);
    }

    public void open() {
        inventory.openInventory(player);
    }
}
