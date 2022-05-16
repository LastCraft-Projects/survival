package net.lastcraft.alternate.commands.warp;

import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.Warp;
import net.lastcraft.alternate.api.manager.WarpManager;
import net.lastcraft.alternate.commands.AlternateCommand;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.gamer.constans.Group;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WarpInfoCommand extends AlternateCommand {

    private final WarpManager warpManager = AlternateAPI.getWarpManager();
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public WarpInfoCommand(ConfigData configData) {
        super(configData, false, "warpinfo");
        setMinimalGroup(Group.MODERATOR);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String cmd, String[] args) {
        if (args.length < 1) {
            COMMANDS_API.notEnoughArguments(gamerEntity, "WARPINFO_FORMAT");
            return;
        }

        String name = args[0];
        if (!warpManager.getWarps().containsKey(name.toLowerCase())) {
            gamerEntity.sendMessageLocale("WARP_NOT_FOUND", name);
            return;
        }

        Warp warp = warpManager.getWarp(name);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(warp.getDate().getTime());
        String date = simpleDateFormat.format(calendar.getTime());

        sendMessageLocale(gamerEntity, "WARPINFO", warp.getName(), warp.getOwner().getDisplayName(), date);
    }
}
