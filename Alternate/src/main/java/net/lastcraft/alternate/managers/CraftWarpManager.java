package net.lastcraft.alternate.managers;

import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.Warp;
import net.lastcraft.alternate.api.manager.UserManager;
import net.lastcraft.alternate.api.manager.WarpManager;
import net.lastcraft.alternate.config.AlternateSql;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.base.sql.GlobalLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CraftWarpManager implements WarpManager {

    private final GamerManager gamerManager = LastCraft.getGamerManager();
    private final UserManager userManager = AlternateAPI.getUserManager();
    private final Map<String, Warp> warps = new ConcurrentHashMap<>();

    @Override
    public void addWarp(Warp warp) {
        String name = warp.getName();
        warps.put(name.toLowerCase(), warp);
    }

    public void addToDataBase(Warp warp) {
        addWarp(warp);
        AlternateSql.addWarp(warp.getName(), warp.getOwnerID(), warp.getLocation());
    }

    @Override
    public Warp getWarp(String name) {
        return warps.get(name.toLowerCase());
    }

    @Override
    public List<Warp> getWarps(User user) {
        String name = user.getName();
        BukkitGamer gamer = gamerManager.getGamer(name);

        int playerID = gamer == null ? GlobalLoader.containsPlayerID(name) : gamer.getPlayerID();

        return getWarps(playerID);
    }

    @Override
    public List<Warp> getWarps(String name) {
        User user = userManager.getUser(name);
        return getWarps(user);
    }

    @Override
    public List<Warp> getWarps(int playerID) {
        List<Warp> warps = new ArrayList<>();

        this.warps.values().forEach(warp -> {
            if (warp.getOwnerID() == playerID) {
                warps.add(warp);
            }
        });

        return warps;
    }

    @Override
    public Map<String, Warp> getWarps() {
        return new HashMap<>(warps);
    }

    @Override
    public void removeWarp(Warp warp) {
        if (warp == null) {
            return;
        }

        removeWarp(warp.getName());
    }

    @Override
    public void removeWarp(String name) {
        AlternateSql.removeWarp(name);
        warps.remove(name.toLowerCase());
    }

    @Override
    public int size() {
        return warps.size();
    }
}
