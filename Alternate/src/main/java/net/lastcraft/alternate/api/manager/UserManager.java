package net.lastcraft.alternate.api.manager;

import net.lastcraft.alternate.api.User;
import org.bukkit.entity.Player;

import java.util.Map;

public interface UserManager {

    /**
     * Получить юзера который хранит инфу
     * @param player - игрок bukkit'a
     * @param player - ник игрока
     * @return - объект юзера
     */
    User getUser(Player player);
    User getUser(String name);

    /**
     * получить список всех юзеров
     * в памяти сервера
     * @return - мапа с юзерами (ник - юзер)
     */
    Map<String, User> getUsers();

    /**
     * добавить юзера в память
     * @param user - юзер которого надо добавить
     */
    void addUser(User user);

    /**
     * Удалить юзера из памяти
     * @param user - юзер которого удалить
     * @param user - ник юзера, которого надо удалить
     */
    void removeUser(User user);
    void removeUser(String name);
}
