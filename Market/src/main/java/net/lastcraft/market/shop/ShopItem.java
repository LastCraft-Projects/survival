package net.lastcraft.market.shop;

import lombok.Getter;
import lombok.ToString;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.DItem;
import net.lastcraft.api.inventory.InventoryAPI;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.sound.SoundAPI;
import net.lastcraft.api.sound.SoundType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.market.utils.MarketUtil;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@Getter
@ToString
public class ShopItem {
    private static final SoundAPI SOUND_API = LastCraft.getSoundAPI();
    private static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();
    private static final InventoryAPI INVENTORY_API = LastCraft.getInventoryAPI();

    private final int slot;
    private final DItem dItem;
    private final ItemStack itemStack;
    private final int amount;
    private final double buyPrice;
    private final double sellPrice;
    private final String name;

    private final ItemStack itemNotChanged;

    public ShopItem(int slot, ItemStack itemStack, int amount, double buyPrice,
                    double sellPrice, String name, ItemStack item) {
        this.name = name;
        this.slot = slot;
        this.amount = amount;
        this.itemStack = itemStack;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.itemNotChanged = item;
        this.dItem = INVENTORY_API.createItem(itemStack, (player, clickType, i) -> {
            BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
            if (gamer == null)
                return;

            Language lang = gamer.getLanguage();

            if (clickType == ClickType.MIDDLE && sellPrice > 0) {
                MarketUtil.sellAllItems(player, this);
                return;
            }

            ShopGuiConfirmed shopGuiConfirmed;
            if (clickType.isLeftClick()) {
                shopGuiConfirmed = new ShopGuiConfirmed(name, lang, "SHOP_GUIBUY_NAME",
                        this, player, true);
            } else {
                shopGuiConfirmed = new ShopGuiConfirmed(name, lang, "SHOP_GUISELL_NAME",
                        this, player, false);
            }

            shopGuiConfirmed.update();

            if ((clickType.isLeftClick() && buyPrice > 0)
                    || (clickType.isRightClick() && sellPrice > 0)) {
                shopGuiConfirmed.open();
            }

            SOUND_API.play(player, SoundType.NO);
        });
    }

    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    //это айтем без всего говна
    public ItemStack getItem() {
        return itemNotChanged.clone();
    }
}
