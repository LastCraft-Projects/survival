package net.lastcraft.alternate.commands.info;

import net.lastcraft.alternate.commands.AlternateCommand;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class NearCommand extends AlternateCommand {

    public NearCommand(ConfigData configData) {
        super(configData, true, "near");
        setMinimalGroup(configData.getInt("nearCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player sender = gamer.getPlayer();

        long radius = 100;
        String nearPLayer = getLocal(sender, radius);
        if (nearPLayer.length() == 0) {
            sendMessageLocale(gamerEntity, "NEAR_NOT_FOUND");
            return;
        }
        sendMessageLocale(gamerEntity, "NEAR", nearPLayer);
    }

    private String getLocal(Player player, long radius) {
        Location loc = player.getLocation();
        World world = loc.getWorld();
        StringBuilder output = new StringBuilder();
        long radiusSquared = radius * radius;

        for (Player otherPlayer: Bukkit.getOnlinePlayers()){
            if (!player.equals(otherPlayer) && otherPlayer.canSee(player)){
                final Location playerLoc = otherPlayer.getLocation();
                if (playerLoc.getWorld() != world){
                    continue;
                }

                final long delta = (long) playerLoc.distanceSquared(loc);
                if (delta < radiusSquared){
                    if (output.length() > 0) {
                        output.append(", ");
                    }
                    output.append(otherPlayer.getDisplayName())
                            .append("§f(§a")
                            .append((long) Math.sqrt(delta))
                            .append("m§f)");
                }
            }
        }
        return output.length() > 1 ? output.toString() : "";
    }
}
