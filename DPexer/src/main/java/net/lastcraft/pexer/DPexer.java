package net.lastcraft.pexer;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.game.GameSettings;
import net.lastcraft.api.game.MiniGameType;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.dartaapi.listeners.JoinListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class DPexer extends JavaPlugin implements Listener {

    private GamerManager gamerManager;

    @Override
    public void onEnable() {
        GameSettings.minigame = MiniGameType.SURVIVAL;

        gamerManager = LastCraft.getGamerManager();

        Bukkit.getPluginManager().registerEvents(this, this);

        new JoinListener(this);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent e) {
        BukkitGamer gamer = gamerManager.getGamer(e.getPlayer());
        if (gamer == null)
            return;

        PermissionUser user = PermissionsEx.getUser(e.getPlayer());
        if (gamer.getGroup() == Group.DEFAULT) {
            if (user.getGroups().length > 0 && !user.getGroups()[0].getName().equalsIgnoreCase("default")) {
                user.setGroups(new String[] { "default" });
            }
        }
        else {
            user.setGroups(new String[] { gamer.getGroup().getGroupName() });
        }
    }
}
