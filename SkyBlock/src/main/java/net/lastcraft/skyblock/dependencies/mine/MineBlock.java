package net.lastcraft.skyblock.dependencies.mine;

import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class MineBlock {

    private final MineBlockManager manager;
    private final Block block;
    private int cooldown = -1;

    public MineBlock(Block block, MineBlockManager manager) {
        this.block = block;
        this.manager = manager;
    }

    public boolean canBreak() {
        return cooldown < 0;
    }

    public int getCooldown() {
        return cooldown--;
    }

    public void breakBlock() {
        cooldown = manager.getCooldown(block.getType());
        BukkitUtil.runTaskLater(2L, ()-> block.setType(Material.BEDROCK));
    }

    public void setBlock(Material material) {
        this.cooldown = -1;
        BukkitUtil.runTask(()-> block.setType(material));
    }
}
