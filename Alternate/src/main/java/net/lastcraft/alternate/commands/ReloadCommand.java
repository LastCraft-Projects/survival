package net.lastcraft.alternate.commands;

import net.lastcraft.alternate.Alternate;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.gamer.constans.Group;

public class ReloadCommand extends AlternateCommand {

    private final Alternate alternate;

    public ReloadCommand(Alternate alternate) {
        super(Alternate.getConfigData(), false, "alternate");
        this.alternate = alternate;
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] strings) {
        sendMessage(gamerEntity, "Plugin Alternate by LastCraft v" + alternate.getDescription().getVersion());
        if (strings.length == 1 && strings[0].equalsIgnoreCase("reload")) {
            if (!gamerEntity.isHuman() ||((BukkitGamer)gamerEntity).getGroup() == Group.ADMIN) {
                Alternate.getConfigData().load();
                gamerEntity.sendMessage("Конфиг плагина Alternate был перегружен!");
                gamerEntity.sendMessage("Если вы отключили или включили какую-то из параметров *System, то оно не будет работать");
                gamerEntity.sendMessage("Нужен полный рестарт сервера. Сожелею(");
            }
        }

        //todo сделать при релоаде отключение команд и норм конфиг
    }
}
