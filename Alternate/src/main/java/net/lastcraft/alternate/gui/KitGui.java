package net.lastcraft.alternate.gui;

import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.AlternateGui;
import net.lastcraft.alternate.api.Kit;
import net.lastcraft.alternate.api.User;
import net.lastcraft.api.inventory.MultiInventory;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.sound.SoundType;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.util.TimeUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitGui extends AlternateGui<MultiInventory> {

    public KitGui(Player player) {
        super(player);
    }

    @Override
    protected void createInventory() {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        lang = gamer.getLanguage();
        dInventory = INVENTORY_API.createMultiInventory(player, lang.getMessage("ALTERNATE_GUI_NAME")
                + " ▸ " + lang.getMessage("KIT_GUI_NAME"), 5);
    }

    @Override
    public void updateItems() {
        User user = USER_MANAGER.getUser(player);
        if (user == null)
            return;

        BukkitGamer mainGamer = GAMER_MANAGER.getGamer(player);
        if (mainGamer == null)
            return;

        int size = user.getKits().size();

        int pageNum = 0;
        int slot = 10;
        for (Kit kit : AlternateAPI.getKitManager().getKits().values()) {
            if (dInventory.getInventories().isEmpty() || kit.isStart()) {
                continue;
            }

            List<String> lore = new ArrayList<>(lang.getList("KIT_DEFAULT_LORE"));
            for (ItemStack itemStack : kit.getItems()) {
                lore.add(" §8• §7" + itemStack.getType().name() + " §8x" + itemStack.getAmount());
            }

            ItemStack finalItem = ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                    .setAmount(1)
                    .setDurability((short) 14)
                    .build();
            boolean red = true;

            if (user.isCooldown(kit)) {
                long time = user.getCooldown(kit) * 1000 + System.currentTimeMillis();
                String timeBuilder = TimeUtil.leftTime(lang, time, true);
                lore.addAll(lang.getList( "KIT_LORE_DISABLE", timeBuilder));
            } else {
                if (kit.getDefaultGroup() != null && kit.getDefaultGroup() == mainGamer.getGroup()) {
                    lore.addAll(lang.getList( "KIT_LORE_ENABLE"));
                    finalItem = kit.getIcon();
                    red = false;
                } else if (kit.getDefaultGroup() == null
                        && kit.getMinimalGroup().getLevel() <= mainGamer.getGroup().getLevel()) {
                    lore.addAll(lang.getList("KIT_LORE_ENABLE"));
                    finalItem = kit.getIcon();
                    red = false;
                } else {
                    if (kit.getDefaultGroup() == null) {
                        lore.addAll(lang.getList("NO_PERMS_KIT", kit.getMinimalGroup().getNameEn()));
                    } else {
                        lore.addAll(lang.getList("NO_PERMS_KIT_ONLY", kit.getDefaultGroup().getNameEn()));
                    }
                }
            }

            finalItem = ItemUtil.getBuilder(finalItem)
                    .setName((red ? "§c" : "§a") + kit.getName())
                    .setLore(lore)
                    .removeFlags()
                    .build();
            boolean finalRed = red;
            dInventory.setItem(pageNum, slot, INVENTORY_API.createItem(finalItem,
                    (player, clickType, i) -> {
                if (!user.isCooldown(kit) && !finalRed) {

                    mainGamer.sendMessage(configData.getPrefix()
                            + lang.getMessage("KIT_SELECT", kit.getName()));
                    Inventory inventory = player.getInventory();
                    kit.getItems().forEach(inventory::addItem);
                    user.addKit(kit);
                    //player.chat("/kit " + kit.getName());
                    SOUND_API.play(player, SoundType.SELECTED);
                    player.closeInventory();
                    return;
                }
                SOUND_API.play(player, SoundType.TELEPORT);
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
