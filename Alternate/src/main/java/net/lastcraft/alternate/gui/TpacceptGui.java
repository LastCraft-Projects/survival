package net.lastcraft.alternate.gui;

import net.lastcraft.alternate.api.AlternateGui;
import net.lastcraft.alternate.object.CraftUser;
import net.lastcraft.api.inventory.MultiInventory;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.sound.SoundType;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class TpacceptGui extends AlternateGui<MultiInventory> {

    public TpacceptGui(Player player) {
        super(player);
    }

    @Override
    protected void createInventory() {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;
        lang = gamer.getLanguage();
        dInventory = INVENTORY_API.createMultiInventory(player, lang.getMessage("ALTERNATE_GUI_NAME")
                + " ▸ " + lang.getMessage("CALL_GUI_NAME"), 5);
    }

    @Override
    public void updateItems() {
        CraftUser craftUser = (CraftUser) USER_MANAGER.getUser(player);
        if (craftUser == null)
            return;

        int size = craftUser.getCallReguests().size();

        dInventory.clearInventories();

        if (size == 0) {
            dInventory.setItem(0, 22, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.GLASS_BOTTLE)
                    .setName(lang.getMessage("CALL_ITEM_EMPTY_NAME"))
                    .setLore(lang.getList( "CALL_ITEM_EMPTY_LORE"))
                    .build(), (player, clickType, slot1) -> SOUND_API.play(player, SoundType.TELEPORT)));
            setAction(dInventory, lang, size);
            return;
        }

        int pageNum = 0;
        int slot = 10;
        for (String name : craftUser.getCallReguests().keySet()) {
            if (dInventory.getInventories().isEmpty()) {
                continue;
            }

            BukkitGamer gamer = GAMER_MANAGER.getGamer(name);
            if (gamer == null) {
                continue;
            }

            int time = 121 - (int) ((System.currentTimeMillis() - craftUser.getCallReguests().get(name)) / 1000);

            List<String> lore = lang.getList("CALL_ITEM_HEAD_LORE", String.valueOf(time),
                    StringUtil.getCorrectWord(time, "TIME_SECOND_1", lang));
            dInventory.setItem(pageNum, slot, INVENTORY_API.createItem(ItemUtil.getBuilder(gamer.getHead())
                            .setName(gamer.getChatName())
                            .setLore(lore)
                            .build(), (player, clickType, i) -> {
                        SOUND_API.play(player, SoundType.SELECTED);
                        if (clickType.isRightClick())
                            player.chat("/tpdeny " + name);

                        if (clickType.isLeftClick()) {
                            player.chat("/tpaccept " + name);
                            player.closeInventory();
                        }
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
