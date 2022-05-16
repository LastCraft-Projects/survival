package net.lastcraft.alternate.gui;

import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.AlternateGui;
import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.object.CraftUser;
import net.lastcraft.api.inventory.DInventory;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.sound.SoundType;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MainGui extends AlternateGui<DInventory> {

    public MainGui(Player player) {
        super(player);
    }

    @Override
    protected void createInventory() {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        lang = gamer.getLanguage();
        dInventory = INVENTORY_API.createInventory(player, lang.getMessage("ALTERNATE_GUI_NAME"), 5);
    }

    @Override
    public void updateItems() {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        User user = USER_MANAGER.getUser(player);
        if (user == null || gamer == null)
            return;

        ItemStack home;
        if (configData.isHomeSystem()) {
            int homeSize = user.getHomes().size();
            home = ItemUtil.getBuilder(Material.BED)
                    .setName("§a" + lang.getMessage("HOME_GUI_NAME"))
                    .setLore(lang.getList("HOME_GUI_LORE",
                            String.valueOf(homeSize), StringUtil.getCorrectWord(homeSize, "HOMES_2", lang)))
                    .build();
            dInventory.setItem(10, INVENTORY_API.createItem(home, (player, clickType, i) -> {
                MANAGER.getGui(HomeGui.class, player).open();
                SOUND_API.play(player, SoundType.CLICK);
            }));
        } else {
            home = ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                    .setDurability((short) 14)
                    .setName("§c" + lang.getMessage("HOME_GUI_NAME"))
                    .setLore(lang.getList("SYSTEM_DISABLED_LORE"))
                    .build();
            dInventory.setItem(10, INVENTORY_API.createItem(home));
        }

        ItemStack kits;
        if (configData.isKitSystem()) {
            kits = ItemUtil.getBuilder(Material.IRON_SWORD)
                    .setName("§a" + lang.getMessage("KIT_GUI_NAME"))
                    .setLore(lang.getList( "KIT_GUI_LORE"))
                    .build();
            dInventory.setItem(13, INVENTORY_API.createItem(kits, (player, clickType, i) -> {
                MANAGER.getGui(KitGui.class, player).open();
                SOUND_API.play(player, SoundType.CLICK);
            }));
        } else {
            kits = ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                    .setDurability((short) 14)
                    .setName("§c" + lang.getMessage("KIT_GUI_NAME"))
                    .setLore(lang.getList("SYSTEM_DISABLED_LORE"))
                    .build();
            dInventory.setItem(13, INVENTORY_API.createItem(kits));
        }

        if (configData.isWarpSystem()) {
            int sizeWarps = AlternateAPI.getWarpManager().getWarps().size();
            dInventory.setItem(16, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.SIGN)
                    .setName("§a" + lang.getMessage("WARP_GUI_NAME"))
                    .setLore(lang.getList("WARP_GUI_LORE", String.valueOf(sizeWarps)))
                    .build(), (player, clickType, i) -> {
                AlternateGui gui = MANAGER.getGui(MyWarpGui.class, player);

                if (clickType.isLeftClick())
                    gui = MANAGER.getGui(WarpGui.class, player);

                if (gui == null)
                    return;

                gui.open();
                SOUND_API.play(player, SoundType.CLICK);
            }));
        } else {
            dInventory.setItem(16, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                    .setDurability((short) 14)
                    .setName("§c" + lang.getMessage("WARP_GUI_NAME"))
                    .setLore(lang.getList("SYSTEM_DISABLED_LORE"))
                    .build()));
        }

        if (configData.isCallSystem()) {
            int requests = ((CraftUser)(user)).getCallReguests().size();
            dInventory.setItem(9 * 3 + 3, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.PAPER)
                    .setName("§a" + lang.getMessage("CALL_GUI_NAME"))
                    .setLore(lang.getList("CALL_GUI_LORE",
                            String.valueOf(requests),
                            StringUtil.getCorrectWord(requests, "PLAYERS_1", lang)))
                    .build(), (player, clickType, i) -> {
                TpacceptGui tpacceptGui = MANAGER.getGui(TpacceptGui.class, player);
                if (tpacceptGui != null)
                    tpacceptGui.open();
                SOUND_API.play(player, SoundType.CLICK);
            }));
        } else {
            dInventory.setItem(9 * 3 + 3, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                    .setDurability((short) 14)
                    .setName("§c" + lang.getMessage("CALL_GUI_NAME"))
                    .setLore(lang.getList("SYSTEM_DISABLED_LORE"))
                    .build()));
        }

        dInventory.setItem(9 * 3 + 5, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.DOUBLE_PLANT)
                .setName("§a" + lang.getMessage("PTIME_GUI_NAME"))
                .setLore(lang.getList( "PTIME_ITEM_LORE",
                        Group.getGroupByLevel(configData.getInt("ptimeCommand")).getNameEn()))
                .build(), (player, clickType, i) -> MANAGER.getGui(PTimeGui.class, player).open()));

        if (!configData.isCallSystem())
            return;
        ItemStack itemStack;
        if (user.isTpToggle()) {
            itemStack = ItemUtil.getBuilder(Material.INK_SACK)
                    .setDurability((short) 10)
                    .setName(lang.getMessage("TPTOGGLE_ITEM_NAME",
                            lang.getMessage("ENABLED")))
                    .setLore(lang.getList("ITEMS_LOBBY_ENABLE_LORE"))
                    .build();
        } else {
            itemStack = ItemUtil.getBuilder(Material.INK_SACK)
                    .setDurability((short) 8)
                    .setName(lang.getMessage("TPTOGGLE_ITEM_NAME", "§c"
                            + lang.getMessage("DISABLED")))
                    .setLore(lang.getList("ITEMS_LOBBY_DISABLE_LORE"))
                    .build();
        }
        dInventory.setItem(44, INVENTORY_API.createItem(itemStack,
                (player, clickType, slot1) -> player.chat("/tptoggle")));
    }
}
