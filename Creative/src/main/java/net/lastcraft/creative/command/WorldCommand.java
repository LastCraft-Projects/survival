package net.lastcraft.creative.command;

import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.manager.UserManager;
import net.lastcraft.api.JSONMessageAPI;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.util.JsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class WorldCommand implements CommandInterface {

    private final List<String> worlds = Arrays.asList(
            "plot55",
            "plot95",
            "plot200"
    );
    private final UserManager userManager = AlternateAPI.getUserManager();

    private final JSONMessageAPI jsonMessageAPI = LastCraft.getJsonMessageAPI();

    public WorldCommand() {
        SpigotCommand spigotCommand = COMMANDS_API.register("world", this);
        spigotCommand.setOnlyPlayers(true);
    }

    public void execute(GamerEntity gamerEntity, String s, String[] strings) { //todo локализация
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();
        if (strings.length < 1) {
            gamer.sendMessage("§cОшибка, пишите /world <название мира> или /world list для просмотра списка доступных");
            return;
        }

        User user = userManager.getUser(player);
        if (user == null) {
            return;
        }


        String arg = strings[0].toLowerCase();
        switch (arg) {
            case "list":
                JsonBuilder names = new JsonBuilder();
                names.addText("§6Creative §8| §fСписок доступных миров: ");
                int amount = 0;
                for (String name : worlds) {
                    amount++;
                    names.addRunCommand("§a" + name, "/world " + name, "§fНажмите для телепортации");
                    if (amount >= worlds.size())
                        continue;

                    names.addText("§f, ");
                }
                jsonMessageAPI.send(player, names);
                break;
            default:
                World world = Bukkit.getWorld(arg);
                if (world == null || !worlds.contains(arg)) {
                    gamer.sendMessage("§cОшибка, указанного Вами мира не существует. Ввведите /world list для просмотра списка доступных миров");
                    return;
                }
                if (user.teleport(world.getSpawnLocation())) {
                    gamer.sendMessage("§6Creative §8| §fВы были телепортированы в мир §a" + world.getName());
                }
                break;
        }
    }
}
