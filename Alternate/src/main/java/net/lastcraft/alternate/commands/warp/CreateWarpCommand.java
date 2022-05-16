package net.lastcraft.alternate.commands.warp;

import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.Warp;
import net.lastcraft.alternate.api.events.UserCreateWarpEvent;
import net.lastcraft.alternate.api.manager.WarpManager;
import net.lastcraft.alternate.commands.AlternateCommand;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.alternate.managers.CraftWarpManager;
import net.lastcraft.alternate.object.CraftWarp;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import org.bukkit.entity.Player;

import java.util.Map;

public class CreateWarpCommand extends AlternateCommand {

    private final CraftWarpManager warpManager = (CraftWarpManager) AlternateAPI.getWarpManager();
    private final Map<Group, Integer> limitWars;

    public CreateWarpCommand(ConfigData configData) {
        super(configData, true, "createwarp", "setwarp");
        limitWars = configData.getWarpLimit();
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        User user = USER_MANAGER.getUser(gamerEntity.getName());
        if (user == null)
            return;

        if (strings.length == 0) {
            COMMANDS_API.notEnoughArguments(gamerEntity, "CREATE_WARP_FORMAT");
            return;
        }

        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Language lang = gamer.getLanguage();
        Player player = gamer.getPlayer();

        String name = strings[0];
        if (warpManager.getWarps().containsKey(name.toLowerCase())) {
            gamer.sendMessageLocale("WARP_CREATE_ERROR", name);
            return;
        }

        int warps = warpManager.size();
        Integer playerLimit = limitWars.get(gamer.getGroup());
        if (playerLimit == null) {
            playerLimit = limitWars.get(Group.DEFAULT);
        }

        if (warps == playerLimit && !gamer.isStaff()) {
            gamer.sendMessageLocale("HOME_WARP_LIMIT",
                    playerLimit,
                    StringUtil.getCorrectWord(playerLimit, "WARPS_1", lang));
            return;
        }

        //todo сделать покупку варпов

        if (name.length() > 32) {
            gamer.sendMessageLocale("WARP_CREATE_NAME_ERROR");
            return;
        }

        Warp warp = new CraftWarp(name, gamer.getPlayerID(), player.getLocation(), false);

        UserCreateWarpEvent event = new UserCreateWarpEvent(user, warp);
        BukkitUtil.callEvent(event);

        if (event.isCancelled())
            return;

        sendMessageLocale(gamerEntity,  "WARP_CREATE", warp.getName());
        warpManager.addToDataBase(warp);
    }
}
