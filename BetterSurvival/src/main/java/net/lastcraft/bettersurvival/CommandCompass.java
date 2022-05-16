package net.lastcraft.bettersurvival;

import net.lastcraft.alternate.Alternate;
import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CommandCompass implements CommandInterface {

    public CommandCompass() {
        SpigotCommand spigotCommand = COMMANDS_API.register("compass", this);
        spigotCommand.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] args) {
        Player player = ((BukkitGamer) gamerEntity).getPlayer();
        switch (args.length) {
            case 0: {
                Location location = BetterSurvival.getBedLocation(player);

                if (location == null || location.getWorld().getName().equalsIgnoreCase("lobby")) {
                    player.setCompassTarget(player.getWorld().getSpawnLocation());

                    player.sendMessage(Alternate.getConfigData().getPrefix() + "§fВаш компас указывает на центр мира, так как у вас нет кровати!");
                    break;
                }
                player.setCompassTarget(location);
                player.sendMessage(Alternate.getConfigData().getPrefix() + "§fВы можете настроить компасс на любые координаты §c/compass [x] [z]");
                break;
            }
            case 2: {
                try {
                    int x = Integer.parseInt(args[0]);
                    int z = Integer.parseInt(args[1]);
                    player.sendMessage(Alternate.getConfigData().getPrefix() + "§fВаш компас указывает на координаты §ax: " + x + " §az: " + z);
                    player.setCompassTarget(new Location(player.getWorld(), (double) x, 64.0, (double) z));
                } catch (NumberFormatException ex) {
                    player.sendMessage(Alternate.getConfigData().getPrefix() + "§fОшибка в написании координат! §fНастроить компас на координаты: §c/compass [x] [z]");
                }
                break;
            }
            case 3: {
                try {
                    int x = Integer.parseInt(args[0]);
                    int z = Integer.parseInt(args[2]);
                    player.sendMessage(Alternate.getConfigData().getPrefix() + "§fВаш компас указывает на координаты §ax: " + x + " §az: " + z);
                    player.setCompassTarget(new Location(player.getWorld(), (double) x, 64.0, (double) z));
                } catch (NumberFormatException ex) {
                    player.sendMessage(Alternate.getConfigData().getPrefix() + "§fОшибка в написании координат! §fНастроить компас на координаты: §c/compass [x] [z]");
                }
                break;
            }
        }
    }
}
