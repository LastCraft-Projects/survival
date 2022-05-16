package net.lastcraft.creative;

import net.lastcraft.base.locale.Language;
import net.lastcraft.creative.command.CreativeMenuCommand;
import net.lastcraft.creative.command.WorldCommand;
import net.lastcraft.creative.gui.CreativeMenuGui;
import net.lastcraft.creative.listener.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class CreativeAddon extends JavaPlugin {

    private final Map<Integer, CreativeMenuGui> menus = new HashMap<>();

    @Override
    public void onEnable() {

        for (Language language : Language.values()) {
            menus.put(language.getId(), new CreativeMenuGui(language));
        }

        new WorldCommand();
        new CreativeMenuCommand(menus);

        new PlayerListener(this);
    }

    public Map<Integer, CreativeMenuGui> getMenus() {
        return menus;
    }
}
