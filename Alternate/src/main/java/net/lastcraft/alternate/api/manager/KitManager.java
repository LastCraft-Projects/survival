package net.lastcraft.alternate.api.manager;

import net.lastcraft.alternate.api.Kit;

import java.util.List;
import java.util.Map;

public interface KitManager {

    /**
     * Получить набор по нику(если его нет, будет null)
     * @param name - ник игрока
     * @return - объект набора
     */
    Kit getKit(String name);

    /**
     * получить список всех юзеров
     * в памяти сервера
     * @return - мапа с юзерами (ник - юзер)
     */
    Map<String, Kit> getKits();

    /**
     * Получить наборы, которые должны выдаваться
     * новичкам при первом заходе на сервер
     * @return - наборы
     */
    List<Kit> getStarterKit();

    /**
     * добавить набор в память
     * @param kit - набор который надо добавить
     */
    void addKit(Kit kit);

    /**
     * Удалить набор из памяти
     * @param kit - набор который удалить
     */
    void removeKit(Kit kit);
}
