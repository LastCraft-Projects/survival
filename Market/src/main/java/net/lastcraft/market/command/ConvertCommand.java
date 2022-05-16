package net.lastcraft.market.command;

import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.market.api.MarketAPI;
import net.lastcraft.market.api.MarketPlayer;
import net.lastcraft.market.api.MarketPlayerManager;

public final class ConvertCommand implements CommandInterface {

    private final MarketPlayerManager marketPlayerManager = MarketAPI.getMarketPlayerManager();

    public ConvertCommand() {
        SpigotCommand command = COMMANDS_API.register("convert", this);
        command.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] args) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;

        if (args.length < 1) {
            COMMANDS_API.notEnoughArguments(gamerEntity, "CONVERT_FORMAT");
            return;
        }

        int sum;
        try {
            sum = Integer.parseInt(args[0]);
        } catch (Exception e) {
            gamer.sendMessageLocale("CONVERT_ERROR");
            return;
        }

        if (sum < 1) {
            gamer.sendMessageLocale("CONVERT_ERROR");
            return;
        }

        if (!gamer.changeGold(-sum)) {
            return;
        }

        MarketPlayer marketPlayer = marketPlayerManager.getMarketPlayer(gamer.getPlayerID());
        if (marketPlayer == null) {
            return;
        }

        int total = sum * 300;  //за 1 золото даем 300 монет на сурвачах
        marketPlayer.changeMoney(total);
        gamer.sendMessageLocale("CONVERT",
                StringUtil.getNumberFormat(sum),
                StringUtil.getNumberFormat(total));
    }
}
