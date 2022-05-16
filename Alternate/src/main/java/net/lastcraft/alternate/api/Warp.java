package net.lastcraft.alternate.api;

import net.lastcraft.base.gamer.IBaseGamer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Date;

public interface Warp {

    /**
     * название варпа
     * @return - имя варпа
     */
    String getName();

    /**
     * DisplayName игрока того, кто создал варп
     * @return - кто владелец варпа(ид игрока)
     */
    String getNameOwner();

    /**
     * айди игрока того, кто создал варп
     * @return - кто владелец варпа(ид игрока)
     */
    IBaseGamer getOwner();
    int getOwnerID();

    /**
     * Получить список тех, кто рядом
     * @return - получить игроков рядом
     */
    Collection<Player> getNearbyPlayers(int size);

    /**
     * иконка для гуи(голова овнера)
     * @return - голова
     */
    ItemStack getIcon();

    /**
     * Координаты варпа
     * @return - локация
     */
    Location getLocation();
    World getWorld();

    /**
     * тп на варп могут ток админы и владельцы
     * или же друзья этого чела
     * @return - привытный или нет варп
     */
    boolean isPrivate();

    /**
     * сделать варп приватным или нет
     * @param flag - true/false
     */
    void setPrivate(boolean flag);

    /**
     * телепортироать на варп игрока
     * @param player - игрок
     */
    void teleport(Player player);

    /**
     * когда ео создали
     * @return - дата
     */
    Date getDate();

    /**
     * удалить варп
     */
    void remove();

    /**
     * добавить его сразу в память и БД
     * @return - получить назад этот варп
     */
    Warp save();
}
