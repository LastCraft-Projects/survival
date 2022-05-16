package net.lastcraft.anarchy.listener;

import gnu.trove.map.TIntObjectMap;
import net.lastcraft.anarchy.Anarchy;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import net.lastcraft.market.Market;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScavengerListener implements Listener {

    private final TIntObjectMap<Integer> configData; //level - percent
    private final Map<String, Scavenger> scavengers = new HashMap<>();
    private final GamerManager gamerManager = LastCraft.getGamerManager();

    public ScavengerListener(Anarchy anarchy) {
        Bukkit.getPluginManager().registerEvents(this, anarchy);

        configData = anarchy.getAnarchyConfig().loadScavenger();
    }

    @EventHandler
    public void onDeathPlayer(PlayerDeathEvent e) {
        Player player = e.getEntity();
        BukkitGamer gamer = gamerManager.getGamer(player);
        if (gamer == null)
            return;

        int percent = getPercent(gamer.getGroup().getLevel());
        if (percent == 0)
            return;

        List<ItemStack> items = getItems(player);
        float exp = (float) getData(player.getExp(), percent);
        int level = (int) getData(player.getLevel(), percent);
        scavengers.put(player.getName().toLowerCase(), new Scavenger(items, exp, level));
    }

    private double getData(double data, int percent) {
        return Market.round(Market.round(data) / 100 * percent);
    }

    private List<ItemStack> getItems(Player player) { //поиск норм вещей
        PlayerInventory inventory = player.getInventory();
        //int count = Arrays.stream(inventory.getContents())
        //        .mapToInt(ItemStack::getAmount)
        //        .sum();
        return null;
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent e) {
        Player player = e.getPlayer();

        Scavenger scavenger = scavengers.remove(player.getName().toLowerCase());
        if (scavenger == null)
            return;

        BukkitUtil.runTaskLater(40L, ()-> scavenger.give(player));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        scavengers.remove(e.getPlayer().getName().toLowerCase());
    }

    private int getPercent(int level) {
        while (level != -1) {
            Integer multi = configData.get(level);
            if (multi == null) {
                level--;
                continue;
            }

            return multi;
        }
        return 0;
    }
}
