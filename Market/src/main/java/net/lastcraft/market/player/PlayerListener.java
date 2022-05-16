package net.lastcraft.market.player;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.market.Market;
import net.lastcraft.market.api.MarketAPI;
import net.lastcraft.market.api.MarketPlayer;
import net.lastcraft.market.api.MarketPlayerManager;
import net.lastcraft.market.utils.PlayerLoader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final MarketPlayerManager marketPlayerManager = MarketAPI.getMarketPlayerManager();
    private final GamerManager gamerManager = LastCraft.getGamerManager();

    private final Market market;

    public PlayerListener(Market market) {
        this.market = market;
        Bukkit.getPluginManager().registerEvents(this, market);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED)
            return;

        String name = e.getName();

        BukkitGamer gamer = gamerManager.getGamer(name);
        if (gamer == null) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cОшибка при загрузке данных...");
            return;
        }

        MarketPlayer marketPlayer = PlayerLoader.get(name);
        if (marketPlayer == null) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cОшибка при загрузке данных...");
            return;
        }
        marketPlayerManager.addMarketPlayer(marketPlayer);

        if (marketPlayerManager.contains(name))
            return;

        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cПерезайдите, ошибка при загрузке данных");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        String name = player.getName().toLowerCase();
        marketPlayerManager.removeMarketPlayer(name);

        market.getAuctionManager().removePlayerGuis(player);
        market.getAuctionManager().getOwnerGuis().remove(name);
    }
}
