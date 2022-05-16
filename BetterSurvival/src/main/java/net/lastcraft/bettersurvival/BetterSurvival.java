package net.lastcraft.bettersurvival;

import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.User;
import net.lastcraft.api.LastCraft;
import net.lastcraft.bettersurvival.combat.CombatManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

//todo перевести все что тут в локализацию и переписать нормально
public class BetterSurvival extends JavaPlugin {

    private BukkitTask gameTimer;

    @Override
    public void onEnable() {
        if (!LastCraft.getGamerManager().getSpigot().getName().contains("skyblock")) { //Если сервер не сб
            this.gameTimer = new GameTimer(this).runTaskTimer(this, 20L, 20L);
            new CommandCompass();
        }
        new CombatManager(this);
    }

    @Override
    public void onDisable() {
        if (this.gameTimer != null) {
            this.gameTimer.cancel();
        }
    }

    public static Location getBedLocation(Player player) {
        User user = AlternateAPI.getUserManager().getUser(player);
        if (user != null)
            return user.getBedLocation();

        return AlternateAPI.getSpawn();
    }
}
