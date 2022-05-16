package net.lastcraft.skyblock.gui.guis;

import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.sound.SoundType;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.SkyBlockGui;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.island.IslandBiome;
import net.lastcraft.skyblock.api.island.member.MemberType;
import net.lastcraft.skyblock.gui.AcceptGui;
import net.lastcraft.skyblock.gui.GuiUtil;
import net.lastcraft.skyblock.module.BiomeModule;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BiomeGui extends SkyBlockGui {

    public BiomeGui(Player player) {
        super(player, "ISLAND_BIOME_GUI_NAME");
    }

    @Override
    protected void setItems(Player player) {
        Island island = ISLAND_MANAGER.getIsland(player);
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null || island == null)
            return;

        GuiUtil.setBack(inventory, lang);

        BiomeModule module = island.getModule(BiomeModule.class);
        if (module == null)
            return;

        int slot = 10;
        for (IslandBiome islandBiome : IslandBiome.values()) {
            boolean can = module.getBiomes().contains(islandBiome) || islandBiome.getPrice() == 0;
            String biomeName = lang.getMessage("ISLAND_BIOME_" + islandBiome.name() + "_NAME");
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.addAll(lang.getList("ISLAND_BIOME_" + islandBiome.name() + "_LORE"));
            lore.add("");
            if (can)
                lore.add(lang.getMessage("ISLAND_CLICK_TO_SET_BIOME"));
            else
                lore.addAll(lang.getList( "ISLAND_CLICK_TO_SET_BIOME_ERROR",
                        StringUtil.getNumberFormat(islandBiome.getPrice())));
            ItemStack item = ItemUtil.getBuilder((can ? islandBiome.getMaterial() : Material.STAINED_GLASS_PANE))
                    .setDurability((short) (can ? 0 : 14))
                    .setName((can ? "§a" : "§c") + biomeName)
                    .setLore(lore)
                    .build();

            Biome biome = islandBiome.getBiome();

            inventory.setItem(slot, API.createItem(item, (clicker, clickType, i) -> {
                if (island.getMemberType(gamer) == MemberType.MEMBER) {
                    gamer.sendMessage(SkyBlockAPI.getPrefix()
                            + lang.getMessage("ISLAND_YOU_NOT_OWNER"));
                    SOUND_API.play(clicker, SoundType.DESTROY);
                    return;
                }

                if (clickType.isLeftClick()) {
                    if (can) {
                        if (island.getBiome() == biome) {
                            SOUND_API.play(clicker, SoundType.NO);
                            gamer.sendMessageLocale("ISLAND_BIOME_SET_ERROR");
                            return;
                        }
                        island.setBiome(biome);
                        gamer.sendMessageLocale("ISLAND_BIOME_SET", biomeName);
                        SOUND_API.play(clicker, SoundType.CLICK);
                        clicker.closeInventory();
                        return;
                    }
                    gamer.sendMessageLocale("ISLAND_BIOME_NOT_OPEN");
                    return;
                }

                if (module.getBiomes().contains(islandBiome)) {
                    gamer.sendMessageLocale("ISLAND_BIOME_BUY_ALREADY");
                    return;
                }
                AcceptGui acceptGui = new AcceptGui(player, AcceptGui.Type.BUY_BIOME);
                acceptGui.open(() -> {
                    if (!island.changeMoney(-islandBiome.getPrice())) {
                        gamer.sendMessageLocale("ISLAND_UPGRADE_BUY_ERROR");
                        SOUND_API.play(player, SoundType.NO);
                        return;
                    }
                    gamer.sendMessageLocale("ISLAND_BIOME_BUY", biomeName);
                    island.getOnlineMembers().forEach(member -> {
                        BukkitGamer bukkitGamer = GAMER_MANAGER.getGamer(member);
                        if (bukkitGamer != null && gamer != bukkitGamer) {
                            Language lang = bukkitGamer.getLanguage();
                            String nameBiome = lang.getMessage("ISLAND_BIOME_"
                                    + islandBiome.name() + "_NAME");
                            bukkitGamer.sendMessageLocale("ISLAND_BIOME_BUY_TO",
                                    gamer.getChatName(),
                                    nameBiome,
                                    StringUtil.getNumberFormat(islandBiome.getPrice()),
                                    StringUtil.getCorrectWord(islandBiome.getPrice(), "MONEY_1", lang));
                        }
                    });
                    clicker.closeInventory();
                    module.buyBiome(islandBiome);
                }, () -> {
                    BiomeGui biomeGui = SKY_GUI_MANAGER.getGui(BiomeGui.class, clicker);
                    if (biomeGui != null)
                        biomeGui.open();
                });
            }));

            slot++;

            if ((slot - 1) % 8 == 0)
                slot += 2;
        }
    }
}
