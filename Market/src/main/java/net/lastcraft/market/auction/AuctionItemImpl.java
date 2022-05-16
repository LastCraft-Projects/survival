package net.lastcraft.market.auction;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.base.gamer.IBaseGamer;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import net.lastcraft.market.api.AuctionItem;
import net.lastcraft.market.api.MarketAPI;
import net.lastcraft.market.api.MarketPlayer;
import net.lastcraft.market.api.MarketPlayerManager;
import net.lastcraft.market.api.event.PlayerBuyAuctionItemEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AuctionItemImpl implements AuctionItem {

    private static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();
    private static final MarketPlayerManager MARKET_PLAYER_MANAGER = MarketAPI.getMarketPlayerManager();

    private final AuctionManager manager;
    private final UUID uuid;
    private final IBaseGamer gamer;
    private final ItemStack item;
    private final int price;
    private final AuctionItemType typeItem;
    private final Timestamp date;

    private boolean available = true;

    public AuctionItemImpl(AuctionManager manager, @NotNull UUID uuid, @NotNull IBaseGamer gamer,
                           ItemStack item, int price, Timestamp date) {
        this.manager = manager;
        this.uuid = uuid;
        this.gamer = gamer;
        this.item = item;
        this.price = price;
        this.typeItem = AuctionItemType.getType(item);
        this.date = date;
    }

    @Override
    public ItemStack getItem() {
        return item.clone();
    }

    @Override
    public UUID getID() {
        return uuid;
    }

    @Override
    public IBaseGamer getOwner() {
        IBaseGamer gamer = GAMER_MANAGER.getGamer(this.gamer.getName());
        if (gamer == null)
            return this.gamer;
        return gamer;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public void buy(Player player) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        MarketPlayer marketPlayer = MARKET_PLAYER_MANAGER.getMarketPlayer(player);
        if (gamer == null || marketPlayer == null)
            return;

        if (!manager.getAllItems().containsKey(uuid)) {
            gamer.sendMessageLocale("AUCTION_ITEM_ALLREADY_SELLED");
            return;
        }

        if (!marketPlayer.hasMoney(price)) {
            gamer.sendMessageLocale("AUCTION_NO_MONEY");
            return;
        }

        PlayerBuyAuctionItemEvent event = new PlayerBuyAuctionItemEvent(player, this);
        BukkitUtil.callEvent(event);

        if (event.isCancelled())
            return;

        marketPlayer.changeMoney(-price);

        gamer.sendMessageLocale("AUCTION_BUY", item.getType().toString(),
                item.getAmount(), StringUtil.getNumberFormat(price));

        PlayerInventory playerInventory = player.getInventory();
        playerInventory.addItem(item.clone());

        available = false;
        manager.remove(this);

        BukkitUtil.runTaskAsync(() -> {
            MarketPlayer seller = MARKET_PLAYER_MANAGER.getOrCreate(this.gamer);
            seller.changeMoney(price);

            BukkitGamer sellerGamer = GAMER_MANAGER.getGamer(this.gamer.getName());
            if (sellerGamer == null)
                return;

            sellerGamer.sendMessageLocale("AUCTION_BUY_SELLER", gamer.getDisplayName(), item.getType().toString(),
                    item.getAmount(), StringUtil.getNumberFormat(price));
        });

    }

    @Override
    public AuctionItemType getType() {
        return typeItem;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public boolean isExpired() {
        return System.currentTimeMillis() > date.getTime() + TimeUnit.DAYS.toMillis(3);
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public void remove() {
        available = false;
        manager.remove(this);
    }
}
