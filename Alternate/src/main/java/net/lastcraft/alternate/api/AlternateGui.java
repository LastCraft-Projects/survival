package net.lastcraft.alternate.api;

import lombok.Setter;
import net.lastcraft.alternate.api.manager.UserManager;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.alternate.gui.MainGui;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.*;
import net.lastcraft.api.manager.GuiManager;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.sound.SoundAPI;
import net.lastcraft.api.sound.SoundType;
import net.lastcraft.api.util.InventoryUtil;
import net.lastcraft.base.locale.Language;
import net.lastcraft.dartaapi.guis.CustomItems;
import org.bukkit.entity.Player;

public abstract class AlternateGui<T extends BaseInventory> {
    protected final static GuiManager<AlternateGui> MANAGER = AlternateAPI.getGuiManager();
    protected static final UserManager USER_MANAGER = AlternateAPI.getUserManager();
    protected static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();

    protected static final InventoryAPI INVENTORY_API = LastCraft.getInventoryAPI();
    protected static final SoundAPI SOUND_API = LastCraft.getSoundAPI();

    @Setter
    protected static ConfigData configData;

    protected Player player;
    protected T dInventory;
    protected Language lang;

    protected boolean opened = false;

    protected AlternateGui(Player player) {
        this.player = player;
        this.createInventory();
        this.update();
    }

    protected abstract void createInventory();

    public void update() {
        if (dInventory == null)
            return;
        if (player == null || !player.isOnline())
            return;
        updateItems();
    }

    protected abstract void updateItems();

    public Player getPlayer() {
        return player;
    }

    public void open() {
        if (dInventory == null)
            return;
        if (player == null || !player.isOnline())
            return;
        dInventory.openInventory(player);
    }

    protected void setAction(MultiInventory multiInventory, Language lang, int size) {
        int pagesCount = InventoryUtil.getPagesCount(size, 21);
        if (size == 0)
            pagesCount = 1;
        for (DInventory inventory : multiInventory.getInventories()) {
            inventory.createInventoryAction(new InventoryAction() {
                @Override
                public void onOpen(Player player) {
                    opened = true;
                }
                @Override
                public void onClose(Player player) {
                    opened = false;
                }
            });
            inventory.setItem(40, INVENTORY_API.createItem(CustomItems.getBack(lang), (player, clickType, slot) -> {
                SOUND_API.play(player, SoundType.PICKUP);
                MainGui mainGui = MANAGER.getGui(MainGui.class, player);
                if (mainGui != null)
                    mainGui.open();
            }));
        }
        INVENTORY_API.pageButton(lang, pagesCount, multiInventory, 38, 42);
    }

    @Override
    public String toString() {
        return "AlternateGui{name = " + this.getClass().getSimpleName() + ", Player = {" + player.getName() + "}}";
    }

    public static void setConfigData(ConfigData configData) {
        AlternateGui.configData = configData;
    }
}
