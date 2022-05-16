package net.lastcraft.market.utils;

import lombok.experimental.UtilityClass;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.InventoryAPI;
import net.lastcraft.api.inventory.MultiInventory;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.sound.SoundAPI;
import net.lastcraft.api.sound.SoundType;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.base.util.TimeUtil;
import net.lastcraft.dartaapi.guis.CustomItems;
import net.lastcraft.market.Market;
import net.lastcraft.market.api.AuctionItem;
import net.lastcraft.market.api.MarketAPI;
import net.lastcraft.market.api.MarketPlayer;
import net.lastcraft.market.api.MarketPlayerManager;
import net.lastcraft.market.auction.AuctionAcceptGui;
import net.lastcraft.market.auction.AuctionItemType;
import net.lastcraft.market.auction.AuctionManager;
import net.lastcraft.market.auction.gui.AuctionPlayerGui;
import net.lastcraft.market.shop.ShopItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@UtilityClass
public class MarketUtil {

    private final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();
    private final MarketPlayerManager MARKET_PLAYER_MANAGER = MarketAPI.getMarketPlayerManager();
    private final SoundAPI SOUND_API = LastCraft.getSoundAPI();
    private final InventoryAPI INVENTORY_API = LastCraft.getInventoryAPI();

    public int getItemsAmount(Player player, ItemStack itemStack) {
        PlayerInventory inventory = player.getInventory();
        if (!inventory.contains(itemStack.getType()))
            return 0;

        int amount = 0;
        for (ItemStack content : inventory.getContents()) {
            if (content == null)
                continue;
            if (inventory.getItemInOffHand() != null && inventory.getItemInOffHand().getType() == content.getType())
                continue;
            if (inventory.getHelmet() != null && inventory.getHelmet().getType() == content.getType())
                continue;
            if (content.getType() == itemStack.getType()
                    && content.getDurability() == itemStack.getDurability()) {
                ItemMeta meta = content.getItemMeta();
                if (meta != null && meta.getDisplayName() != null)
                    continue;

                amount += content.getAmount();
            }
        }
        return amount;
    }

