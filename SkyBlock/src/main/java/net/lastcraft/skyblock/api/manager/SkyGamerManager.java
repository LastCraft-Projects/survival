package net.lastcraft.skyblock.api.manager;

import net.lastcraft.skyblock.api.entity.SkyGamer;
import org.bukkit.entity.Player;

import java.util.Map;

public interface SkyGamerManager {

    /**
     * получить SkyGamer'а
     * @param playerID - ид игрока
     * @param name - имя игрока
     * @return - скайгеймер
     */
    SkyGamer getSkyGamer(int playerID);
    SkyGamer getSkyGamer(String name);
    SkyGamer getSkyGamer(Player player);

    /**
     * есть ли в памяти скайгеймер для этого игрока
     * @param name - ник
     * @return - да или нет
     */
    boolean contains(String name);

    /**
     * добавить скайгеймера в память
     * @param skyGamer - кого именно
     */
    void addSkyGamer(SkyGamer skyGamer);

    /**
     * Удалить скайгеймера из памяти
     * @param skyGamer - юзер которого удалить
     * @param name - ник юзера, которого надо удалить
     */
    void removeSkyGamer(SkyGamer skyGamer);
    void removeSkyGamer(String name);

    /**
     * получить всех скайгеймеров в онлайне
     * @return - список скайгеймеров
     */
    Map<String, SkyGamer> getSkyGamers();
}
