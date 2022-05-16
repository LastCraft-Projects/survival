package net.lastcraft.alternate.commands.tp;

import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.events.UserTeleportByCommandEvent;
import net.lastcraft.alternate.commands.AlternateCommand;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.alternate.util.LocationUtil;
import net.lastcraft.alternate.util.TeleportingUtil;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TopCommand extends AlternateCommand {

    public TopCommand(ConfigData configData) {
        super(configData, true, "top");
        setMinimalGroup(configData.getInt("topCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        Player player = ((BukkitGamer)gamerEntity).getPlayer();
        User user = USER_MANAGER.getUser(player);
        if (user == null)
            return;

        final int topX = player.getLocation().getBlockX();
        final int topZ = player.getLocation().getBlockZ();
        final float pitch = player.getLocation().getPitch();
        final float yaw = player.getLocation().getYaw();
        final Location loc;
        try {
            loc = LocationUtil.getSafeDestination(new Location(player.getWorld(), topX,
                    player.getWorld().getMaxHeight(), topZ, yaw, pitch));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        UserTeleportByCommandEvent event = new UserTeleportByCommandEvent(user,
                UserTeleportByCommandEvent.Command.TOP, loc);
        BukkitUtil.callEvent(event);

        if (!event.isCancelled()) {
            TeleportingUtil.teleport(player, this, () -> user.teleport(loc));
        }

    }
}
