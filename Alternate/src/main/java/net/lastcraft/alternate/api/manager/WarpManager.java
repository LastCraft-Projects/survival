package net.lastcraft.alternate.api.manager;

import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.Warp;

import java.util.List;
import java.util.Map;

public interface WarpManager {

    /**
     * сохранить варп в память сервера и баху
     * @param warp - варп
     */
    void addWarp(Warp warp);

    /**
     * получить варп по имени
     * @param name - имя варпа
     * @return - варп
     */
    Warp getWarp(String name);

    /**
     * получить варпы определенного игрока
     * @return - варпы
     */
    List<Warp> getWarps(User user);
    List<Warp> getWarps(String name);
    List<Warp> getWarps(int playerID);

    Map<String, Warp> getWarps();

    void removeWarp(Warp warp);
    void removeWarp(String name);

    /**
     * всего варпов
     * @return - число
     */
    int size();
}
