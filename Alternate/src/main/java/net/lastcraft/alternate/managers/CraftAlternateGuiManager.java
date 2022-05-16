package net.lastcraft.alternate.managers;

import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import net.lastcraft.alternate.api.AlternateGui;
import net.lastcraft.api.manager.GuiManager;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class CraftAlternateGuiManager implements GuiManager<AlternateGui> {

    private final Map<String, Map<String, AlternateGui>> playerGuis = new ConcurrentHashMap<>();
    private final Set<String> guis = new ConcurrentSet<>();

    @Override
    public void createGui(Class<? extends AlternateGui> clazz) {
        String name = clazz.getSimpleName().toLowerCase();
        if (guis.contains(name))
            return;

        guis.add(name);
    }

    @Override
    public void removeGui(Class<? extends AlternateGui> clazz) {
        String nameClazz = clazz.getSimpleName().toLowerCase();
        for (String name : playerGuis.keySet()) {
            Map<String, AlternateGui> guis = playerGuis.get(name);
            for (String guiName : guis.keySet())
                if (guiName.equalsIgnoreCase(nameClazz) )
                    guis.remove(guiName);
        }
    }

    @Override
    public <T extends AlternateGui> T getGui(Class<T> clazz, Player player) {
        T gui = null;

        String guiName = clazz.getSimpleName().toLowerCase();
        String name = player.getName().toLowerCase();

        if (guis.contains(guiName)) {
            Map<String, AlternateGui> guis = playerGuis.get(name);
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


}
