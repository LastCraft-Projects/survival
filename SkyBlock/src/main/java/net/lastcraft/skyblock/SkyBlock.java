package net.lastcraft.skyblock;

import lombok.Getter;
import net.lastcraft.api.game.GameSettings;
import net.lastcraft.api.game.MiniGameType;
import net.lastcraft.api.util.ConfigManager;
import net.lastcraft.base.sql.ConnectionConstants;
import net.lastcraft.dartaapi.achievements.manager.AchievementManager;
import net.lastcraft.dartaapi.listeners.JoinListener;
import net.lastcraft.dartaapi.listeners.RedstoneFixListener;
import net.lastcraft.dartaapi.loader.DartaAPI;
import net.lastcraft.dartaapi.utils.ArmorStandUtil;
import net.lastcraft.dartaapi.utils.bukkit.EmptyWorldGenerator;
import net.lastcraft.dartaapi.utils.core.CoreUtil;
import net.lastcraft.skyblock.achievement.AchievementListener;
import net.lastcraft.skyblock.achievement.IslandAchievements;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.command.*;
import net.lastcraft.skyblock.dependencies.DependManager;
import net.lastcraft.skyblock.gui.GuiListener;
import net.lastcraft.skyblock.listener.*;
import net.lastcraft.skyblock.utils.FaweUtils;
import net.lastcraft.skyblock.utils.IslandLoader;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public final class SkyBlock extends JavaPlugin {

    private ConfigManager configManager;

    private DependManager dependManager;
    private AchievementManager achievementManager;

    @Override
    public final void onEnable() {
        EmptyWorldGenerator generator = DartaAPI.getInstance().getGenerator();
        Bukkit.createWorld(WorldCreator.name(SkyBlockAPI.getSkyBlockWorldName())
                .generator(generator)
                .generateStructures(false));

        World pvpWorld = Bukkit.createWorld(WorldCreator.name("PvPWorld")
                .generator(generator)
                .generateStructures(false));

        GameSettings.minigame = MiniGameType.SURVIVAL;

        File file = new File(CoreUtil.getConfigDirectory() + "/skyConfig.yml");
        if (!file.exists()) {
            Bukkit.getConsoleSender().sendMessage(
                    "§cКонфиг не найден, кажется некоторые вещи работать не будут и плагин будет отключен");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        configManager = new ConfigManager(file);

        IslandLoader.init();
        dependManager = new DependManager(this);

        achievementManager = new AchievementManager(this,
                "skyblock",
                "s1" + ConnectionConstants.DOMAIN.getValue());
        achievementManager.setLoadOnJoin(true);
        achievementManager.addAchievements(IslandAchievements.getAchievements());

        new JoinListener(this);
        new ProtectLobbyListener(this);
        new PvpListener(this, pvpWorld);

        new ModuleListener(this);
        new SkyGamerListener(this);
        new ProtectListener(this);
        new RedstoneFixListener(this);
        new IslandMainListener(this);
        new FlagsListener(this);
        new EventRewrite(this);
        new AchievementListener(this);

        new GuiListener(this, new IslandsCommand());

        new IslandCommand();
        new HomeCommand();
        new AcceptCommand();
        new CancelCommand();
        new AchievementCommand(achievementManager); //todo удалить вместе с классом

        ArmorStandUtil.fixArmorStand();

        FaweUtils.disableCommand(this);
    }

    @Override
    public final void onDisable() {
        //World world = SkyBlockAPI.getSkyBlockWorld();
        //world.save();

        //Closer closer = Closer.create();
        //try {
        //    closer.register(guiListener);
        //    closer.register(dependManager);

        //    closer.close();
        //} catch (Throwable ex) {
        //    ex.printStackTrace();
        //}
        IslandLoader.close();
    }

    @Override
    public final void reloadConfig() {
        configManager.reloadConfig();
        dependManager.loadAllConfigs();
    }

    @Override
    public final FileConfiguration getConfig() {
        return configManager.getConfig();
    }
}
