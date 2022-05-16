package net.lastcraft.anarchy;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.lastcraft.anarchy.command.AnarchystartCommand;
import net.lastcraft.anarchy.command.MoneyCommand;
import net.lastcraft.anarchy.gui.AnarchyMenuGui;
import net.lastcraft.anarchy.listener.PlayerListener;
import net.lastcraft.anarchy.listener.ScavengerListener;
import net.lastcraft.anarchy.stats.StatsLoader;
import net.lastcraft.anarchy.top.TopManager;
import net.lastcraft.api.game.GameSettings;
import net.lastcraft.api.game.MiniGameType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.dartaapi.utils.core.RestartServer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Anarchy extends JavaPlugin {

    private AnarchyConfig config;

    private TopManager topManager;

    private final TIntObjectMap<AnarchyMenuGui> menus = new TIntObjectHashMap<>();

    @Override
    public void onEnable() {
        GameSettings.minigame = MiniGameType.SURVIVAL;
        config = new AnarchyConfig(this);

        StatsLoader.init();

        new RestartServer("05:00");

        for (Language language : Language.values()) {
            menus.put(language.getId(), new AnarchyMenuGui(language));
        }

        new PlayerListener(this);
        new ScavengerListener(this);

        new MoneyCommand();
        new AnarchystartCommand(menus);

        topManager = new TopManager(this);
    }

    public AnarchyConfig getAnarchyConfig() {
        return config;
    }

    @Override
    public FileConfiguration getConfig() {
        return getAnarchyConfig().getConfig();
    }
}
