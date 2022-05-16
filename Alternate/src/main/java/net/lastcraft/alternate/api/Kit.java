package net.lastcraft.alternate.api;

import net.lastcraft.base.gamer.constans.Group;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public interface Kit {
    /**
     * название кита
     * @return - название
     */
    String getName();

    /**
     * Получить список айтемов кита
     * @return - получить айтемы
     */
    List<ItemStack> getItems();

    /**
     * Получить иконку для гуи
     * @return - иконка
     */
    ItemStack getIcon();

    /**
     * Выдавать при первом входе или нет
     * по умолчанию false и он будет виден в гуи наборов
     * если true, он будет скрыт в гуи
     * @return - true/false
     */
    boolean isStart();

    /**
     * Кулдаун на набор
     * @return - получить кулдаун в сек
     */
    int getCooldown();

    /**
     * Добавить айтемы в набор
     * @param itemStacks - айтемы
     */
    void addItems(Collection<ItemStack> itemStacks);

    /**
     * Установить кулдаун на набор
     * @param time - время в сек
     */
    void setCooldown(int time);

    /**
     * Установить параметр того, что набор будет выдан игроку при первом входе
     */
    void setStart(boolean start);

    /**
     * Получить группу с которой можно юзать набор
     * @return - группа
     */
    Group getMinimalGroup();

    /**
     * вернуть группу, которой доступен этот набор
     * если не null, то доступен только для этого набоа
     * @return - группа
     */
    Group getDefaultGroup();

    /**
     * Удалить набор из памяти и отовсюду
     */
    void remove();
}
