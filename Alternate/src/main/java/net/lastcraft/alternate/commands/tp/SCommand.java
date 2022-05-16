package net.lastcraft.alternate.commands.tp;

import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.events.UserTeleportByCommandEvent;
import net.lastcraft.alternate.commands.AlternateCommand;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SCommand extends AlternateCommand {

    public SCommand(ConfigData configData) {
        super(configData, true, "s");
        setMinimalGroup(Group.MODERATOR);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        Player player = ((BukkitGamer)gamerEntity).getPlayer();

        if (strings.length == 0) {
            COMMANDS_API.notEnoughArguments(gamerEntity, "S_FORMAT");
            return;
        }

        Player other = Bukkit.getPlayer(strings[0]);
        if (other == null || !other.isOnline()) {
            COMMANDS_API.playerOffline(gamerEntity, strings[0]);
            return;
        }
        User user = USER_MANAGER.getUser(other);
        UserTeleportByCommandEvent event = new UserTeleportByCommandEvent(user, UserTeleportByCommandEvent.Command.S,
                player.getLocation(), player);
        BukkitUtil.callEvent(event);

        if (event.isCancelled())
            return;

        user.teleport(player.getLocation());
    }
}
