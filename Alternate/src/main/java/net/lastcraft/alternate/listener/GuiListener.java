package net.lastcraft.alternate.listener;

import net.lastcraft.alternate.Alternate;
import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.AlternateGui;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.alternate.gui.*;
import net.lastcraft.api.manager.GuiManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GuiListener implements Listener {

    private final GuiManager<AlternateGui> manager = AlternateAPI.getGuiManager();

    public GuiListener(Alternate main) {
        ConfigData configData = Alternate.getConfigData();
        Bukkit.getPluginManager().registerEvents(this, main);

        //создаем все ГУИ
        if (configData.isKitSystem())
            manager.createGui(KitGui.class);

        if (configData.isHomeSystem())
            manager.createGui(HomeGui.class);

        if (configData.isWarpSystem()) {
            manager.createGui(WarpGui.class);
            manager.createGui(MyWarpGui.class);
        }

        manager.createGui(MainGui.class);
        manager.createGui(PTimeGui.class);
        manager.createGui(TpacceptGui.class);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        manager.removeALL(player);
    }

    @EventHandler
    public void onPlayerOpenGui(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if (!e.getMessage().equalsIgnoreCase("/menu"))
            return;

        e.setCancelled(true);
        manager.getGui(MainGui.class, player).open();
    }
}
