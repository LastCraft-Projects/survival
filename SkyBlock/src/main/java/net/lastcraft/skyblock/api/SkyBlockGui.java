package net.lastcraft.skyblock.api;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.ClickAction;
import net.lastcraft.api.inventory.DInventory;
import net.lastcraft.api.inventory.InventoryAPI;
import net.lastcraft.api.manager.GuiManager;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.sound.SoundAPI;
import net.lastcraft.base.locale.Language;
import net.lastcraft.dartaapi.guis.CustomItems;
import net.lastcraft.skyblock.api.manager.IslandManager;
import org.bukkit.entity.Player;

public abstract class SkyBlockGui {
    protected static final GuiManager<SkyBlockGui> SKY_GUI_MANAGER = SkyBlockAPI.getSkyGuiManager();
    protected static final InventoryAPI API = LastCraft.getInventoryAPI();
    protected static final IslandManager ISLAND_MANAGER = SkyBlockAPI.getIslandManager();
    protected static final String GUI_NAME_SKY_BLOCK = "ISLAND_PROFILE_GUI_NAME";
    protected static final SoundAPI SOUND_API = LastCraft.getSoundAPI();
    protected static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();

    protected Player player;
    protected DInventory inventory;
    protected Language lang;

    protected boolean opened = false;

    protected SkyBlockGui(Player player) {
        create(player);
        inventory = API.createInventory(player, lang.getMessage(GUI_NAME_SKY_BLOCK), 5);

        setItems(player);
    }

    protected SkyBlockGui(Player player, String key) {
        create(player);
        inventory = API.createInventory(player, this.lang.getMessage(GUI_NAME_SKY_BLOCK)
                + " ▸ "
                + this.lang.getMessage(key), 5);

        setItems(player);
    }

    private void create(Player player) {
        this.lang = Language.RUSSIAN;

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer != null)
            this.lang = gamer.getLanguage();

        this.player = player;
    }

    public void update() {
        if (inventory == null || player == null || !player.isOnline())
            return;

        if (opened)
            return;

        setItems(player);
    }

    protected abstract void setItems(Player player);

    public void open() {
        inventory.openInventory(player);
    }

    protected void setBack(Language lang, DInventory dInventory, ClickAction clickAction) {
        dInventory.setItem(4 * 9 + 4, API.createItem(CustomItems.getBack(lang), clickAction));
    }
}
