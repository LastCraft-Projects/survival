package net.lastcraft.skyblock.gui;

import lombok.experimental.UtilityClass;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.DInventory;
import net.lastcraft.api.inventory.InventoryAPI;
import net.lastcraft.api.inventory.MultiInventory;
import net.lastcraft.api.manager.GuiManager;
import net.lastcraft.api.sound.SoundAPI;
import net.lastcraft.api.sound.SoundType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.dartaapi.guis.CustomItems;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.SkyBlockGui;
import net.lastcraft.skyblock.gui.guis.ProfileGui;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class GuiUtil {

    private static final InventoryAPI API = LastCraft.getInventoryAPI();
    private static final SoundAPI SOUND_API = LastCraft.getSoundAPI();
    private static final GuiManager<SkyBlockGui> SKY_GUI_MANAGER = SkyBlockAPI.getSkyGuiManager();

    public void setBack(DInventory inventory, Language lang) {
        ItemStack backItem = CustomItems.getBack(lang);

        inventory.setItem(4 * 9 + 4, API.createItem(backItem, (player, clickType, i) -> {
            ProfileGui profileGui = SKY_GUI_MANAGER.getGui(ProfileGui.class, player);
            if (profileGui != null)
                profileGui.open();
            SOUND_API.play(player, SoundType.PICKUP);
        }));
    }

    public void setBack(MultiInventory multiInventory, Language lang) {
        multiInventory.getInventories().forEach(inventory -> setBack(inventory, lang));
    }
}
