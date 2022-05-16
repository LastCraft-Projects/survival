package net.lastcraft.skyblock.manager;

import net.lastcraft.api.manager.GuiManager;
import net.lastcraft.skyblock.api.SkyBlockGui;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CraftSkyGuiManager implements GuiManager<SkyBlockGui> {

    private final Map<String, Map<String, SkyBlockGui>> playerGuis = new ConcurrentHashMap<>();
    private final Set<String> guis = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void createGui(Class<? extends SkyBlockGui> clazz) {
        String name = clazz.getSimpleName().toLowerCase();
        if (guis.contains(name))
            return;

        guis.add(name);
    }

    @Override
    public void removeGui(Class<? extends SkyBlockGui> clazz) {
        String nameClazz = clazz.getSimpleName().toLowerCase();
        for (String name : playerGuis.keySet()) {
            Map<String, SkyBlockGui> guis = playerGuis.get(name);
            for (String guiName : guis.keySet()) {
                if (!guiName.equalsIgnoreCase(nameClazz))
                    continue;
                guis.remove(guiName);
            }
        }
    }

    @Override
    public <T extends SkyBlockGui> T getGui(Class<T> clazz, Player player) {
        T gui = null;

        String guiName = clazz.getSimpleName().toLowerCase();
        String name = player.getName().toLowerCase();

        if (guis.contains(guiName)) {
            Map<String, SkyBlockGui> guis = playerGuis.get(name);
            if (guis == null) {
                guis = new ConcurrentHashMap<>();
                playerGuis.put(name, guis);
            }
            gui = (T) guis.get(guiName);

            if (gui == null) {
                try {
                    gui = clazz.getConstructor(Player.class).newInstance(player);
                    guis.put(guiName, gui);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return gui;
    }

    @Override
    public void removeALL(Player player) {
        String name = player.getName().toLowerCase();
        playerGuis.remove(name);
    }


    @Override
    public Map<String, Map<String, SkyBlockGui>> getPlayerGuis() {
        return playerGuis;
    }
}
