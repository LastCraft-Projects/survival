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

public class JumpCommand extends AlternateCommand {

    public JumpCommand(ConfigData configData) {
        super(configData, true, "jump");
        setMinimalGroup(configData.getInt("jumpCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();
        User user = USER_MANAGER.getUser(player);
        if (user == null)
            return;

        final Location location = player.getLocation();

        Location loc = LocationUtil.getTarget(player);
        loc.setYaw(location.getYaw());
        loc.setPitch(location.getPitch());
        loc.setY(loc.getY() + 1);

        UserTeleportByCommandEvent event = new UserTeleportByCommandEvent(user,
                UserTeleportByCommandEvent.Command.JUMP, loc);
        BukkitUtil.callEvent(event);

        if (event.isCancelled())
            return;

        TeleportingUtil.teleport(player, this, () -> user.teleport(loc));
    }
}
