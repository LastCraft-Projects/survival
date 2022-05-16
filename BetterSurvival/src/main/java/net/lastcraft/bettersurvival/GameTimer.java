package net.lastcraft.bettersurvival;

import lombok.RequiredArgsConstructor;
import net.lastcraft.api.LastCraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GameTimer extends BukkitRunnable {

    private static final PotionEffect SLEEP_REGEN = new PotionEffect(PotionEffectType.REGENERATION, 200, 0);
    private static final PotionEffect NOT_SLEEP_BLIDNESS = new PotionEffect(PotionEffectType.BLINDNESS, 200, 0);

    private final Plugin plugin;

    public void run() {
        List<Player> sleepingUsers = Bukkit.getOnlinePlayers().stream().filter(HumanEntity::isSleeping).collect(Collectors.toList());
        List<Player> notSleeping = Bukkit.getOnlinePlayers().stream().filter(player -> player.getWorld() == Bukkit.getWorlds().get(0) && !player.isSleeping()).collect(Collectors.toList());
        int totalPlayers = sleepingUsers.size() + notSleeping.size();
        int percent = sleepingUsers.size() * 100 / ((totalPlayers != 0) ? totalPlayers : 1);
        if (percent >= 50) {
            Bukkit.getWorlds().get(0).setTime(0L);
            notSleeping.forEach(user -> {
                user.getPlayer().addPotionEffect(GameTimer.NOT_SLEEP_BLIDNESS);
                user.getPlayer().setSleepingIgnored(true);
            });
            sleepingUsers.forEach(users -> users.getPlayer().addPotionEffect(GameTimer.SLEEP_REGEN));

            Bukkit.getScheduler().runTask(plugin, () -> notSleeping.forEach(user -> user.getPlayer().setSleepingIgnored(false)));
        }
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.isSleeping()) {
                LastCraft.getActionBarAPI().sendBar(player, "Спят: §e" + sleepingUsers.size() + "§f/§e" + totalPlayers + " §f- §e" + percent + "%");
            } else if ((player.getInventory().getItemInMainHand() != null && player.getInventory().getItemInMainHand().getType() == Material.COMPASS) || (player.getInventory().getItemInOffHand() != null && player.getInventory().getItemInOffHand().getType() == Material.COMPASS)) {
                Location loc1 = player.getLocation();
                loc1.setY(64.0);
                Location loc2 = player.getCompassTarget();
                loc2.setY(64.0);
                int blocks = (int) loc1.distance(loc2);

                LastCraft.getActionBarAPI().sendBar(player, "Расстояние до цели: §e" + blocks);
            }
        });
        sleepingUsers.clear();
        notSleeping.clear();
    }
}
