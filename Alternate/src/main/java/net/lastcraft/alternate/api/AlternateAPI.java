package net.lastcraft.alternate.api;

import lombok.Setter;
import net.lastcraft.alternate.api.manager.KitManager;
import net.lastcraft.alternate.api.manager.UserManager;
import net.lastcraft.alternate.api.manager.WarpManager;
import net.lastcraft.alternate.managers.CraftAlternateGuiManager;
import net.lastcraft.alternate.managers.CraftKitManager;
import net.lastcraft.alternate.managers.CraftUserManager;
import net.lastcraft.alternate.managers.CraftWarpManager;
import net.lastcraft.api.manager.GuiManager;
import org.bukkit.Location;

public final class AlternateAPI {
    private static UserManager userManager;
    private static WarpManager warpManager;
    private static KitManager kitManager;
    private static GuiManager guiManager;
    @Setter
    private static Location spawn;

    /**
     * Интерфейс для работы с User'ами
     * @return - UserManager
     */
    public static UserManager getUserManager() {
        if (userManager == null)
            userManager = new CraftUserManager();

        return userManager;
    }

    public static Location getSpawn() {
        return spawn.clone();
    }

    /**
     * Интерфейс для работы с warp'ами
     * @return - WarpManager
     */
    public static WarpManager getWarpManager() {
        if (warpManager == null)
            warpManager = new CraftWarpManager();

        return warpManager;
    }

    /**
     * Интерфейс для работы с kit'ами
     * @return - KitManager
     */
    public static KitManager getKitManager() {
        if (kitManager == null)
            kitManager = new CraftKitManager();

        return kitManager;
    }

    /**
     * интерфейс для работы с gui
     * @return - guimanager
     */
    public static GuiManager<AlternateGui> getGuiManager() {
        if (guiManager == null)
            guiManager = new CraftAlternateGuiManager();

        return guiManager;
    }
}
