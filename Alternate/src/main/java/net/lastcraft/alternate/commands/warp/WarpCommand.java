package net.lastcraft.alternate.commands.warp;

import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.Warp;
import net.lastcraft.alternate.api.manager.WarpManager;
import net.lastcraft.alternate.commands.AlternateCommand;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.alternate.gui.WarpGui;
import net.lastcraft.alternate.util.TeleportingUtil;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import org.bukkit.entity.Player;

public class WarpCommand extends AlternateCommand {

    private final WarpManager warpManager = AlternateAPI.getWarpManager();

    public WarpCommand(ConfigData configData) {
        super(configData, true, "warp", "warps");
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        if (strings.length == 0) {
            WarpGui warpGui = GUI_MANAGER.getGui(WarpGui.class, player);
            if (warpGui != null)
                warpGui.open();
            return;
        }

        String name = strings[0];
        Warp warp = warpManager.getWarp(name);
        if (warp == null) {
            gamerEntity.sendMessageLocale("WARP_NOT_FOUND", name);
            return;
        }

        TeleportingUtil.teleport(player, this, ()-> warp.teleport(player));
    }
}
