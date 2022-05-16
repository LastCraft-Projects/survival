package net.lastcraft.skyblock.gui.general;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.InventoryAPI;
import net.lastcraft.api.inventory.MultiInventory;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.sound.SoundAPI;
import net.lastcraft.api.sound.SoundType;
import net.lastcraft.api.util.Head;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.island.IslandFlag;
import net.lastcraft.skyblock.api.island.member.IslandMember;
import net.lastcraft.skyblock.api.island.member.MemberType;
import net.lastcraft.skyblock.api.manager.IslandManager;
import net.lastcraft.skyblock.module.BorderModule;
import net.lastcraft.skyblock.module.FlagModule;
import net.lastcraft.skyblock.module.HomeModule;
import net.lastcraft.skyblock.module.IgnoreModule;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.stream.Collectors;

public class IslandsOpenedGui  {

    private static final InventoryAPI INVENTORY_API = LastCraft.getInventoryAPI();
    private static final IslandManager ISLAND_MANAGER = SkyBlockAPI.getIslandManager();
    private static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();
    private static final SoundAPI SOUND_API = LastCraft.getSoundAPI();
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    private final Language lang;
    private final MultiInventory inventory;

    public IslandsOpenedGui(Language lang) {
        this.lang = lang;
        this.inventory = INVENTORY_API.createMultiInventory(
                lang.getMessage("ISLAND_OPENED_GUI_NAME"), 5);
    }

    public void open(Player player) {
        inventory.openInventory(player);
    }

    public void update() {
        if (inventory == null)
            return;

        inventory.clearInventories();

        int slot = 10;
        int page = 0;
        for (Island island : ISLAND_MANAGER.getPlayerIsland().values()
                .stream()
                .sorted(Comparator.comparingInt(Island::getLevel).reversed())
                .collect(Collectors.toList())) {
            FlagModule flagModule = island.getModule(FlagModule.class);
            if (flagModule == null)
                continue;

            if (!flagModule.isFlag(IslandFlag.OPENED))
                continue;

            Calendar calendar = Calendar.getInstance();
            IslandMember ownerMember = island.getMembers(MemberType.OWNER).get(0);
            calendar.setTimeInMillis(ownerMember == null ? System.currentTimeMillis() : ownerMember.getDate().getTime());
            String date = SIMPLE_DATE_FORMAT.format(calendar.getTime());

            ItemStack head = Head.getHeadByValue(island.getOwner().getSkin().getValue());
            inventory.setItem(page, slot++, INVENTORY_API.createItem(ItemUtil.getBuilder(head)
                    .setName(island.getOwner().getDisplayName())
                    .setLore(lang.getList("ISLAND_OPENED_GUI_LORE",
                            date,
                            island.getBiome().name(),
                            StringUtil.getNumberFormat(island.getMoney()),
                            StringUtil.getNumberFormat(island.getLevel()),
                            island.getModule(BorderModule.class).getPercent() + "%",
                            StringUtil.getNumberFormat(island.getMembers().size()),
                            StringUtil.getNumberFormat(island.getLimitMembers()),
                            StringUtil.getNumberFormat(island.getOnlineGamers().size())
                            )).build(), (player, clickType, i) -> {
                if (!ISLAND_MANAGER.getPlayerIsland().containsKey(island.getOwner().getPlayerID()))
                    return;
                BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
                if (gamer == null)
                    return;
                HomeModule homeModule = island.getModule(HomeModule.class);
                if (homeModule == null)
                    return;

                IgnoreModule ignoreModule = island.getModule(IgnoreModule.class);
                if (ignoreModule == null)
                    return;

                if (ignoreModule.getIgnoreList().containsKey(gamer.getPlayerID())) {
                    SOUND_API.play(player, SoundType.NO);
                    gamer.sendMessageLocale("ISLAND_MEMBER_BLOCKED", island.getOwner().getDisplayName());
                    return;
                }

                if (!flagModule.isFlag(IslandFlag.OPENED)) {
                    SOUND_API.play(player, SoundType.NO);
                    gamer.sendMessageLocale("ISLAND_CLOSED");
                    return;
                }

                homeModule.teleport(player);
            }));

            if ((slot - 8) % 9 == 0)
                slot += 2;

            if (slot >= 35) {
                slot = 10;
                page++;
            }
        }

        INVENTORY_API.pageButton(lang, page + 1, inventory, 38, 42);
    }
}
