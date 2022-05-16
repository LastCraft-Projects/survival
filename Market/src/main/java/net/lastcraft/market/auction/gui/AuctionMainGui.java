package net.lastcraft.market.auction.gui;

import net.lastcraft.base.locale.Language;
import net.lastcraft.market.auction.AuctionItemType;
import net.lastcraft.market.auction.AuctionManager;
import net.lastcraft.market.utils.MarketUtil;

public class AuctionMainGui extends AuctionAbstractGui {

    private int amount;

    public AuctionMainGui(AuctionManager manager, Language lang) {
        super(manager, lang, lang.getMessage("AUCTION_MAINGUI_NAME"));

        setItems();
    }

    @Override
    protected void setItems() {
        amount = MarketUtil.setItems(manager, inventory, amount,
                                    manager.getAllItems().values(), lang,
                                    AuctionItemType.ALL, true);
    }
}
