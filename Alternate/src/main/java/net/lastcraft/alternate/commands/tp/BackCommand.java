package net.lastcraft.alternate.commands.tp;

import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.events.UserTeleportByCommandEvent;
import net.lastcraft.alternate.commands.AlternateCommand;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.alternate.util.TeleportingUtil;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BackCommand extends AlternateCommand {

    public BackCommand(ConfigData configData) {
        super(configData, true, "back", "return");
        setMinimalGroup(configData.getInt("backCommand"));
        spigotCommand.setCooldown(10 * 60, getCooldownType());
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        Player player = ((BukkitGamer)gamerEntity).getPlayer();
        User user = USER_MANAGER.getUser(player);
        if (user == null)
            return;

        Location last = AlternateAPI.getSpawn();
        if (user.getLastLocation() != null && user.getLastLocation().getY() > 0)
            last = user.getLastLocation();

        UserTeleportByCommandEvent event = new UserTeleportByCommandEvent(user, UserTeleportByCommandEvent.Command.BACK,
                user.getLastLocation());
        BukkitUtil.callEvent(event);

        if (event.isCancelled())
            return;

        Location finalLast = last;
        TeleportingUtil.teleport(player, this, () -> {
            if (user.teleport(finalLast)) {
                sendMessageLocale(gamerEntity, "BACK");
            }
        });
    }
}
