package net.lastcraft.skyblock.gui.guis;

import net.lastcraft.api.inventory.MultiInventory;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.sound.SoundType;
import net.lastcraft.api.util.InventoryUtil;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.SkyBlockGui;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.manager.SkyGamerManager;
import net.lastcraft.skyblock.craftisland.CraftSkyGamer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RequestIslandGui extends SkyBlockGui {

    private static final SkyGamerManager SKY_GAMER_MANAGER = SkyBlockAPI.getSkyGamerManager();
    private static final String KEY = "ISLAND_REGUEST_GUI_NAME";

    private MultiInventory inventory;

    public RequestIslandGui(Player player) {
        super(player, "");
        inventory = API.createMultiInventory(player, lang.getMessage(KEY), 5);
    }

    @Override
    protected void setItems(Player player) {
        CraftSkyGamer skyGamer = (CraftSkyGamer) SKY_GAMER_MANAGER.getSkyGamer(player);
        if (skyGamer == null)
            return;

        int size = skyGamer.getRequests().size();

        int pagesCount = InventoryUtil.getPagesCount(size, 21);
        inventory.clearInventories();
        API.pageButton(lang, pagesCount, inventory.getInventories(), 38, 42);

        if (size == 0) {
            inventory.setItem(0,22, API.createItem(ItemUtil.getBuilder(Material.GLASS_BOTTLE)
                    .setName(lang.getMessage("CALL_ITEM_EMPTY_NAME"))
                    .build(),
                    (clicker, clickType, slot1) -> SOUND_API.play(clicker, SoundType.TELEPORT)));
            return;
        }

        int pageNum = 0;
        int slot = 10;
        for (String name : skyGamer.getRequests().keySet()) {
            BukkitGamer gamer = GAMER_MANAGER.getGamer(name);
            if (gamer == null)
                continue;

            Island island = ISLAND_MANAGER.getIsland(name);
            if (island == null)
                continue;

            int time = 121 - (int) ((System.currentTimeMillis() - skyGamer.getRequests().get(name)) / 1000);

            List<String> lore = lang.getList("ISLAND_REQUESTS_ITEM_HEAD_LORE", String.valueOf(time),
                    StringUtil.getCorrectWord(time, "TIME_SECOND_1", lang), island.getOwner().getDisplayName());

            ItemStack itemStack = ItemUtil.getBuilder(gamer.getHead())
                    .setName(gamer.getChatName())
                    .setLore(lore)
                    .build();

            inventory.setItem(pageNum, slot, API.createItem(itemStack, (clicker, clickType, i) -> {
                SOUND_API.play(clicker, SoundType.SELECTED);
                if (clickType.isRightClick())
                    clicker.chat("/cancel " + name);

                if (clickType.isLeftClick()) {
                    clicker.chat("/accept " + name);
                    clicker.closeInventory();
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

    }

    @Override
    public void open() {
        if (inventory == null)
            return;

        inventory.openInventory(player);
    }
}
