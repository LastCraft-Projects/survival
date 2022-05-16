package net.lastcraft.skyblock.dependencies.clearlag;

import net.lastcraft.api.event.AsyncGamerQuitEvent;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.gamer.IBaseGamer;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.skyblock.SkyBlock;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.entity.IslandEntity;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.manager.EntityManager;
import net.lastcraft.skyblock.api.manager.IslandManager;
import net.lastcraft.skyblock.dependencies.DependManager;
import net.lastcraft.skyblock.dependencies.SkyBlockDepend;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClearLagg extends SkyBlockDepend implements Listener {

    private final IslandManager islandManager = SkyBlockAPI.getIslandManager();
    private final Map<Island, Integer> mobs = new ConcurrentHashMap<>();
    private final EntityManager entityManager = SkyBlockAPI.getEntityManager();

    private final Map<Group, Integer> limit = new HashMap<>();

    private int defaultLimit;

    public ClearLagg(DependManager manager) {
        super(manager);
    }

    @Override
    protected void init() {
        Bukkit.getPluginManager().registerEvents(this, manager.getSkyBlock());
        new MobLimitCommand(this);
    }

    public int getLimit(IBaseGamer gamer) {
        if (gamer == null || gamer.getGroup() == Group.DEFAULT)
            return defaultLimit;

        return this.limit.computeIfAbsent(gamer.getGroup(), group -> defaultLimit);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSpawnEntity(EntitySpawnEvent e) {
        Entity entity = e.getEntity();
        if (entity instanceof Player
                || entity instanceof Monster
                || e.getEntityType() == EntityType.DROPPED_ITEM
                || entity instanceof Painting) {
            return;
        }

        Island island = islandManager.getIsland(entity);
        if (island == null) {
            e.setCancelled(true);
            return;
        }

        Integer amount = mobs.get(island);
        if (amount == null) {
            manager.getRunnable().put(String.valueOf(island.getIslandID()), () -> {
                int size = 0;
                for (IslandEntity islandEntity : entityManager.getEntities(island.getTerritory())) {
                    if (islandEntity.isPlayer() || islandEntity.isMonster())
                        continue;
                    if (islandEntity.getType() == EntityType.PAINTING)
                        continue;
                    if (islandEntity.getType() == EntityType.DROPPED_ITEM)
                        continue;
                    size++;
                }
                mobs.put(island, size);
            });
            //if (!(entity instanceof Cow))
            //    e.setCancelled(true);
            return;
        }

        Integer limit = this.limit.get(island.getOwner().getGroup());
        if (limit == null) {
            limit = defaultLimit;
        }

        if (amount <= limit) {
            return;
        }

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(AsyncGamerQuitEvent e) { //при выходе всех участников острова, удалять раннейбл
        BukkitGamer gamer = e.getGamer();
        Island island = islandManager.getIsland(gamer.getPlayerID());
        if (island == null || !island.getOnlineGamers().isEmpty()) {
            return;
        }

        manager.getRunnable().remove(String.valueOf(island.getIslandID()));
    }

    @Override
    public void loadConfig() {
        limit.clear();

        SkyBlock instance = manager.getSkyBlock();
        FileConfiguration config = instance.getConfig();

        defaultLimit = config.getInt("spawnMobLimitDefault");
        config.getStringList("spawnMobLimit").forEach(string -> {
            String[] strings = string.split(":");
            Group group = Group.getGroupByName(strings[0]);
            int limit = Integer.parseInt(strings[1]);
            if (group != Group.DEFAULT)
                this.limit.put(group, limit);
        });
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }
}
