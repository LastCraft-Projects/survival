package net.lastcraft.alternate.commands.home;

import com.google.common.collect.ImmutableList;
import net.lastcraft.alternate.api.Home;
import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.events.UserTeleportByCommandEvent;
import net.lastcraft.alternate.commands.AlternateCommand;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.alternate.gui.HomeGui;
import net.lastcraft.alternate.util.TeleportingUtil;
import net.lastcraft.api.command.CommandTabComplete;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class HomeCommand extends AlternateCommand implements CommandTabComplete {

    public HomeCommand(ConfigData configData) {
        super(configData, true, "home", "homes");
        spigotCommand.setCommandTabComplete(this);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        User user = USER_MANAGER.getUser(gamerEntity.getName());
        if (user == null) {
            return;
        }

        int size = user.getHomes().size();

        Home home;
        String nameHome;

        if (size == 0) {
            gamerEntity.sendMessageLocale("HOME_EMPTY");
            return;
        }

        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        if (size == 1) {
            nameHome = getFirstHome(user);
            home = user.getHomes().get(nameHome);
        } else {
            if (strings.length == 0) {
                HomeGui homeGui = GUI_MANAGER.getGui(HomeGui.class, player);
                if (homeGui != null)
                    homeGui.open();
                return;
            }
            nameHome = strings[0].toLowerCase();

            home = user.getHomes().get(nameHome);
            if (home == null) {
                gamerEntity.sendMessageLocale("HOME_NOT_FOUND", nameHome);
                return;
            }
        }

        UserTeleportByCommandEvent event = new UserTeleportByCommandEvent(user,
                UserTeleportByCommandEvent.Command.HOME,
                home.getLocation());
        BukkitUtil.callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        TeleportingUtil.teleport(player, this, () -> {
            if (user.teleport(home.getLocation())) {
                sendMessageLocale(gamerEntity,  "HOME_TO", nameHome);
            }
        });
    }

    private String getFirstHome(User user) {
        return user.getHomes().keySet().stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<String> getComplete(GamerEntity gamerEntity, String s, String[] strings) {
        User user = USER_MANAGER.getUser(gamerEntity.getName());
        if (user == null || strings.length > 1) {
            return ImmutableList.of();
        }

        return COMMANDS_API.getCompleteString(user.getHomes().keySet(), strings);
    }
}
