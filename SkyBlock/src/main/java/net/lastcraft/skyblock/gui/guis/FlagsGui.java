package net.lastcraft.skyblock.gui.guis;

import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.sound.SoundType;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.SkyBlockGui;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.island.IslandFlag;
import net.lastcraft.skyblock.api.island.member.MemberType;
import net.lastcraft.skyblock.gui.GuiUtil;
import net.lastcraft.skyblock.module.FlagModule;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class FlagsGui extends SkyBlockGui {

    private static final ItemStack RED = ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
            .setDurability((short) 14)
            .build();

    public FlagsGui(Player player) {
        super(player, "ISLAND_FLAG_GUI_NAME");
    }

    @Override
    protected void setItems(Player player) {
        Island island = ISLAND_MANAGER.getIsland(player);
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null || island == null)
            return;

        GuiUtil.setBack(inventory, lang);

        FlagModule flagModule = island.getModule(FlagModule.class);
        if (flagModule == null)
            return;

        int slot = 10;
        for (IslandFlag flag : IslandFlag.values()) {
            boolean result = flagModule.isFlag(flag);
            boolean use = isUse(island, flag);
            String nameGroup = flag.getGroup().getNameEn();

            inventory.setItem(slot, API.createItem(ItemUtil.getBuilder(getMaterial(island, flag))
                    .removeFlags()
                    .glowing(result)
                    .setName((result ? "§a" : "§c") + lang.getMessage("ISLAND_FLAG_" + flag.name() + "_NAME"))
                    .setLore(lang.getList( "ISLAND_FLAG_" + flag.name() + "_LORE"))
                    .addLore("")
                    .addLore((!use ? lang.getList("ISLAND_FLAG_NOT_USED_GUI", nameGroup)
                            : Collections.emptyList()))
                    .addLore(lang.getMessage((result ? "ISLAND_FLAG_DISABLE" : "ISLAND_FLAG_ENABLE")))
                    .build(), (clicker, clickType, i) -> {
                if (island.getMemberType(gamer) == MemberType.MEMBER) {
                    gamer.sendMessage(SkyBlockAPI.getPrefix()
                            + lang.getMessage("ISLAND_YOU_NOT_OWNER"));
                    SOUND_API.play(clicker, SoundType.DESTROY);
                    return;
                }
                if (use) {
                    flagModule.setFlag(flag, !result);
                    SOUND_API.play(clicker, SoundType.CLICK);
                } else {
                    gamer.sendMessage(SkyBlockAPI.getPrefix()
                            + lang.getMessage("ISLAND_FLAG_NOT_USED", nameGroup));
                    SOUND_API.play(clicker, SoundType.NO);
                }
            }));

            slot++;

            if ((slot - 1) % 8 == 0)
                slot += 2;
        }
    }

    private static boolean isUse(Island island, IslandFlag flag) {
        Group group = flag.getGroup();
        int level = island.getOwner().getGroup().getLevel();

        return level >= group.getLevel();
    }

    private static ItemStack getMaterial(Island island, IslandFlag flag) {
        if (isUse(island, flag))
            return new ItemStack(flag.getMaterial());
        else
            return RED.clone();

    }
}
