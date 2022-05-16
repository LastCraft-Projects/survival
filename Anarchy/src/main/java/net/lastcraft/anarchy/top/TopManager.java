package net.lastcraft.anarchy.top;

import net.lastcraft.anarchy.Anarchy;
import net.lastcraft.anarchy.stats.StatsLoader;
import net.lastcraft.api.util.LocationUtil;
import net.lastcraft.base.sql.ConnectionConstants;
import net.lastcraft.base.sql.api.MySqlDatabase;
import net.lastcraft.dartaapi.armorstandtop.ArmorStandTopManager;
import net.lastcraft.dartaapi.armorstandtop.StandTop;
import net.lastcraft.market.utils.PlayerLoader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TopManager {

    private static final String SQL_TOP_MONEY = "SELECT * FROM `MarketPlayer` ORDER BY `money` DESC LIMIT 5;";
    private static final String SQL_TOP_KILLS = "SELECT * FROM `Stats` ORDER BY `kills` DESC LIMIT 5;";

    private final ArmorStandTopManager armorStandTopManager;
    //private final ScheduledExecutorService executorService;

    private final Map<Integer, MoneyTop> moneyTopMap = new HashMap<>();
    private final Map<Integer, KillsTop> killsTopMap = new HashMap<>();

    public TopManager(Anarchy anarchy) {
        armorStandTopManager = new ArmorStandTopManager(anarchy,
                "s1" + ConnectionConstants.DOMAIN.getValue(),
                "anarchy", "IslandTopStand");

        setDefaultValues();

        FileConfiguration config = anarchy.getConfig();

        AtomicInteger pos = new AtomicInteger(1);
        List<StandTop> moneyTop = new ArrayList<>();
        List<StandTop> levelTop = new ArrayList<>();
        config.getStringList("armorStandTop").forEach(stringLoc -> {
            Location location = LocationUtil.stringToLocation(stringLoc, true);
            MoneyTop moneyData = moneyTopMap.get(pos.get());
            KillsTop killsTop = killsTopMap.get(pos.get());
            if (moneyData != null) {
                StandTop standTop = new StandTop(location, pos.get(), moneyData);
                moneyTop.add(standTop);
            }
            if (killsTop != null) {
                StandTop standTop = new StandTop(location, pos.get(), killsTop);
                levelTop.add(standTop);
            }
            pos.getAndIncrement();
        });
        armorStandTopManager.addArmorStandTop(moneyTop, "ISLAND_TOP_MONEY_HOLO");
        armorStandTopManager.addArmorStandTop(levelTop, "ISLAND_TOP_KILLS_HOLO");

        Bukkit.getScheduler().runTaskTimerAsynchronously(anarchy, this::setValues, 0, 20 * 60 * 15);
    }

    private void setValues() {
        MySqlDatabase mySqlDatabase = PlayerLoader.getMySqlDatabase();
        mySqlDatabase.executeQuery(SQL_TOP_MONEY, (rs) -> {
            int pos = 1;
            while (rs.next()) {
                int playerID = rs.getInt("playerID");
                double money = rs.getDouble("money");
                MoneyTop moneyData = moneyTopMap.get(pos);
                if (moneyData != null)
                    moneyData.update(playerID, money);
                pos++;
            }
            return Void.TYPE;
        });

        mySqlDatabase = StatsLoader.getMysqlDatabase();
        mySqlDatabase.executeQuery(SQL_TOP_KILLS, (rs) -> {
            int pos = 1;
            while (rs.next()) {
                int playerID = rs.getInt("playerID");
                double kills = rs.getDouble("kills");
                KillsTop killsTop = killsTopMap.get(pos);
                if (killsTop != null)
                    killsTop.update(playerID, kills);
                pos++;
            }
            return Void.TYPE;
        });

        armorStandTopManager.updateTops();
    }

    private void setDefaultValues() {
        moneyTopMap.put(1, new MoneyTop(1));
        moneyTopMap.put(2, new MoneyTop(1));
        moneyTopMap.put(3, new MoneyTop(1));
        moneyTopMap.put(4, new MoneyTop(1));
        moneyTopMap.put(5, new MoneyTop(1));

        killsTopMap.put(1, new KillsTop(1));
        killsTopMap.put(2, new KillsTop(1));
        killsTopMap.put(3, new KillsTop(1));
        killsTopMap.put(4, new KillsTop(1));
        killsTopMap.put(5, new KillsTop(1));
    }
}
