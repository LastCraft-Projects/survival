package net.lastcraft.alternate.commands.tp;

import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.events.UserTeleportByCommandEvent;
import net.lastcraft.alternate.commands.AlternateCommand;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.alternate.util.TeleportingUtil;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpawnCommand extends AlternateCommand {

    public SpawnCommand(ConfigData configData) {
        super(configData, true, "spawn");
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player sender = gamer.getPlayer();

        if (strings.length == 1 && gamer.getGroup() == Group.ADMIN) {
            String name = strings[0];
            Player player = Bukkit.getPlayer(name);
            if (player == null || !player.isOnline()) {
                COMMANDS_API.playerOffline(gamerEntity, name);
                return;
            }
            send("SPAWN", gamer, player.getDisplayName());
            spawn(player);
            return;
        }

        TeleportingUtil.teleport(sender, this, () -> spawn(sender));
    }

    private void spawn(Player player) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        User user = USER_MANAGER.getUser(player);
        if (gamer == null || user == null)
            return;

        UserTeleportByCommandEvent event = new UserTeleportByCommandEvent(user,
                UserTeleportByCommandEvent.Command.SPAWN, AlternateAPI.getSpawn());
        BukkitUtil.callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        if (user.teleport(AlternateAPI.getSpawn())) {
            send("SPAWN", gamer, null);
        }
    }
}
