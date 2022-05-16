package net.lastcraft.alternate;

import net.lastcraft.alternate.api.AlternateGui;
import net.lastcraft.alternate.commands.*;
import net.lastcraft.alternate.commands.get.HeadCommand;
import net.lastcraft.alternate.commands.get.KitCommand;
import net.lastcraft.alternate.commands.home.BedHomeCommand;
import net.lastcraft.alternate.commands.home.HomeCommand;
import net.lastcraft.alternate.commands.home.RemoveHomeCommand;
import net.lastcraft.alternate.commands.home.SetHomeCommand;
import net.lastcraft.alternate.commands.info.*;
import net.lastcraft.alternate.commands.tp.*;
import net.lastcraft.alternate.commands.warp.*;
import net.lastcraft.alternate.config.AlternateSql;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.alternate.listener.GuiListener;
import net.lastcraft.alternate.listener.KitListener;
import net.lastcraft.alternate.listener.PlayerListener;
import net.lastcraft.alternate.listener.UserListener;
import net.lastcraft.alternate.util.GuiThread;
import net.lastcraft.alternate.util.TeleportingUtil;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.game.GameSettings;
import net.lastcraft.api.game.MiniGameType;
import net.lastcraft.api.player.Spigot;
import net.lastcraft.api.util.ConfigManager;
import net.lastcraft.dartaapi.utils.core.CoreUtil;
import net.lastcraft.dartaapi.utils.core.RestartServer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Alternate extends JavaPlugin {

    private FileConfiguration config;
    private static ConfigData configData;

    @Override
    public void onEnable() {
        GameSettings.minigame = MiniGameType.SURVIVAL;

        configData = new ConfigData(this);
        AlternateGui.setConfigData(configData);

        configData.load();
        AlternateSql.init();
        configData.init();

        new UserListener(this);
        new TeleportingUtil(this);
        registerCommand();
        new GuiListener(this);
        new PlayerListener(this);
        new KitListener(this);
        new GuiThread();
    }

    @Override
    public void reloadConfig() {
        File file = new File(CoreUtil.getConfigDirectory() + "/alternate.yml");
        if (!file.exists()) {
            Spigot spigot = LastCraft.getGamerManager().getSpigot();
            spigot.sendMessage("§c[Alternate] Конфиг не найден! Плагин выключается");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        ConfigManager configManager = new ConfigManager(file);
        config = configManager.getConfig();
    }

    @Override
    public void saveConfig() {
        //nothing
    }

    @Override
    public void saveDefaultConfig() {
        reloadConfig();

        if (config.contains("Restart")) {
            new RestartServer(config.getString("Restart"));
        }
    }

    @Override
    public FileConfiguration getConfig() {
        return config;
    }

    public static ConfigData getConfigData() {
        return configData;
    }

    //todo рандом телепортер

    @Override
    public void onDisable() {
        AlternateSql.getMySqlDatabase().close();
    }

    private void registerCommand() {
        new RecipeCommand(configData);
        new FlyCommand(configData);
        new ClearCommand(configData);
        new ReloadCommand(this);
        new ExtCommand(configData);
        new HealCommand(configData);
        new SpeedCommand(configData);
        new SpawnCommand(configData);
        new ListCommand(configData);
        new NearCommand(configData);
        new GodCommand(configData);
        new FeedCommand(configData);
        new WorkbenchCommand(configData);
        new SuicideCommand(configData);
        new AnvilCommand(configData);
        new RepairCommand(configData);
        new EnderChestCommand(configData);
        new PTimeCommand(configData);
        new GamemodeCommand(configData);
        new TpCommand(configData);
        new TopCommand(configData);
        new ItemdbCommand(configData);
        new SCommand(configData);
        new TpDenyCommand(configData);
        new BackCommand(configData);
        new JumpCommand(configData);
        new TpPosCommand(configData);
        new TpChunkCommand(configData);
        new HatCommand(configData);
        new HeadCommand(configData);

        if (configData.isWarpSystem()) {
            new WarpCommand(configData);
            new DelWarpCommand(configData);
            new PlayerWarpCommand(configData);
            new CreateWarpCommand(configData);
            new WarpInfoCommand(configData);
        }

        if (configData.isCallSystem()) {
            new CallCommand(configData);
            new TpToggleCommand(configData);
            new TpacceptCommand(configData);
        }

        if (configData.isKitSystem())
            new KitCommand(configData);


        if (configData.isHomeSystem()) {
            new HomeCommand(configData);
            new SetHomeCommand(configData);
            new RemoveHomeCommand(configData);
        }

        if (configData.isTrade())
            new TradeCommand(this);


        if (configData.isBedHomeSystem())
            new BedHomeCommand(configData);

    }
}
