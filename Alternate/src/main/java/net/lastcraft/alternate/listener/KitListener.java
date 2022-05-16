package net.lastcraft.alternate.listener;

import net.lastcraft.alternate.Alternate;
import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.Kit;
import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.manager.KitManager;
import net.lastcraft.alternate.api.manager.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;

public class KitListener implements Listener {

    private final UserManager userManager = AlternateAPI.getUserManager();
    private final KitManager kitManager = AlternateAPI.getKitManager();

    public KitListener(Alternate alternate) {
        if (!Alternate.getConfigData().isKitSystem())
            return;

        Bukkit.getPluginManager().registerEvents(this, alternate);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        User user = userManager.getUser(player);
        if (user == null)
            return;

        if (!user.isOnline() || !user.isFirstJoin())
            return;

        Inventory inventory = player.getInventory();
        for (Kit kit : kitManager.getKits().values()) {
            if (!kit.isStart())
                continue;

            kit.getItems().forEach(inventory::addItem);
        }
    }
}
