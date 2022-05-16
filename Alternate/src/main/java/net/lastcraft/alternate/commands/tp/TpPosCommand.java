package net.lastcraft.alternate.commands.tp;

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

public class TpPosCommand extends AlternateCommand {

    public TpPosCommand(ConfigData configData) {
        super(configData, true, "tppos", "pos");
        setMinimalGroup(configData.getInt("tpPosCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();
        User user = USER_MANAGER.getUser(player);
        if (user == null)
            return;

        if (strings.length < 3) {
            COMMANDS_API.notEnoughArguments(gamerEntity, "TPPOS_FORMAT");
            return;
        }

        final int x;
        final int y;
        final int z;

        try {
            x = Integer.parseInt(strings[0]);
            y = Integer.parseInt(strings[1]);
            z = Integer.parseInt(strings[2]);
        } catch (NumberFormatException e){
            gamer.sendMessageLocale("TPPOS_ERROR");
            return;
        }
        Location location = new Location(player.getWorld(), x, y, z);

        if (strings.length > 3) {
            try {
                location.setYaw((Float.parseFloat(strings[3]) + 180 + 360) % 360);
            } catch(NumberFormatException ignored){}
        }

        if (strings.length > 4) {
            try {
                location.setPitch(Float.parseFloat(strings[4]));
            } catch(NumberFormatException ignored){}
        }

        if (x > 30000000 || y > 30000000 || z > 30000000 || x < -30000000 || y < -30000000 || z < -30000000) {
            gamer.sendMessageLocale("TPPOS_ERROR_2");
            return;
        }

        UserTeleportByCommandEvent event = new UserTeleportByCommandEvent(user,
                UserTeleportByCommandEvent.Command.TPPOS, location);
        BukkitUtil.callEvent(event);

        if (event.isCancelled())
            return;

        String locString = "§a" + x + "§f, §a" + y + "§f, §a" + z;
        TeleportingUtil.teleport(player, this, () -> {
            if (user.teleport(location)) {
                sendMessageLocale(gamerEntity, "TPPOS", locString);
            }
        });
    }
}
