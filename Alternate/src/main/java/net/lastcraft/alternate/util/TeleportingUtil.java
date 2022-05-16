package net.lastcraft.alternate.util;

import net.lastcraft.alternate.Alternate;
import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.manager.UserManager;
import net.lastcraft.alternate.commands.AlternateCommand;
import net.lastcraft.alternate.listener.PlayerListener;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class TeleportingUtil {

    private static final UserManager USER_MANAGER = AlternateAPI.getUserManager();
    private static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();
    private static Alternate alternate;

    public TeleportingUtil(Alternate alternate) {
        TeleportingUtil.alternate = alternate;
    }

    public static void teleport(Player sender, AlternateCommand alternateCommand, Runnable runnable) {
        if (alternate == null || !alternate.isEnabled())
            return;

        BukkitGamer gamer = GAMER_MANAGER.getGamer(sender);
        User user = USER_MANAGER.getUser(sender);
        if (gamer == null || user == null)
            return;

        String name = sender.getName();
        Map<String, Boolean> map = PlayerListener.getMapTpErrorBeforeTp();
        Language lang = gamer.getLanguage();
        int waitTeleport = Alternate.getConfigData().getInt("waitTeleport");

        if (waitTeleport <= 0
                || gamer.getGroup().getLevel() >= Alternate.getConfigData().getInt("ignoreWaitTeleport")) {
            runnable.run();
            return;
        }

        if (map.get(name) != null && map.get(name)){
            gamer.sendMessageLocale("TELEPORTING_RUNNING");
            return;
        } else {
            alternateCommand.sendMessageLocale(gamer, "TELEPORTING",
                    String.valueOf(waitTeleport),
                    StringUtil.getCorrectWord(waitTeleport, "TIME_SECOND_1", lang));
        }
        map.put(sender.getName(), true);

        new BukkitRunnable(){
            int time = waitTeleport * 20;
            @Override
            public void run() {
                time--;
                if (map.get(name) != null && map.get(name)){
                    if (time == 0) {
                        BukkitUtil.runTask(runnable);
                        map.put(name, false);
                        cancel();
                    }
                } else {
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(alternate, 0L, 1L);
    }
}
