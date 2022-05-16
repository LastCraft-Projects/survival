package net.lastcraft.skyblock.dependencies.mine;

import lombok.Getter;
import net.lastcraft.api.util.LocationUtil;
import net.lastcraft.skyblock.dependencies.DependManager;
import net.lastcraft.skyblock.dependencies.SkyBlockDepend;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MineBlockManager extends SkyBlockDepend {

    private final Random random = new Random();

    @Getter
    private final Map<Block, MineBlock> mineLocations = new HashMap<>();
    private final Map<Material, Integer> cooldownData = new HashMap<>();
    private final Map<Integer, Material> randomData = new HashMap<>();

    private int defaultCooldown;
    private MineListener listener;

    public MineBlockManager(DependManager manager) {
        super(manager);
    }

    @Override
    protected void init() {
        listener = new MineListener(manager.getSkyBlock(), this);
    }

    @Override
    public void loadConfig() {
        mineLocations.clear();
        randomData.clear();
        cooldownData.clear();

        FileConfiguration config = manager.getSkyBlock().getConfig();
        config.getStringList("mineLocations").forEach(stringLoc -> {
            Location location = LocationUtil.stringToLocation(stringLoc, false);
            Block block = location.getWorld().getBlockAt(location);
            mineLocations.put(block, new MineBlock(block, this));
        });

        if (mineLocations.size() < 1)
            return;

        for (String name : config.getConfigurationSection("blocksChance").getKeys(false)) {
            String patch = "blocksChance." + name + ".";
            Material material = Material.valueOf(config.getString(patch + "material"));
            int delay = config.getInt(patch + "delay");
            int percent = config.getInt(patch + "percent");
            cooldownData.put(material, delay);
            randomData.put(percent, material);
        }
        this.defaultCooldown = config.getInt("defaultDelay");

        manager.getRunnable().put("просто рандомные символы", () -> mineLocations.values().forEach(mineBlock -> {
            if (!mineBlock.canBreak() && mineBlock.getCooldown() <= 0)
                mineBlock.setBlock(randomMaterial());
        }));
    }

    private Material randomMaterial() {
        int random = this.random.nextInt(100) + 1;
        int sum = 0;
        for (Map.Entry<Integer, Material> data : randomData.entrySet()) {
            sum += data.getKey();
            if (random <= sum && data.getKey() != 0)
                return data.getValue();
        }
        return randomData.getOrDefault(this.random.nextInt(randomData.size()), Material.COBBLESTONE);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(listener);
    }

    public MineBlock getMineBlock(Block block) {
        return mineLocations.get(block);
    }

    public int getCooldown(Material type) {
        return cooldownData.getOrDefault(type, defaultCooldown); //стандартная КД, если вдруг что
    }
}
