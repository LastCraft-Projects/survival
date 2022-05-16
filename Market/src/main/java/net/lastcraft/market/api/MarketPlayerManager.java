package net.lastcraft.market.api;

import net.lastcraft.base.gamer.IBaseGamer;
import org.bukkit.entity.Player;

import javax.validation.constraints.NotNull;
import java.util.Map;

public interface MarketPlayerManager {

    /**
     * получить MarketPlayer'а
     * @param playerID - ид игрока
     * @param name - имя игрока
     * @return - скайгеймер
     */
    MarketPlayer getMarketPlayer(int playerID);
    MarketPlayer getMarketPlayer(String name);
    MarketPlayer getMarketPlayer(Player player);

    MarketPlayer getOrCreate(String name);
    MarketPlayer getOrCreate(@NotNull IBaseGamer gamer);

    /**
     * добавить marketPlayer в память
     * @param marketPlayer - кого именно
     */
    void addMarketPlayer(MarketPlayer marketPlayer);

    /**
     * Удалить marketPlayer из памяти
     * @param marketPlayer - юзер которого удалить
     * @param name - ник юзера, которого надо удалить
     */
    void removeMarketPlayer(MarketPlayer marketPlayer);
    void removeMarketPlayer(String name);

    /**
     * получить всех маркетИгроков в онлайне
     * @return - список
     */
    Map<Integer, MarketPlayer> getMarketPlayers();

    /**
     * проверить, есть ли игрок такой
     * @param name - ник
     * @return - да или нет
     */
    boolean contains(String name);
}