    public int setItems(AuctionManager manager, MultiInventory inventory, int amountOld,
                         Collection<AuctionItem> auctionItems, Language lang,
                         AuctionItemType type, boolean button) {

        List<AuctionItem> collect = auctionItems.stream()
                .sorted(Comparator.comparingLong(value -> value.getDate().getTime()))
                .collect(Collectors.toList());

        if (amountOld != collect.size()) {
            inventory.clearInventories();
        }

        int slot = 10;
        int page = 0;
        for (AuctionItem auctionItem : collect) {

            if (auctionItem.isExpired() || !manager.getAllItems().containsKey(auctionItem.getID()))
                continue;

            ItemStack itemStack = ItemUtil.getBuilder(auctionItem.getItem())
                    .addLore(lang.getList( "AUCTION_LORE_ITEM",
                            auctionItem.getOwner().getDisplayName(),
                            StringUtil.getNumberFormat(auctionItem.getPrice()),
                            TimeUtil.leftTime(lang,auctionItem.getDate().getTime()
                                    + TimeUnit.DAYS.toMillis(3), true))
                    ).build();

            int finalPage = page;
            inventory.setItem(page, slot++, INVENTORY_API.createItem(itemStack, (player, clickType, i) -> {
                BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
                if (gamer == null)
                    return;

                MarketPlayer marketPlayer = MARKET_PLAYER_MANAGER.getMarketPlayer(player);
                if (marketPlayer == null)
                    return;

                if (clickType.isRightClick()) {
                    AuctionPlayerGui playerGui = manager.getPlayerGui(gamer, auctionItem.getOwner());
                    playerGui.open(player);
                    return;
                }

                if (auctionItem.getOwner().getPlayerID() == gamer.getPlayerID()) {
                    gamer.sendMessageLocale("AUCTION_BUY_YOU_ERROR");
                    SOUND_API.play(player, SoundType.NO);
                    return;
                }
                if (!manager.getAllItems().containsKey(auctionItem.getID()) || !auctionItem.isAvailable()) {
                    gamer.sendMessageLocale("AUCTION_ITEM_ALLREADY_SELLED");
                    return;
                }
                if (!marketPlayer.hasMoney(auctionItem.getPrice())) {
                    gamer.sendMessageLocale("AUCTION_NO_MONEY");
                    return;
                }

                if (MarketUtil.isFull(player.getInventory())) {
                    SOUND_API.play(player, SoundType.NO);
                    gamer.sendMessageLocale("INVENTORY_IS_FULL");
                    player.closeInventory();
                    return;
                }

                AuctionAcceptGui acceptGui = new AuctionAcceptGui(player, auctionItem);
                acceptGui.open(() -> {
                    auctionItem.buy(player);
                    SOUND_API.play(player, SoundType.SELECTED);
                    player.closeInventory();
                    //manager.updateGuis();
                }, () -> inventory.openInventory(player, finalPage));
            }));

            if ((slot - 8) % 9 == 0)
                slot += 2;

            if (slot >= 44) {
                slot = 10;
                page++;
            }
        }

        if (button) {
            setTypeItem(manager, inventory, type, lang);
        }

        setTypeMyGuiItem(manager, inventory, lang);

        if (slot == 10 && page == 0) {
            inventory.setItem(0, 22, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.BARRIER)
                    .setName("§c" + lang.getMessage("AUCTION_NO_ITEMS_NAME"))
                    .setLore(lang.getList("AUCTION_NO_ITEMS_LORE"))
                    .build(), (player, clickType, i) -> SOUND_API.play(player, SoundType.NO)));
            return 0;
        }

        INVENTORY_API.pageButton(lang, page + 1, inventory, 47, 51);

        return collect.size();
    }

    private void setTypeMyGuiItem(AuctionManager manager, MultiInventory inventory, Language lang) {
        inventory.getInventories().forEach(dInventory ->
                dInventory.setItem(49, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.BED)
                        .setDurability((short) 1)
                        .setName("§c" + lang.getMessage( "AUCTION_MYGUI_ITEM_NAME"))
                        .setLore(lang.getList( "AUCTION_MYGUI_ITEM_LORE"))
                        .build(), (player, clickType, i) -> {
                    BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
                    if (gamer == null)
                        return;
                    manager.getOrCreateMyGui(player).open();
                })));
    }

    private void setTypeItem(AuctionManager manager, MultiInventory inventory, AuctionItemType type, Language lang) {
        inventory.getInventories().forEach(dInventory ->
                dInventory.setItem(53, INVENTORY_API.createItem(ItemUtil.getBuilder(type.getItem())
                .setName("§c" + lang.getMessage( "AUCTION_CATEGORY_NAME"))
                .setLore(lang.getList("AUCTION_CATEGORY_LORE", type.getName(lang)))
                .build(), (player, clickType, i) -> {
            BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
            if (gamer == null)
                return;
            manager.openTypeMainGui(gamer);
        })));
    }

    public boolean contains(Player player, ItemStack itemStack) {
        return getItemsAmount(player, itemStack) <= 0;
    }

    public void removeItems(Player player, ItemStack itemStack, int amount) {
        PlayerInventory inventory = player.getInventory();

        for (ItemStack content : inventory.getContents()) {
            if (amount <= 0)
                break;

            if (content == null || content.getType() == Material.AIR)
                continue;
            if (inventory.getItemInOffHand() != null && inventory.getItemInOffHand().getType() == itemStack.getType())
                continue;
            if (inventory.getHelmet() != null && inventory.getHelmet().getType() == itemStack.getType())
                continue;

            if (!content.isSimilar(itemStack))
                continue;

            ItemStack removeItem = content.clone();
            if (removeItem.getAmount() >= amount) {
                removeItem.setAmount(amount);
            }

            inventory.removeItem(removeItem);
            amount -= removeItem.getAmount();
        }
    }

    public void sellAllItems(Player player, ShopItem shopItem) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        int amount = getItemsAmount(player, shopItem.getItem());
        if (amount == 0)
            return;

        removeItems(player, shopItem.getItem(), amount);

        MarketPlayer marketPlayer = MARKET_PLAYER_MANAGER.getMarketPlayer(player);
        marketPlayer.changeMoney(amount * shopItem.getSellPrice());

        gamer.sendMessageLocale("SHOP_ALL_ITEMS_SELLED",
                shopItem.getItemStack().getType().toString(),
                StringUtil.getNumberFormat(amount),
                String.valueOf(amount * shopItem.getSellPrice()));
    }

    public void changeItems(Player player, boolean buy, ShopItem shopItem, int amount) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        MarketPlayer marketPlayer = MARKET_PLAYER_MANAGER.getMarketPlayer(player);
        if (marketPlayer == null || gamer == null)
            return;

        ItemStack item = ItemUtil.getBuilder(shopItem.getItem())
                .setAmount(amount)
                .build();

        if (buy) {
            if (MarketUtil.isFull(player.getInventory())) {
                SOUND_API.play(player, SoundType.NO);
                gamer.sendMessageLocale("INVENTORY_IS_FULL");
                player.closeInventory();
                return;
            }

            player.getInventory().addItem(item);
            marketPlayer.changeMoney(-(shopItem.getBuyPrice() * amount));

            gamer.sendMessageLocale("SHOP_ALL_ITEMS_BUY",
                    shopItem.getItemStack().getType().toString(),
                    StringUtil.getNumberFormat(amount),
                    String.valueOf(amount * shopItem.getBuyPrice()));

            return;
        }

        removeItems(player, shopItem.getItem(), amount);
        marketPlayer.changeMoney(Market.round(shopItem.getSellPrice() * amount));

        gamer.sendMessageLocale("SHOP_ALL_ITEMS_SELLED",
                shopItem.getItemStack().getType().toString(),
                StringUtil.getNumberFormat(amount),
                String.valueOf(amount * shopItem.getSellPrice()));
    }

    public boolean isFull(PlayerInventory playerInventory) {
        int amount = 36;
        for (ItemStack itemStack : playerInventory.getStorageContents()) {
            if (itemStack == null || itemStack.getType() == Material.AIR)
                continue;

            amount--;
        }
        return amount == 0;
    }

    public void setBack(AuctionManager manager, MultiInventory inventory, Language lang) {
        inventory.getInventories().forEach(inv -> inv.setItem(49,
                INVENTORY_API.createItem(CustomItems.getBack(lang), (player, clickType, i) -> {
                    BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
                    if (gamer == null)
                        return;
                    manager.openMainGui(gamer);
                })));
    }
}
