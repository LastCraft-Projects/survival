package net.lastcraft.anarchy;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.Spigot;
import net.lastcraft.api.util.ConfigManager;
import net.lastcraft.dartaapi.utils.core.CoreUtil;
import net.lastcraft.market.Market;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AnarchyConfig {

    private final Spigot spigot = LastCraft.getGamerManager().getSpigot();

    private final Anarchy anarchy;

    @Getter
    private FileConfiguration config;

    public AnarchyConfig(Anarchy anarchy) {
        this.anarchy = anarchy;

        File file = new File(CoreUtil.getConfigDirectory() + "/anarchy.yml");
        if (!file.exists()) {
            spigot.sendMessage("§c[Anarchy] [ERROR] §fКонфиг не найден! Плагин отключается!");
            Bukkit.getPluginManager().disablePlugin(anarchy);
            return;
        }

        ConfigManager configManager = new ConfigManager(file);
        config = configManager.getConfig();
    }


    public TIntObjectMap<Integer> loadScavenger() {
        TIntObjectMap<Integer> data = new TIntObjectHashMap<>();

        config.getStringList("Scavenger").forEach(s -> {
            String[] strings = s.split(";");
            data.put(Integer.valueOf(strings[0]), Integer.valueOf(strings[1]));
        });

        return data;
    }

    public TIntObjectMap<Double> loadMultiMoney() {
        TIntObjectMap<Double> data = new TIntObjectHashMap<>();

        config.getStringList("MobMoneyMulti").forEach(s -> {
            String[] strings = s.split(";");
            double multi = Market.round(Double.valueOf(strings[1]));
            data.put(Integer.valueOf(strings[0]), multi);
        });

        return data;
    }

    public Map<EntityType, Integer> loadMoneyData() {
        Map<EntityType, Integer> data = new HashMap<>();

        config.getStringList("MobMoney").forEach(s -> {
            String[] strings = s.split(";");
            EntityType entityType = EntityType.valueOf(strings[0]);
            data.put(entityType, Integer.valueOf(strings[1]));
        });

        return data;
    }

}
