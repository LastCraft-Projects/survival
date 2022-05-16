package net.lastcraft.alternate.gui;

import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.AlternateGui;
import net.lastcraft.alternate.api.Warp;
import net.lastcraft.alternate.api.manager.WarpManager;
import net.lastcraft.api.inventory.DInventory;
import net.lastcraft.api.inventory.InventoryAction;
import net.lastcraft.api.inventory.MultiInventory;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.sound.SoundType;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.util.StringUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class WarpGui extends AlternateGui<MultiInventory> {
    private static final WarpManager WARP_MANAGER = AlternateAPI.getWarpManager();
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    public WarpGui(Player player) {
        super(player);
    }

    @Override
    protected void createInventory() {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        lang = gamer.getLanguage();
        dInventory = INVENTORY_API.createMultiInventory(lang.getMessage("ALTERNATE_GUI_NAME")
                + " ▸ " + lang.getMessage("WARP_GUI_NAME"), 5);
        dInventory.getInventories().forEach(inventory -> inventory.createInventoryAction(new InventoryAction() {
            @Override
            public void onOpen(Player player) {
                opened = true;
            }

            @Override
            public void onClose(Player player) {
                opened = false;
            }
        }));
    }

    @Override
    public void updateItems() {
        if (opened)
            return;

        BukkitGamer mainGamer = GAMER_MANAGER.getGamer(player);
        if (mainGamer == null)
            return;

        Map<String, Warp> warps = WARP_MANAGER.getWarps();

        int size = 0;
        for (Warp warp : warps.values()) {
            if (warp.isPrivate())
                continue;
            size++;
        }

        dInventory.clearInventories();

        for (DInventory dInventory : dInventory.getInventories()) {
            dInventory.setItem(44, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.SIGN)
                    .setName("§a" + lang.getMessage("MYWARP_ITEM_CHANGER_NAME"))
                    .setLore(lang.getList("MYWARP_ITEM_CHANGER_LORE"))
                    .build(), (player, clickType, i) -> MANAGER.getGui(MyWarpGui.class, player).open()));
        }

        if (size == 0) {
            dInventory.setItem(0, 22, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.GLASS_BOTTLE)
                            .setName(lang.getMessage( "WARP_ITEM_EMPTY_NAME"))
                            .setLore(lang.getList("WARP_ITEM_EMPTY_LORE"))
                            .build(), (player, clickType, slot1) -> SOUND_API.play(player, SoundType.TELEPORT)));
            setAction(dInventory, lang, size);
            return;
        }

        int pageNum = 0;
        int slot = 10;
        for (Warp warp : warps.values().stream()
                .sorted(Comparator.comparingLong(value -> value.getDate().getTime()))
                .collect(Collectors.toList())) {

            if (warp.isPrivate() && !mainGamer.getFriends().contains(warp.getOwner().getPlayerID())) {
                continue;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(warp.getDate().getTime());
            String date = SIMPLE_DATE_FORMAT.format(calendar.getTime());

            String name = warp.getName();
            Location location = warp.getLocation();
            int players = warp.getNearbyPlayers(30).size();
            dInventory.setItem(pageNum, slot, INVENTORY_API.createItem(ItemUtil.getBuilder(warp.getIcon())
                    .setName("§a" + name)
                    .setLore(lang.getList( "WARP_ITEM_LORE",
                            warp.getNameOwner(),
                            date,
                            location.getWorld().getName(),
                            String.valueOf((int) location.getX()),
                            String.valueOf((int) location.getY()),
                            String.valueOf((int) location.getZ()),
                            String.valueOf(players),
                            StringUtil.getCorrectWord(players, "PLAYERS_1", lang)))
                    .build(), (player, clickType, i) -> {
                player.chat("/warp " + name);
                SOUND_API.play(player, SoundType.SELECTED);
                player.closeInventory();
            }));

            if (slot == 16) {
                slot = 19;
            } else if (slot == 25) {
                slot = 28;
            } else if (slot == 34) {
                slot = 10;
                ++pageNum;
            } else {
                ++slot;
            }
        }

        setAction(dInventory, lang, size);
    }

}
