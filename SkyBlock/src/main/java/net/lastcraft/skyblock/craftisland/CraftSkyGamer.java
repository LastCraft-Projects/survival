package net.lastcraft.skyblock.craftisland;

import net.lastcraft.dartaapi.achievements.achievement.AchievementPlayer;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.entity.SkyGamer;
import net.lastcraft.skyblock.api.manager.SkyGamerManager;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CraftSkyGamer implements SkyGamer {
    private static final SkyGamerManager MANAGER = SkyBlockAPI.getSkyGamerManager();

    private final String name;
    private final AchievementPlayer achievementPlayer;

    private final Map<String, Long> requests = new ConcurrentHashMap<>();

    private boolean border;

    private boolean save = false; //сохранять или нет

    public CraftSkyGamer(String name, AchievementPlayer achievementPlayer, boolean border) {
        this.name = name;
        this.achievementPlayer = achievementPlayer;
        this.border = false; //todo
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AchievementPlayer getAchievementPlayer() {
        return achievementPlayer;
    }

    @Override
    public boolean isBorder() {
        return border;
    }

    @Override
    public void setBorder(boolean flag) {
        this.border = flag;
    }

    @Override
    public void remove() {
        MANAGER.removeSkyGamer(this);
    }

    public void save() {
        if (save) {
            //todo сохранить все в памяти (что надо)
        }
    }

    public Map<String, Long> getRequests() {
        return requests;
    }

    public boolean addRequest(Player player) {
        long time = System.currentTimeMillis();
        String name = player.getName();

        if (requests.containsKey(name) && requests.get(name) + 120 * 1000 > System.currentTimeMillis())
            return false;

        requests.put(name, time);
        return true;
    }

    @Override
    public String toString() {
        return "CraftSkyGamer{name = " + name + "}";
    }
}
