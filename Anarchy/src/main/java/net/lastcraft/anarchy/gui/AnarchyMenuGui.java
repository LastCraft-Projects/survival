package net.lastcraft.anarchy.gui;

import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.locale.Language;
import org.bukkit.Material;

import java.util.Arrays;

public class AnarchyMenuGui extends AnarchyAbstractGui {

    public AnarchyMenuGui(Language lang) { //todo локализация
        super("Меню анархии", 5, lang);
    }

    @Override
    protected void setItems() { //todo локализцация
        inventory.setItem(3, 2, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.COMPASS)
                .setName("§bТелепорт в случайную точку на карте")
                .setLore(Arrays.asList(
                        "§7Если вы готовы начать игру, то сперва",
                        "§7вам нужно попасть в обычный мир",
                        "§7Команда: §f/rtp",
                        " ",
                        "§e▶ Нажмите, чтобы телепортироваться!"
                )).build(), (player, clickType, i) -> player.chat("/rtp")));

        inventory.setItem(5, 2, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.BOOK)
                .setName("§bСтартовый набор вещей, смена погоды и времени")
                .setLore(Arrays.asList(
                        "§7В этом меню вы сможете взять стартовый",
                        "§7набор вещей, установить себе время и",
                        "§7поменять погоду на нужную! От §e§lGOLD §7и выше!",
                        " ",
                        "§e▶ Нажмите, чтобы открыть!"
                )).build(), (player, clickType, i) -> player.chat("/menu")));

        inventory.setItem(7, 2, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.CHEST)
                .setName("§bТорговля на аукционе")
                .setLore(Arrays.asList(
                        "§7Вы можете продавать или покупать нужные",
                        "§7вам вещи и предметы у других игроков",
                        "§7Команда: §f/ah",
                        " ",
                        "§e▶ Нажмите, чтобы открыть!"
                )).build(), (player, clickType, i) -> player.chat("/ah")));

        inventory.setItem(3, 4, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.EYE_OF_ENDER)
                .setName("§bНа анархии тоже есть правила, а вы как думали?")
                .setLore(Arrays.asList(
                        "§f1. §7Соблюдать общие правила поведения в чате",
                        "§f2. §7Не использовать читы или любой запрещенный софт",
                        "§f3. §7Не багоюзить, не создавать лаг машины и",
                        "§7не совершать действия, которые могут навредить серверу!",
                        " ",
                        "§eНа этом все, больше вы ничем не ограничены",
                        "§eДелайте что хотите и как захотите!"
                )).build()));

        inventory.setItem(4, 4, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.EMERALD_ORE)
                .setName("§bКак заприватить и добавить друга в регион?")
                .setLore(Arrays.asList(
                        "§7Чтобы заприватить нужную вам территорию",
                        "§7нужно поставить один из специальных блоков,",
                        "§7найти их вы сможете на спавне, только помните, что",
                        "§7удаление этого блока удалит и ваш приват тоже!",
                        " ",
                        "§7Добавить друга в регион §a/ps addmember <ник игрока>",
                        "§7Удалить друга из региона §c/ps remove <ник игрока>"
                )).build()));

        inventory.setItem(5, 4, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.BED)
                .setName("§bИнфомация о телепортах и точках дома")
                .setLore(Arrays.asList(
                        "§7На данном режиме есть только один способ",
                        "§7сохранить свою точку появления - кровать!",
                        "§7Все остальные команды, запросы и варпы отключены, а",
                        "§7при сносе кровати, вы потеряете свою точку возрождения!"
                )).build()));

        inventory.setItem(6, 4, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.DOUBLE_PLANT)
                .setName("§bЗаработок валюты")
                .setLore(Arrays.asList(
                        "§7Единственный существующий способ заработка, это",
                        "§7убийство мирных и злых мобов, поэтому постарайтесь",
                        "§7с умом разводить животных и находить любые способы",
                        "§7фарма враждебно настроенных к вам монстров!",
                        " ",
                        "§aМножители получения денег у донатеров:",
                        "§8▪ §e§lGOLD §7- §f1.1x",
                        "§8▪ §b§lDIAMOND §7- §f1.2x",
                        "§8▪ §a§lEMERALD §7- §f1.3x",
                        "§8▪ §c§lMAGMA §7- §f1.4x",
                        "§8▪ §d§lSHULKER §7и выше - §f1.5x"
                )).build()));

        inventory.setItem(7, 4, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.BOOK_AND_QUILL)
                .removeFlags()
                .setName("§bОбщая информация о режиме")
                .setLore(Arrays.asList(
                        "§8▪ §7На режимы установлен уровень сложности - Hard",
                        "§7поэтому, здоровье и урон всех враждебных мобов увеличен",
                        "§8▪ §7Включены любые взрывы и распространение огня, в том",
                        "§7числе и иссушитель, который может уничтожить любые",
                        "§7блоки, даже если находятся в чужом регионе..",
                        "§8▪ §7Каждые 12 часов в енд-мире воскрешается дракон!"
                )).build()));

        inventory.setItem(5, 5, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.DIAMOND)
                .setName("§bБонусы для донатеров")
                .setLore(Arrays.asList(
                        "§8▪ §7Донатеры имеют большее кол-во приватов",
                        "§8▪ §7Спец. множитель на денеги за убийства мобов",
                        "§8▪ §7Не теряют определенный процент от вещей и опыта",
                        "  §e§lGOLD §7- §f20%",
                        "  §b§lDIAMOND §7- §f25%",
                        "  §a§lEMERALD §7- §f30%",
                        "  §c§lMAGMA §7- §f35%",
                        "  §d§lSHULKER §7и выше - §f35%",
                        "",
                        "§7Полное описание возможностей §f/donate"
                )).build()));
    }
}
