package net.lastcraft.market.api;

import net.lastcraft.base.gamer.IBaseGamer;
import net.lastcraft.market.auction.AuctionItemType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.UUID;

public interface AuctionItem {

    UUID getID();

    /**
     * айтем что продается
     */
    ItemStack getItem();

    /**
     * кто выставил на продажу
     * @return - кто именнл
     */
    IBaseGamer getOwner();

    /**
     * цена за которую выставили
     * @return - цена
     */
    int getPrice();

    /**
     * купить айтем
     * @param player - кто покупает
     */
    void buy(Player player);

    /**
     * тип айтема
     * @return - тип
     */
    AuctionItemType getType();

    Date getDate();

    boolean isExpired(); //истекает через 3 дня после старта продаж

    boolean isAvailable();

    void remove();
}
