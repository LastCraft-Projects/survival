package net.lastcraft.alternate.commands;

import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import org.bukkit.entity.Player;

public class WorkbenchCommand extends AlternateCommand {

    public WorkbenchCommand(ConfigData configData) {
        super(configData, true, "workbench", "wb");
        setMinimalGroup(configData.getInt("workBenchCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();
        player.openWorkbench(null, true);
    }
}
