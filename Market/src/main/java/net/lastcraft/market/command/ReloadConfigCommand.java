package net.lastcraft.market.command;

import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.market.Market;

public final class ReloadConfigCommand implements CommandInterface {

    private final Market market;

    public ReloadConfigCommand(Market market) {
        this.market = market;

        SpigotCommand command = COMMANDS_API.register("shopreload", this);
        command.setMinimalGroup(Group.ADMIN);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        gamerEntity.sendMessage("§6Market §8| §fПерезагружено!");
        market.reloadConfig();
    }
}
