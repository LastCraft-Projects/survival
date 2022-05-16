package net.lastcraft.alternate.commands;

import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.alternate.gui.PTimeGui;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import org.bukkit.entity.Player;

public class PTimeCommand extends AlternateCommand {

    public PTimeCommand(ConfigData configData) {
        super(configData, true, "ptime", "playertime");
        setMinimalGroup(configData.getInt("ptimeCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        if (strings.length == 0) {
            PTimeGui gui = GUI_MANAGER.getGui(PTimeGui.class, player);
            gui.open();
            return;
        }

        if (strings[0].equalsIgnoreCase("reset")) {
            sendMessageLocale(gamerEntity, "PTIME_RESET");
            player.resetPlayerWeather();
            player.resetPlayerTime();
            return;
        }

        long time;
        try {
            time = Integer.valueOf(strings[0]);
        } catch (Exception e) {
            player.resetPlayerTime();
            player.resetPlayerWeather();
            gamerEntity.sendMessageLocale("PTIME_ERROR");
            return;
        }
        sendMessageLocale(gamerEntity, "PTIME", time);
        player.setPlayerTime(time, false);
    }
}
