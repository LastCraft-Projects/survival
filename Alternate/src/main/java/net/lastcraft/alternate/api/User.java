package net.lastcraft.alternate.api;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.Map;

public interface User {
    /**
     * Получить ник юзера
     * @return - юзер
     */
    String getName();

    /**
     * получить игрока Bukkit'а
     * @return - игрок
     */
    Player getPlayer();

    /**
     * Получить список китов и их КД
     * @return - список китов
     */
    Map<Kit, Timestamp> getKits();

    /**
     * добавить кит в память
     * @param kit - какой набор
     */
    void addKit(Kit kit);
    /**
     * Узнать, есть ли кулдаун у игрока на набор
     * @param kit - набор
     * @return - true/false если есть кулдаун
     */
    boolean isCooldown(Kit kit);

    /**
     * Узнать, есть ли кулдаун и если получить время
     * если нету, то время 0
     * @param kit - набор
     * @return - время кулдауна
     */
    int getCooldown(Kit kit);

    /**
     * Получить дома игрока
     * @return - список домов (название - локация)
     */
    Map<String, Home> getHomes();

    /**
     * Создать хоум игроку
     * @param name - имя игрока
     * @param home - дом игрока(локацмя)
     */
    void addHome(String name, Location home);

    /**
     * Удалить точку дома
     * @param name - имя дома
     */
    void removeHome(String name);
    /**
     * Получить последнюю локацию на которой
     * был игрок до телепортации
     * @return - место, где он вышел
     */
    Location getLastLocation();
    /**
     * Проверить, первый ли вход (для наборов надо)
     * @return - true/false
     */
    boolean isFirstJoin();
    /**
     * Проверить, онлайн ли игрок
     * @return - true/false
     */
    boolean isOnline();
    /**
     * Проверить, включен ли полет
     * @return - true/false
     */
    boolean isFly();
    /**
     * Проверить, включен ли режим бога
     * @return - true/false
     */
    boolean isGod();

    /**
     * можно ле делать запросы на телепорацию к игроку
     * @return - true - можно/false
     */
    boolean isTpToggle();

    /**
     * установить режим бога
     * @param god - true/false
     */
    void setGod(boolean god, boolean message);
    /**
     * установить режим полета
     * @param fly - true/false
     */
    void setFly(boolean fly, boolean message);

    /**
     * Включить или отключить запросы на телепортацию к игроку
     * @param tpToggle - флаг вкл/выкл
     */
    void setTpToggle(boolean tpToggle);

    /**
     * Назначинить последнюю локацию
     * @param location - последняя локация
     */
    void setLastLocation(Location location);

    /**
     * Установить геймод
     * @param gamemode - бакитовский геймод
     */
    void setGamemode(GameMode gamemode);

    /**
     * получить точку респавна игрока, а именно его кровати, где он спал
     * @return - локация точки спанья так сказать
     */
    Location getBedLocation();

    /**
     * задать точку локации, где поспал игрок
     * @param location - где поспал игрок
     */
    void setBedLocation(Location location);

    /**
     * телепортировать(если телепортировать иначе, то будет фигово для команды /back, не не запомнит)
     * @param location - куда телепортируем
     * @return - произошла ли телепортация или нет
     */
    boolean teleport(Location location);

    /**
     * вызвать этот метод и проверить, в афк ли игрок или нет
     */
    boolean checkAfk();
    void updateAfkPosition();

    /**
     * Удалить юзера из памяти
     */
    void remove();
}
