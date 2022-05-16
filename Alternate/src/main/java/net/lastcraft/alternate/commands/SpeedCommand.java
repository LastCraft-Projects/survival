package net.lastcraft.alternate.commands;

import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.gamer.constans.Group;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpeedCommand extends AlternateCommand {

    public SpeedCommand(ConfigData configData) {
        super(configData, true, "speed", "скорость");
        setMinimalGroup(configData.getInt("speedCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] args) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        User user = USER_MANAGER.getUser(player);
        if (user == null)
            return;

        if (args.length == 0) {
            setSpeed(player, 1);
            return;
        }

        if (args.length == 2 && gamer.getGroup() == Group.ADMIN) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                COMMANDS_API.playerOffline(gamerEntity, args[0]);
                return;
            }
            int speed = parseSpeed(args[1]);
            sendMessageLocale(gamerEntity, "SPEED_TO", speed, target.getDisplayName());
            setSpeed(target, speed);
            return;
        }

        int speed = parseSpeed(args[0]);
        setSpeed(player, speed);
    }

    private int parseSpeed(String string) {
        int speed = 1;

        try {
            speed = Integer.parseInt(string);
            if (speed > 10)
                speed = 10;
            if (speed < 1)
                speed = 1;
        } catch (Exception ignored) {}

        return speed;
    }

    private void setSpeed(Player player, int speed) {
        boolean isFly = player.isFlying();

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        sendMessageLocale(gamer, "SPEED", speed);

        if (isFly) {
            player.setFlySpeed(getRealMoveSpeed(speed, true));
            return;
        }

        player.setWalkSpeed(getRealMoveSpeed(speed, false));
    }

    private float getRealMoveSpeed(float userSpeed, boolean isFly) {
        final float defaultSpeed = isFly ? 0.1f : 0.2f;
        float maxSpeed = 1f;

        if (userSpeed < 1f) {
            return defaultSpeed * userSpeed;
        } else {
            float ratio = ((userSpeed - 1) / 9) * (maxSpeed - defaultSpeed);
            return ratio + defaultSpeed;
        }
    }
}
