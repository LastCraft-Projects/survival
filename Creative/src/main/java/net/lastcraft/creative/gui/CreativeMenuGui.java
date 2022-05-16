package net.lastcraft.creative.gui;

import net.lastcraft.api.util.Head;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.locale.Language;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.Arrays;

public class CreativeMenuGui extends CreativeAbstractGui {

    public CreativeMenuGui(Language lang) {
        super("Креатив меню", 5, lang); //todo локализация
    }

    @Override
    protected void setItems() { //todo локализцация
        inventory.setItem(3, 2, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.DIRT)
                .setDurability((short) 1)
                .setName("§bСоздать участок в мире 55x55")
                .setLore(Arrays.asList(
                        "§7Доступен обычным игрокам",
                        "§7Чтобы попасть в этот мир",
                        "§7Команда: §f/world plot55",
                        " ",
                        "§e▶ Нажмите, чтобы выбрать!"
                ))
                .build(), (player, clickType, i) -> {
                    player.chat("/world plot55");
                    player.chat("/plot auto");
                }));

        inventory.setItem(5, 2, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.DIRT)
                .setDurability((short) 2)
                .setName("§bСоздать участок в мире 95x95")
                .setLore(Arrays.asList(
                        "§7Доступен от §b§lDIAMOND§7 и выше",
                        "§7Чтобы попасть в этот мир",
                        "§7Команда: §f/world plot95",
                        " ",
                        "§e▶ Нажмите, чтобы выбрать!"
                ))
                .build(), (player, clickType, i) -> {
            player.chat("/world plot95");
            player.chat("/plot auto");
        }));

        inventory.setItem(7, 2, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.GRASS)
                .setName("§bСоздать участок в мире 200x200")
                .setLore(Arrays.asList(
                        "§7Доступен от §c§lMAGMA§7 и выше",
                        "§7Чтобы попасть в этот мир",
                        "§7Команда: §f/world plot200",
                        " ",
                        "§e▶ Нажмите, чтобы выбрать!"
                ))
                .build(), (player, clickType, i) -> {
            player.chat("/world plot95");
            player.chat("/plot auto");
        }));

        inventory.setItem(2, 3, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.BOOK)
                .setName("§bВарпы, запросы на тп, смена погоды и времени")
                .setLore(Arrays.asList(
                        "§7В этом меню вы сможете посмотреть список",
                        "§7доступных варпов, установить себе время и",
                        "§7поменять погоду на нужную!",
                        " ",
                        "§e▶ Нажмите, чтобы открыть!"
                ))
                .build(), (player, clickType, i) -> player.chat("/menu")));

        inventory.setItem(3, 3, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.DOUBLE_PLANT)
                .setName("§bМеню плагина Builders Utilities")
                .setLore(Arrays.asList(
                        "§7Многие полезные функции для строительства",
                        "§7Доступно для всех игроков",
                        "§7Команда: §f/bu",
                        " ",
                        "§e▶ Нажмите, чтобы открыть!"
                ))
                .build(), (player, clickType, i) -> player.chat("/bu")));

        inventory.setItem(4, 3, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.PRISMARINE)
                .setName("§bСекретные блоки")
                .setLore(Arrays.asList(
                        "§7В это меню вы сможете найти",
                        "§7специальные блоки, которые не получить",
                        "§7из обычного креатив меню",
                        "§7Команда: §f/blocks",
                        " ",
                        "§e▶ Нажмите, чтобы открыть!"
                ))
                .build(), (player, clickType, i) -> player.chat("/blocks")));

        inventory.setItem(5, 3, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.BANNER)
                .setName("§bБаннеры")
                .setLore(Arrays.asList(
                        "§7Создавайте свои уникальные банеры",
                        "§7Команда: §f/banner",
                        " ",
                        "§e▶ Нажмите, чтобы открыть"
                ))
                .build(), (player, clickType, i) -> player.chat("/banner")));

        inventory.setItem(6, 3, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.LEATHER_CHESTPLATE)
                .setColor(Color.SILVER)
                .setName("§bЦветная броня")
                .setLore(Arrays.asList(
                        "§7Быстрый способ подобрать и покрасить",
                        "§7броню в нужный вам цвет",
                        "§7Команда: §f/color",
                        " ",
                        "§e▶ Нажмите, чтобы открыть!"
                ))
                .removeFlags()
                .build(), (player, clickType, i) -> player.chat("/color")));

        inventory.setItem(7, 3, INVENTORY_API.createItem(ItemUtil.getBuilder(Head.CREATIVE)
                .setName("§bГоловы для декораций")
                .setLore(Arrays.asList(
                        "§7Более 17-ти тысяч различных голов для строительства",
                        "§7Команда: §f/heads",
                        " ",
                        "§e▶ Нажмите, чтобы открыть!"
                ))
                .build(), (player, clickType, i) -> player.chat("/heads")));

        inventory.setItem(8, 3, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.ARMOR_STAND)
                .setName("§bАрмор стенды")
                .setLore(Arrays.asList(
                        "§7Огромный функционал по созданию",
                        "§7различных фигур и обьектов из стендов",
                        "§7Команда: §f/ast §7от §e§lGOLD §7и выше",
                        " ",
                        "§e▶ Нажмите, чтобы перейти в режим конструктора!"
                ))
                .build(), (player, clickType, i) -> player.chat("/ast")));

        inventory.setItem(4, 5, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.BOOK)
                .setName("§bПлоты и права плагинов")
                .setLore(Arrays.asList(
                        "§7Обычный игрок может иметь §f3§7 плота в мире",
                        "§aplot55§7, а начиная от §b§lDIAMOND",
                        "§7доступен весь функционал §eWorldEdit!",
                        "§7А для самых активных строителей на сервере",
                        "§7установлены §eGoBrush§7, §eGoPaint §7и",
                        "§eVoxelSniper §7доступные от §a§lEMERALD"
                ))
                .build()));

        inventory.setItem(6, 5, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.SIGN)
                .setName("§bОбщая информация об игре")
                .setLore(Arrays.asList(
                        " §8▪ §7Чтобы занять нужный - §f/plot auto",
                        " §8▪ §7Чтобы занять участок на котором вы стоите - §f/plot claim",
                        " §8▪ §7Телепортироваться на участок - §f/plot home <номер>",
                        " §8▪ §7Телепортироваться в центр участка - §f/plot middle",
                        " §8▪ §7Посмотреть информацию о участке - §f/plot info",
                        " §8▪ §7Очистить участок на котором вы находитесь - §f/plot clear",
                        " §8▪ §7Удалить свой участок без возможности возврата - §f/plot delete",
                        " §8▪ §7Установить варп - §f/setwarp <название>",
                        " §8▪ §7Удалить варп - §f/delwarp <название>",
                        " §8▪ §7Разрешить игроку строить на вашем учатске - §f/plot trust",
                        " §8▪ §7Удалить игрока с участка - §f/plot remove <ник>",
                        " §8▪ §7Кикнуть игрока с участка - §f/plot kick <ник>",
                        " §8▪ §7Установить скорость бега/полета - §f/speed <номер>"
                )).build()));
    }
}
