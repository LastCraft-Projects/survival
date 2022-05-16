package net.lastcraft.alternate.util;

import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.AlternateGui;
import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.manager.UserManager;
import net.lastcraft.alternate.object.CraftUser;
import net.lastcraft.api.manager.GuiManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class GuiThread extends Thread {

    private final GuiManager<AlternateGui> manager = AlternateAPI.getGuiManager();
    private final UserManager userManager = AlternateAPI.getUserManager();

    public GuiThread() {
        super("AlternateGuiThread");
        start();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(50);

                for (User user : userManager.getUsers().values()) {
                    CraftUser craftUser = (CraftUser) user;
                    clear(craftUser.getCallReguests());
                    clear(craftUser.getTradeReguests());
                }

                for (String name : manager.getPlayerGuis().keySet()) {
                    Map<String, AlternateGui> guiList = manager.getPlayerGuis().get(name);
                    if (guiList != null) {
                        guiList.values().forEach(alternateGui -> {
                            if (alternateGui != null) {
                                alternateGui.update();
                            }
                        });
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void clear(Map<String, Long> data) {
        for (Map.Entry<String, Long> request : data.entrySet()) {
            String name = request.getKey();
            Long time = request.getValue();
            if (time + 122 * 1000 < System.currentTimeMillis()) {
                data.remove(name);
            }

            Player player = Bukkit.getPlayerExact(name);
            if (player == null || !player.isOnline()) {
                data.remove(name);
            }
        }
    }
}
