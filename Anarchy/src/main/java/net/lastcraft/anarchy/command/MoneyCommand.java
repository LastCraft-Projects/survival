package net.lastcraft.anarchy.command;

import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.market.api.MarketAPI;
import net.lastcraft.market.api.MarketPlayer;
import net.lastcraft.market.api.MarketPlayerManager;

public class MoneyCommand implements CommandInterface {

    private final MarketPlayerManager marketPlayerManager = MarketAPI.getMarketPlayerManager();

    public MoneyCommand() {
        SpigotCommand command = COMMANDS_API.register("money", this, "деньги", "balance");
        command.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        MarketPlayer marketPlayer = marketPlayerManager.getMarketPlayer(gamer.getName());
        if (marketPlayer == null)
            return;

        double money = marketPlayer.getMoney();
        String end = StringUtil.getCorrectWord((int) money, "MONEY_1", gamerEntity.getLanguage());
        gamerEntity.sendMessageLocale("BALANCE", StringUtil.getNumberFormat(money), end);
    }
}
