package net.lastcraft.bettersurvival.combat;

import lombok.Getter;
import lombok.Setter;
import net.lastcraft.alternate.Alternate;
import net.lastcraft.api.ActionBarAPI;
import net.lastcraft.api.LastCraft;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class CombatData {

    private static final int LEAVE_DELAY_SECONDS = 15;
    private static final String MESSAGE_PVP_FINISH = Alternate.getConfigData().getPrefix() + "§fВы вышли из боя! Теперь можно спокойно перезайти на сервер!";
    private static final ActionBarAPI ACTION_BAR_API = LastCraft.getActionBarAPI();

    private BukkitTask task;
    private Player player;
    @Setter
    private LivingEntity lastDamager;
    @Getter
    private boolean inPvp;
    private long lastCombatTime;

    public CombatData(Player player) {
        this.task = null;
        this.lastCombatTime = 0L;
        this.player = player;
    }

    void handleCombat(boolean damager, Plugin owner) {
        this.lastCombatTime = System.currentTimeMillis();
        if (!this.inPvp) {
            this.inPvp = true;
            this.player.sendMessage(Alternate.getConfigData().getPrefix() + "§fВы вступили в бой! Не покидайте игру в течении §c" + LEAVE_DELAY_SECONDS + " §fсекунд");
        }
        if (!damager && this.task != null) {
            this.task.cancel();
            this.task = null;
        }
        if (this.task == null) {
            this.task = new BukkitRunnable() {
                public void run() {
                    if (CombatData.this.lastCombatTime + LEAVE_DELAY_SECONDS * 1000 <= System.currentTimeMillis()) {
                        CombatData.this.inPvp = false;
                        CombatData.this.task = null;
                        if (CombatData.this.player.isOnline()) {
                            CombatData.this.player.sendMessage(CombatData.MESSAGE_PVP_FINISH);
                            ACTION_BAR_API.sendBar(CombatData.this.player, " ");
                        }
                        this.cancel();
                    } else {
                        int delay = (int) (LEAVE_DELAY_SECONDS + (CombatData.this.lastCombatTime - System.currentTimeMillis()) / 1000L);
                        int per = (int) ((1.0f - delay / LEAVE_DELAY_SECONDS) * 20.0f);
                        StringBuilder bar = new StringBuilder();
                        for (int i = 0; i < 20; ++i) {
                            bar.append("§").append((i < per) ? "a" : "c").append("|");
                        }
                        ACTION_BAR_API.sendBar(CombatData.this.player, "Режим боя " + bar.toString() + " §f" + delay + "с");
                    }
                }
            }.runTaskTimerAsynchronously(owner, 10L, 10L);
        }
    }
}
