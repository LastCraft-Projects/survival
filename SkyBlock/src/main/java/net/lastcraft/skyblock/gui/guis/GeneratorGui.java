package net.lastcraft.skyblock.gui.guis;

import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.sound.SoundType;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.skyblock.api.SkyBlockGui;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.island.member.MemberType;
import net.lastcraft.skyblock.generator.Generators;
import net.lastcraft.skyblock.gui.AcceptGui;
import net.lastcraft.skyblock.gui.GuiUtil;
import net.lastcraft.skyblock.module.GeneratorModule;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GeneratorGui extends SkyBlockGui {

    private static final ItemStack RED_PANE = ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
            .setDurability((short) 14)
            .build();

    public GeneratorGui(Player player) {
        super(player, "ISLAND_GENERATOR_GUI_NAME");
    }

    @Override
    protected void setItems(Player player) {
        Island island = ISLAND_MANAGER.getIsland(player);
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null || island == null)
            return;

        GeneratorModule module = island.getModule(GeneratorModule.class);
        if (module == null)
            return;

        GuiUtil.setBack(inventory, lang);

        MemberType memberType = island.getMemberType(gamer.getPlayerID());

        int slot = 10;
        for (Generators generator : Generators.values()) {
            boolean can = module.getGenerators().containsKey(generator.getID());
            boolean enable = can && module.getActiveGenerator() == generator;
            String color = (enable ? "§a" : (can ? "§e" : "§c"));

            inventory.setItem(slot, API.createItem(ItemUtil
                    .getBuilder((can ? generator.getItemStack() : RED_PANE.clone()))
                    .setName(color + generator.getName(lang))
                    .setLore(generator.getLore(lang))
                    .addLore("")
                    .addLore(getDependLore(generator, enable, can, lang))
                    .glowing(enable)
                    .build(), (clicker, clickType, i) -> {
                if (memberType == MemberType.MEMBER || memberType == MemberType.NOBODY) {
                    gamer.sendMessageLocale("ISLAND_NOT_OWNER");
                    clicker.closeInventory();
                    return;
                }

                if (!can) {
                    if (island.getMoney() < generator.getPrice()) {
                        gamer.sendMessageLocale("ISLAND_UPGRADE_BUY_ERROR");
                        SOUND_API.play(player, SoundType.NO);
                        return;
                    }

                    if (module.getGenerators().containsKey(generator.getID()))
                        return;

                    AcceptGui acceptGui = new AcceptGui(clicker, AcceptGui.Type.BUY_UPGRADE);
                    acceptGui.open(() -> {
                        if (!island.changeMoney(-generator.getPrice())) {
                            gamer.sendMessageLocale("ISLAND_UPGRADE_BUY_ERROR");
                            SOUND_API.play(player, SoundType.NO);
                            player.closeInventory();
                            return;
                        }

                        module.buyGenerator(generator);
                        SOUND_API.play(clicker, SoundType.CLICK);
                        island.getOnlineMembers().forEach(member -> {
                            BukkitGamer memberGamer = GAMER_MANAGER.getGamer(member);
                            if (memberGamer != null) {
                                Language lang = memberGamer.getLanguage();
                                memberGamer.sendMessageLocale("ISLAND_BUY_GENERATOR",
                                        gamer.getChatName(),
                                        generator.getName(lang),
                                        StringUtil.getNumberFormat(generator.getPrice()),
                                        StringUtil.getCorrectWord(generator.getPrice(), "MONEY_1", lang)
                                );
                            }
                        });
                        clicker.closeInventory();
                    }, () -> {
                        GeneratorGui generatorGui = SKY_GUI_MANAGER.getGui(GeneratorGui.class, clicker);
                        if (generatorGui != null)
                            generatorGui.open();
                    });
                } else if (!enable) {

                    island.getOnlineMembers().forEach(member -> {
                        BukkitGamer memberGamer = GAMER_MANAGER.getGamer(member);
                        if (memberGamer != null) {
                            Language lang = memberGamer.getLanguage();
                            memberGamer.sendMessageLocale("ISLAND_CHANGE_GENERATOR",
                                    gamer.getChatName(),
                                    generator.getName(lang)
                            );
                        }
                    });

                    SOUND_API.play(player, SoundType.DESTROY);
                    module.setDefaultGenerator(generator);
                }

            }));

            slot++;
        }
    }

    private static List<String> getDependLore(Generators generator, boolean enable, boolean can, Language lang) {
        List<String> lore = new ArrayList<>();
        if (enable) {
            lore.add(lang.getMessage("ISLAND_GENERATOR_LORE_ENABLED"));
        } else if (can){
            lore.add(lang.getMessage("ISLAND_GENERATOR_LORE_ENABLE"));
        } else {
            lore.addAll(lang.getList( "ISLAND_GENERATOR_LORE_PRICE",
                    StringUtil.getNumberFormat(generator.getPrice())));
        }
        return lore;
    }
}
