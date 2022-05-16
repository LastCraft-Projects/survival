package net.lastcraft.skyblock.api.island.member;

import net.lastcraft.api.depend.BaseUser;
import net.lastcraft.base.gamer.IBaseGamer;
import net.lastcraft.skyblock.api.island.Island;

import java.util.Date;

public interface IgnoredPlayer extends BaseUser {

    /**
     * на каком острове в ЧС
     * @return - остров
     */
    Island getIsland();

    /**
     * когда он был добавлен в список игнорируемых
     * @return - дата
     */
    Date getDate();

    /**
     * онлайн или нет
     * @return - узнать онлайн этот мембер или нет
     */
    boolean isOnline();

    /**
     * тот кто добавил в список игнорируемых
     * @return - кто добавил
     */
    IBaseGamer getBlockedPlayer();

    /**
     * удалить из списка игнорируемых
     */
    void removeFromIgnoreList();
}
