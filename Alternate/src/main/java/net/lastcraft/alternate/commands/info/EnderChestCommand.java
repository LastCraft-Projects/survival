package net.lastcraft.alternate.commands.info;

import net.lastcraft.alternate.commands.AlternateCommand;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import org.bukkit.entity.Player;

public class EnderChestCommand extends AlternateCommand {

    public EnderChestCommand(ConfigData configData) {
        super(configData, true, "enderchest", "ender", "ec", "chest");
        spigotCommand.setMinimalGroup(configData.getInt("enderChestCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] args) {
        Player player = ((BukkitGamer)gamerEntity).getPlayer();
        player.openInventory(player.getEnderChest());
    }
}
