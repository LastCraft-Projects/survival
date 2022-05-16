package net.lastcraft.alternate.commands;

import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.packetlib.nms.NmsAPI;
import net.lastcraft.packetlib.nms.interfaces.NmsManager;
import org.bukkit.entity.Player;

public class AnvilCommand extends AlternateCommand {

    private final NmsManager nmsManager = NmsAPI.getManager();

    public AnvilCommand(ConfigData configData) {
        super(configData, true, "anvil");
        setMinimalGroup(configData.getInt("anvilCommand"));
        spigotCommand.setCooldown(60, getCooldownType());
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] strings) {
        Player player = ((BukkitGamer)gamerEntity).getPlayer();

        nmsManager.getAnvil(player).openGui();
    }
}
