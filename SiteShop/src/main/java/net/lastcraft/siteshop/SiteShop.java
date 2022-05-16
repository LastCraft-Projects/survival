package net.lastcraft.siteshop;

import lombok.Getter;
import net.lastcraft.api.util.ConfigManager;
import net.lastcraft.dartaapi.utils.core.CoreUtil;
import net.lastcraft.siteshop.commands.GiveCommand;
import net.lastcraft.siteshop.item.SSItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class SiteShop extends JavaPlugin implements Listener {

    @Getter
    private SSItemManager itemManager;
    private ConfigManager configManager;

    @Override
    public final void onEnable() {
        reloadConfig();

        if (configManager == null) {
            return;
        }

        Bukkit.getPluginManager().registerEvents(this, this);

        String database = getConfig().getString("dataBase");
        itemManager = new SSItemManager(database);
        itemManager.loadItemsFromConfig(this);

        new GiveCommand(this);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        if (block != null && block.getType() != null && block.getType() == Material.MOB_SPAWNER) {
            BlockState blockState = block.getState();
            CreatureSpawner spawner = (CreatureSpawner) blockState;
            spawner.setSpawnedType(spawner.getSpawnedType());
            blockState.update(true);
        }
    }

    @Override
    public final FileConfiguration getConfig() {
        return configManager.getConfig();
    }

    @Override
    public final void reloadConfig() {
        File file = new File(CoreUtil.getConfigDirectory() + "/ssconfig.yml");
        if (!file.exists()) {
            Bukkit.getConsoleSender().sendMessage(
                    "§cКонфиг не найден, кажется некоторые вещи работать не будут и плагин будет отключен");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        configManager = new ConfigManager(file);

        if (itemManager != null) {
            itemManager.loadItemsFromConfig(this);
        }
    }

    @Override
    public final void onDisable() {
        if (itemManager != null)  {
            itemManager.close();
        }
    }
}
