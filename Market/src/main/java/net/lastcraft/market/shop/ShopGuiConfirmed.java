package net.lastcraft.market.shop;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.DInventory;
import net.lastcraft.api.inventory.InventoryAPI;
import net.lastcraft.api.sound.SoundAPI;
import net.lastcraft.api.sound.SoundType;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.locale.Language;
import net.lastcraft.market.Market;
import net.lastcraft.market.api.MarketAPI;
import net.lastcraft.market.api.MarketPlayer;
import net.lastcraft.market.api.MarketPlayerManager;
import net.lastcraft.market.utils.MarketUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class ShopGuiConfirmed {
    private static final InventoryAPI API = LastCraft.getInventoryAPI();
    private static final SoundAPI SOUND_API = LastCraft.getSoundAPI();
    private static final MarketPlayerManager MARKET_PLAYER_MANAGER = MarketAPI.getMarketPlayerManager();

    private final String nameShop;
    private final DInventory dInventory;
    private final ShopItem shopItem;
    private final Language lang;
    private final Player player;
    private final boolean buy;
    private final MarketPlayer marketPlayer;

    private int finalAmount;

    ShopGuiConfirmed(String nameShop, Language lang, String keyNameGui, ShopItem shopItem, Player player, boolean buy) {
        this.nameShop = nameShop;
        this.marketPlayer = MARKET_PLAYER_MANAGER.getMarketPlayer(player);
        this.buy = buy;
        this.player = player;
        this.lang = lang;
        this.shopItem = shopItem;
        this.finalAmount = shopItem.getAmount();

        String itemName = shopItem.getDItem().getItem().getData().getItemType().toString();
        String name = lang.getMessage(keyNameGui, itemName);
        this.dInventory = API.createInventory(player, name,5);
    }

    void update() {
        dInventory.clearInventory(); //заранее чистим весь инв

        ItemStack itemStackCenter = shopItem.getItemStack();
        ItemMeta meta = itemStackCenter.getItemMeta();

        if (buy) {
            List<String> lore = new ArrayList<>(lang.getList("SHOP_ITEMBUY_LORE",
                    String.valueOf("§c" + finalAmount * shopItem.getBuyPrice())));
            if (!marketPlayer.hasMoney(shopItem.getBuyPrice() * finalAmount))
                lore.addAll(lang.getList("SHOP_ITEM_NO_MONEY"));

            meta.setLore(lore);
        } else {
            List<String> lore = new ArrayList<>();
            if (MarketUtil.contains(player, itemStackCenter)) {
                lore.addAll(lang.getList("SHOP_ITEM_NO_ITEMS"));
            } else {
                lore.addAll(lang.getList( "SHOP_ITEMSELL_LORE", "§a" +
                        NumberFormat.getNumberInstance(Locale.US)
                                .format(Market.round(finalAmount * shopItem.getSellPrice()))));

            }
            meta.setLore(lore);
        }
        itemStackCenter.setItemMeta(meta);
        itemStackCenter.setAmount(finalAmount);
        dInventory.setItem(13, API.createItem(itemStackCenter));

        if (finalAmount > 1) {
            dInventory.setItem(9, API.createItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE) //поставить 1
                            .setDurability((short) 14)
                            .setName(lang.getMessage("SHOP_NAME_REMOVE_1"))
                            .setLore(lang.getList("SHOP_LORE_CHANGED"))
                            .build(), (player, clickType, i) -> {
                finalAmount = 1;
                update();
            }));
        }

        if (finalAmount > 10) {
            dInventory.setItem(10, API.createItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE) //убрать 10
                            .setDurability((short) 14)
                            .setName(lang.getMessage("SHOP_NAME_REMOVE_2"))
                            .setLore(lang.getList( "SHOP_LORE_CHANGED"))
                            .build(), (player, clickType, i) -> {
                finalAmount -= 10;
                update();
            }));
        }

        if (finalAmount > 1) {
            dInventory.setItem(11, API.createItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE) //убрать 1
                            .setDurability((short) 14)
                            .setName(lang.getMessage( "SHOP_NAME_REMOVE_3"))
                            .setLore(lang.getList("SHOP_LORE_CHANGED"))
                            .build(), (player, clickType, i) -> {
                finalAmount -= 1;
                update();
            }));
        }

        if (finalAmount < 64) {
            dInventory.setItem(15, API.createItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE) //добавить 1
                            .setDurability((short) 5)
                            .setName(lang.getMessage("SHOP_NAME_ADD_1"))
                            .setLore(lang.getList("SHOP_LORE_CHANGED"))
                            .build(), (player, clickType, i) -> {
                finalAmount += 1;
                update();
            }));
            dInventory.setItem(17, API.createItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE) //выставить 64
                            .setDurability((short) 5)
                            .setName(lang.getMessage("SHOP_NAME_ADD_3"))
                            .setLore(lang.getList("SHOP_LORE_CHANGED"))
                            .build(), (player, clickType, i) -> {
                finalAmount = 64;
                update();
            }));
        }

        if (finalAmount < 54) {
            dInventory.setItem(16, API.createItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE) //добавить 10
                            .setDurability((short) 5)
                            .setName(lang.getMessage( "SHOP_NAME_ADD_2"))
                            .setLore(lang.getList("SHOP_LORE_CHANGED"))
                            .build(), (player, clickType, i) -> {
                finalAmount += 10;
                update();
            }));
        }

        String name = "§c" + lang.getMessage("CONFIRMED_NAME");
        boolean click = false;
        if (buy && marketPlayer.hasMoney(shopItem.getBuyPrice() * finalAmount)) {
            click = true;
            name = "§a" + lang.getMessage("CONFIRMED_NAME");
        }
        if (!buy && MarketUtil.getItemsAmount(player, shopItem.getItemStack()) >= finalAmount) {
            click = true;
            name = "§a" + lang.getMessage("CONFIRMED_NAME");
        }
        boolean finalClick = click;
        dInventory.setItem(3 * 9 - 1 + 4, API.createItem(ItemUtil.getBuilder(Material.STAINED_GLASS) //кнопка подтвердить
                        .setDurability((short) 5)
                        .setName(name)
                        .build(), (player, clickType, i) -> {
            if (!finalClick)
                return;
            SOUND_API.play(player, SoundType.CHEST_OPEN);
            MarketUtil.changeItems(player, buy, shopItem, finalAmount);
            update();
        }));

        dInventory.setItem(3 * 9 - 1 + 6, API.createItem(ItemUtil.getBuilder(Material.STAINED_GLASS) //кнопка назад
                        .setDurability((short) 14)
                        .setName(lang.getMessage("CANCEL_NAME"))
                        .setLore(lang.getList("PROFILE_BACK_ITEM_LORE2"))
                        .build(), (player, clickType, i) -> {
            player.chat("/shop " + nameShop);
            SOUND_API.play(player, SoundType.PICKUP);
        }));

        if (!buy) {
            int amountInPlayer = MarketUtil.getItemsAmount(player, shopItem.getItemStack()); //кнопка продать все
            if (amountInPlayer > 0) {
                dInventory.setItem(4 * 9 - 1 + 5, API.createItem(ItemUtil.getBuilder(Material.STAINED_GLASS)
                                .setDurability((short) 1)
                                .setName("§a" + lang.getMessage("SHOP_NAME_SELL_ALL"))
                                .setLore(lang.getList( "SHOP_LORE_SELL_ALL",
                                        amountInPlayer * shopItem.getSellPrice()))
                                .build(), (player, clickType, i) -> {
                    MarketUtil.sellAllItems(player, shopItem);
                    SOUND_API.play(player, SoundType.DESTROY);
                    update();
                }));
            } else {
                dInventory.setItem(4 * 9 - 1 + 5, API.createItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                        .setDurability((short) 14)
                        .setName("§c" + lang.getMessage("SHOP_NAME_SELL_ALL"))
                        .setLore(lang.getList("SHOP_ITEM_NO_ITEMS"))
                        .build()));
            }
        }
    }

    void open() {
        dInventory.openInventory(player);
    }
}
