package net.lastcraft.skyblock.dependencies.top;

import net.lastcraft.api.util.LocationUtil;
import net.lastcraft.base.sql.ConnectionConstants;
import net.lastcraft.base.sql.api.MySqlDatabase;
import net.lastcraft.dartaapi.armorstandtop.ArmorStandTopManager;
import net.lastcraft.dartaapi.armorstandtop.StandTop;
import net.lastcraft.market.utils.PlayerLoader;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.dependencies.DependManager;
import net.lastcraft.skyblock.dependencies.SkyBlockDepend;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SkyBlockTop extends SkyBlockDepend {

    private static final String SQL_TOP_MONEY = "SELECT * FROM `MarketPlayer` ORDER BY `money` DESC LIMIT 5;";

    private final Map<Integer, MoneyData> moneyDataMap = new ConcurrentHashMap<>();
    private final Map<Integer, LevelTop> levelTopMap = new ConcurrentHashMap<>();

    private ArmorStandTopManager armorStandTopManager;
    private boolean enable;

    private long cooldown;

    public SkyBlockTop(DependManager manager) {
        super(manager);
    }

    @Override
    protected void init() {
        enable = true;
    }

    @Override
    protected void loadConfig() {
        if (enable) {
            return;
        }

        armorStandTopManager = new ArmorStandTopManager(manager.getSkyBlock(),
                "s1" + ConnectionConstants.DOMAIN.getValue(),
                SkyBlockAPI.getDatabase(), "IslandTopStand");

        moneyDataMap.clear();
        levelTopMap.clear();
        setDefaultValues();
        setValues();

        Runnable runnable = () -> {
            cooldown--;
            if (cooldown > 0)
                return;

            setValues();
        };
        runnable.run();
        manager.getRunnable().put("обновлние топа бабок", runnable);

        FileConfiguration config = manager.getSkyBlock().getConfig();

        AtomicInteger pos = new AtomicInteger(1);
        List<StandTop> moneyTop = new ArrayList<>();
        List<StandTop> levelTop = new ArrayList<>();
        config.getStringList("armorStandTop").forEach(stringLoc -> {
            Location location = LocationUtil.stringToLocation(stringLoc, true);
            MoneyData moneyData = moneyDataMap.get(pos.get());
            LevelTop levelData = levelTopMap.get(pos.get());
            if (moneyData != null) {
                StandTop standTop = new StandTop(location, pos.get(), moneyData);
                moneyTop.add(standTop);
            }
            if (levelData != null) {
                StandTop standTop = new StandTop(location, pos.get(), levelData);
                levelTop.add(standTop);
            }
            pos.getAndIncrement();
        });
        armorStandTopManager.addArmorStandTop(moneyTop, "ISLAND_TOP_MONEY_HOLO");
        armorStandTopManager.addArmorStandTop(levelTop, "ISLAND_TOP_LEVEL_HOLO");
    }

    private void setDefaultValues() {
        moneyDataMap.put(1, new MoneyData(1));
        moneyDataMap.put(2, new MoneyData(1));
        moneyDataMap.put(3, new MoneyData(1));
        moneyDataMap.put(4, new MoneyData(1));
        moneyDataMap.put(5, new MoneyData(1));

        levelTopMap.put(1, new LevelTop(1));
        levelTopMap.put(2, new LevelTop(1));
        levelTopMap.put(3, new LevelTop(1));
        levelTopMap.put(4, new LevelTop(1));
        levelTopMap.put(5, new LevelTop(1));
    }

    private void setValues() {
        cooldown = TimeUnit.MINUTES.toSeconds(5); //время кулдауна

        MySqlDatabase mySqlDatabase = PlayerLoader.getMySqlDatabase();
        mySqlDatabase.executeQuery(SQL_TOP_MONEY, (rs) -> {
            int pos = 1;
            while (rs.next()) {
                int playerID = rs.getInt("playerID");
                double money = rs.getDouble("money");
                MoneyData moneyData = moneyDataMap.get(pos);
                if (moneyData != null)
                    moneyData.update(playerID, money);
                pos++;
            }
            return Void.TYPE;
        });

        AtomicInteger pos = new AtomicInteger(1);
        ISLAND_MANAGER.getPlayerIsland().values()
                .stream()
                .sorted(Comparator.comparingInt(Island::getLevel).reversed())
                .limit(5)
                .forEach(island -> {
                    LevelTop levelTop = levelTopMap.get(pos.get());
                    if (levelTop != null)
                        levelTop.update(island.getOwner().getPlayerID(), island.getLevel());
                    pos.getAndIncrement();
                });

        armorStandTopManager.updateTops();
    }

    @Override
    protected void disable() {
        armorStandTopManager.onDisable();
        enable = false;
    }
}
