package net.lastcraft.skyblock.gui.guis;

import net.lastcraft.api.inventory.DItem;
import net.lastcraft.api.inventory.InventoryAction;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.sound.SoundType;
import net.lastcraft.api.util.Head;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.dartaapi.achievements.achievement.AchievementPlayer;
import net.lastcraft.skyblock.SkyBlock;
import net.lastcraft.skyblock.achievement.IslandAchievements;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.SkyBlockGui;
import net.lastcraft.skyblock.api.entity.SkyGamer;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.island.member.IslandMember;
import net.lastcraft.skyblock.api.island.member.MemberType;
import net.lastcraft.skyblock.api.manager.SkyGamerManager;
import net.lastcraft.skyblock.dependencies.clearlag.ClearLagg;
import net.lastcraft.skyblock.module.BorderModule;
import net.lastcraft.skyblock.module.GeneratorModule;
import net.lastcraft.skyblock.module.HomeModule;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ProfileGui extends SkyBlockGui {

    private static final SkyGamerManager SKY_GAMER_MANAGER = SkyBlockAPI.getSkyGamerManager();
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static final ClearLagg CLEAR_LAGG = JavaPlugin.getPlugin(SkyBlock.class)
            .getDependManager()
            .getClearLagg();

    public ProfileGui(Player player) {
        super(player);
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
    }

    @Override
    protected void setItems(Player player) {
        Island island = ISLAND_MANAGER.getIsland(player);
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        SkyGamer skyGamer = SKY_GAMER_MANAGER.getSkyGamer(player);
        if (skyGamer == null)
            return;

        AchievementPlayer achievementPlayer = skyGamer.getAchievementPlayer();
        if (achievementPlayer == null)
            return;

        inventory.clearInventory();

        inventory.setItem(44, API.createItem(ItemUtil.getBuilder(Material.DIAMOND)
                .setName("§e" + lang.getMessage("ISLAND_ACHIEVEMENT_NAME"))
                .setLore(lang.getList("ISLAND_ACHIEVEMENT_LORE",
                        achievementPlayer.getCompleted().size(),
                        IslandAchievements.values().length))
                .build(), (clicker, clickType, i) -> clicker.chat("/achievement")));

        //todo айтем для помощи

        if (island == null) {
            DItem itemStart = API.createItem(ItemUtil.getBuilder(Material.GRASS)
                            .setName(lang.getMessage("ISLAND_CREATE_NAME"))
                            .setLore(lang.getList( "ISLAND_CREATE_LORE"))
                            .build(), (clicker, clickType, i) -> {
                ChoisedGui choisedGui = SKY_GUI_MANAGER.getGui(ChoisedGui.class, player);
                if (choisedGui != null)
                    choisedGui.open();
            });
            inventory.setItem(9 * 2 + 4, itemStart);

            return;
        }

        MemberType memberType = island.getMemberType(gamer);
        BukkitGamer islandGamer = GAMER_MANAGER.getGamer(island.getOwner().getPlayerID());
        ItemStack head = (islandGamer != null ?
                islandGamer.getHead() :
                Head.getHeadByValue(island.getOwner().getSkin().getValue()));
        Calendar calendar = Calendar.getInstance();
        IslandMember ownerMember = island.getMembers(MemberType.OWNER).get(0);
        calendar.setTimeInMillis(ownerMember == null ? System.currentTimeMillis() : ownerMember.getDate().getTime());

        String date = SIMPLE_DATE_FORMAT.format(calendar.getTime());
        inventory.setItem(4, API.createItem(ItemUtil.getBuilder(head)
                .setName("§e" + lang.getMessage( "ISLAND_PROFILE_NAME"))
                .setLore(lang.getList( "ISLAND_PROFILE_LORE",
                        date,
                        island.getOwner().getDisplayName(),
                        lang.getMessage(memberType.getKey()),
                        island.getBiome().name(),
                        StringUtil.getNumberFormat(island.getMoney()),
                        StringUtil.getNumberFormat(island.getLevel()),
                        StringUtil.getNumberFormat(CLEAR_LAGG.getLimit(island.getOwner())),
                        island.getModule(BorderModule.class).getPercent() + "%",
                        String.valueOf(island.getMembers().size()),
                        String.valueOf(island.getLimitMembers())
                )).build()));

        inventory.setItem(2 * 9 + 1, API.createItem(ItemUtil.getBuilder(Material.BED)
                .setDurability((short) 12)
                .setName("§e" + lang.getMessage("ISLAND_PROFILE_HOME_NAME"))
                .setLore(lang.getList( "ISLAND_PROFILE_HOME_LORE"))
                .build(), (clicker, clickType, i) -> {
            HomeModule homeModule = island.getModule(HomeModule.class);
            if (homeModule != null)
                homeModule.teleport(clicker);
        }));

        inventory.setItem(2 * 9 + 2, API.createItem(ItemUtil
                .getBuilder(Material.ARMOR_STAND)
                .setName("§e" + lang.getMessage("ISLAND_PROFILE_FLAG_NAME"))
                .setLore(lang.getList("ISLAND_PROFILE_FLAG_LORE"))
                .build(), (clicker, clickType, i) -> {
            FlagsGui flagsGui = SKY_GUI_MANAGER.getGui(FlagsGui.class, player);
            if (flagsGui != null)
                flagsGui.open();
            SOUND_API.play(clicker, SoundType.CLICK);
        }));

        inventory.setItem(2 * 9 + 3, API.createItem(ItemUtil
                .getBuilder(Material.GRASS)
                .setName("§e" + lang.getMessage("ISLAND_PROFILE_BIOME_NAME"))
                .setLore(lang.getList("ISLAND_PROFILE_BIOME_LORE"))
                .setDurability((short) 2)
                .build(), (clicker, clickType, i) -> {
            BiomeGui biomeGui = SKY_GUI_MANAGER.getGui(BiomeGui.class, player);
            if (biomeGui != null)
                biomeGui.open();
            SOUND_API.play(clicker, SoundType.CLICK);
        }));

        inventory.setItem(2 * 9 + 4, API.createItem(ItemUtil
                .getBuilder(Material.SKULL_ITEM)
                .setDurability((short) 3)
                .setName("§e" + lang.getMessage("ISLAND_PROFILE_MEMBERS_NAME"))
                .setLore(lang.getList("ISLAND_PROFILE_MEMBERS_LORE"))
                .build(), (clicker, clickType, i) -> {
            MembersGui membersGui = SKY_GUI_MANAGER.getGui(MembersGui.class, player);
            if (membersGui != null)
                membersGui.open();
            SOUND_API.play(clicker, SoundType.CLICK);
        }));

        inventory.setItem(2 * 9 + 5, API.createItem(ItemUtil
                .getBuilder(Material.ANVIL)
                .setName("§e" + lang.getMessage("ISLAND_PROFILE_UPGRADE_NAME"))
                .setLore(lang.getList("ISLAND_PROFILE_UPGRADE_LORE"))
                .build(), (clicker, clickType, i) -> {
            UpgradeGui upgradeGui = SKY_GUI_MANAGER.getGui(UpgradeGui.class, player);
            if (upgradeGui != null)
                upgradeGui.open();
            SOUND_API.play(clicker, SoundType.CLICK);
        }));

        inventory.setItem(2 * 9 + 6, API.createItem(ItemUtil
                .getBuilder(Material.PAPER)
                .setName("§e" + lang.getMessage("ISLAND_PROFILE_IGNORE_LIST_NAME"))
                .setLore(lang.getList("ISLAND_PROFILE_IGNORE_LIST_LORE"))
                .build(), (clicker, clickType, i) -> {
            IgnoreGui ignoreGui = SKY_GUI_MANAGER.getGui(IgnoreGui.class, player);
            if (ignoreGui != null)
                ignoreGui.open();
            SOUND_API.play(clicker, SoundType.CLICK);
        }));

        GeneratorModule generatorModule = island.getModule(GeneratorModule.class);
        if (generatorModule == null)
            return;

        inventory.setItem(2 * 9 + 7, API.createItem(ItemUtil
                .getBuilder(Material.COBBLESTONE)
                .setName("§e" + lang.getMessage("ISLAND_GENERATOR_GUI_NAME"))
                .setLore(lang.getList( "ISLAND_GENERATOR_GUI_LORE",
                        generatorModule.getActiveGenerator().getName(lang)))
                .build(), (clicker, clickType, i) -> {
            GeneratorGui generatorGui = SKY_GUI_MANAGER.getGui(GeneratorGui.class, player);
            if (generatorGui != null)
                generatorGui.open();
            SOUND_API.play(clicker, SoundType.CLICK);
        }));
    }
}
