package net.lastcraft.alternate.commands.warp;

import com.google.common.collect.ImmutableList;
import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.Warp;
import net.lastcraft.alternate.api.events.UserRemoveWarpEvent;
import net.lastcraft.alternate.api.manager.WarpManager;
import net.lastcraft.alternate.commands.AlternateCommand;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.api.command.CommandTabComplete;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;

import java.util.List;
import java.util.stream.Collectors;

public class DelWarpCommand extends AlternateCommand implements CommandTabComplete {

    private final WarpManager warpManager = AlternateAPI.getWarpManager();

    public DelWarpCommand(ConfigData configData) {
        super(configData, true, "delwarp", "removewarp");
        spigotCommand.setCommandTabComplete(this);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        User user = USER_MANAGER.getUser(gamerEntity.getName());
        if (user == null) {
            return;
        }

        if (strings.length == 0) {
            COMMANDS_API.notEnoughArguments(gamerEntity, "DEL_WARP_FORMAT");
            return;
        }

        String name = strings[0];
        if (!warpManager.getWarps().containsKey(name.toLowerCase())) {
            gamerEntity.sendMessageLocale( "WARP_NOT_FOUND", name);
            return;
        }

        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Warp warp = warpManager.getWarp(name);
        if (warp.getOwnerID() != gamer.getPlayerID() && !gamer.isStaff()) {
            gamer.sendMessageLocale("WARP_NOT_YOUR", warp.getName());
            return;
        }

        UserRemoveWarpEvent event = new UserRemoveWarpEvent(user, warp);
        BukkitUtil.callEvent(event);

        if (!event.isCancelled()) {
            sendMessageLocale(gamerEntity, "WARP_REMOVED", warp.getName());
            warpManager.removeWarp(warp);
        }
    }

    @Override
    public List<String> getComplete(GamerEntity gamerEntity, String s, String[] strings) {
        User user = USER_MANAGER.getUser(gamerEntity.getName());
        if (user == null || strings.length > 1) {
            return ImmutableList.of();
        }

        return COMMANDS_API.getCompleteString(warpManager.getWarps(user)
                .stream()
                .map(Warp::getName)
                .collect(Collectors.toList()), strings);
    }
}
