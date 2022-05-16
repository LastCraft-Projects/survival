package net.lastcraft.skyblock.dependencies;

import lombok.Getter;
import net.lastcraft.skyblock.SkyBlock;
import net.lastcraft.skyblock.dependencies.clearlag.ClearLagg;
import net.lastcraft.skyblock.dependencies.mine.MineBlockManager;
import net.lastcraft.skyblock.dependencies.top.SkyBlockTop;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class DependManager {

    private final SkyBlock skyBlock;

    private final Map<Class<? extends SkyBlockDepend>, SkyBlockDepend> depends = new HashMap<>();
    private final Map<String, Runnable> runnable = new ConcurrentHashMap<>();

    //private final ScheduledExecutorService executorService;
    private final BukkitTask bukkitTask;

    public DependManager(SkyBlock skyBlock) {
        this.skyBlock = skyBlock;

        //executorService = Executors.newSingleThreadScheduledExecutor();
        //executorService.scheduleAtFixedRate(() -> {
        bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(skyBlock, ()-> {
            try {
                runnable.values().forEach(Runnable::run);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 20);

        init(MineBlockManager.class);
        init(ClearLagg.class);
        init(SkyBlockTop.class);
    }

    private void init(Class<? extends SkyBlockDepend> dependClass) {
        try {
            SkyBlockDepend skyBlockDepend = dependClass.getConstructor(DependManager.class).newInstance(this);
            skyBlockDepend.loadConfig();
            skyBlockDepend.init();
            depends.put(dependClass, skyBlockDepend);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isMineLocation(Block block) {
        MineBlockManager mineBlockManager = (MineBlockManager) depends.get(MineBlockManager.class);
        return mineBlockManager.getMineBlock(block) != null;
    }

    public ClearLagg getClearLagg() {
        return (ClearLagg) depends.get(ClearLagg.class);
    }

    public void loadAllConfigs() {
        depends.values().forEach(SkyBlockDepend::loadConfig);
    }

    //@Override
    //public void close()  {
    //    depends.values().forEach(SkyBlockDepend::disable);
    //    depends.clear();
    //    executorService.shutdown();
    //}


}
