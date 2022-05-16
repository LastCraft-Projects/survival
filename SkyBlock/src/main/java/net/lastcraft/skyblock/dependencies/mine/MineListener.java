package net.lastcraft.skyblock.dependencies.mine;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.effect.ParticleAPI;
import net.lastcraft.api.effect.ParticleEffect;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import net.lastcraft.skyblock.api.event.absract.IslandListener;
import net.lastcraft.skyblock.api.event.world.MineBlockBreakEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class MineListener extends IslandListener {

    //private final SoundAPI soundAPI = LastCraft.getSoundAPI();
    private final Random random = new Random();
    private final ParticleAPI particleAPI = LastCraft.getParticleAPI();
    private final MineBlockManager manager;

    public MineListener(JavaPlugin javaPlugin, MineBlockManager manager) {
        super(javaPlugin);
        this.manager = manager;
        this.manager.getMineLocations().values().forEach(mineBlock -> mineBlock.setBlock(Material.COBBLESTONE));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Player player = e.getPlayer();

        if (isSkyBlockWorld(block.getWorld()))
            return;

        MineBlock mineBlock = manager.getMineBlock(block);
        if (mineBlock == null || !mineBlock.canBreak() || block.getType() == Material.BEDROCK)
            return;

        MineBlockBreakEvent event = new MineBlockBreakEvent(player, block);
        BukkitUtil.callEvent(event);

        if (event.isCancelled())
            return;

        e.setDropItems(false);
        mineBlock.breakBlock();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreakMine(MineBlockBreakEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();

        player.getInventory().addItem(getItem(block));
        particleAPI.sendEffect(ParticleEffect.SPELL,block.getLocation().clone().add(0.0, 0.8, 0.0),
                0.2f, 0.22f, 0.2f, 0.05f, 5, player);
    }

    private ItemStack getItem(Block block) {
        Material material = block.getType();
        ItemStack item;
        switch (material) {
            case STONE:
                item = new ItemStack(Material.COBBLESTONE);
                break;
            case COAL_ORE:
                item = new ItemStack(Material.COAL);
                break;
            case DIAMOND_ORE:
                item = new ItemStack(Material.DIAMOND);
                break;
            case EMERALD_ORE:
                item = new ItemStack(Material.EMERALD);
                break;
            case GOLD_ORE:
                item = new ItemStack(Material.GOLD_INGOT);
                break;
            case IRON_ORE:
                item = new ItemStack(Material.IRON_INGOT);
                break;
            case REDSTONE_ORE:
                item = new ItemStack(Material.REDSTONE, random.nextInt(5) + 4);
                break;
            case GLOWING_REDSTONE_ORE:
                item = new ItemStack(Material.GLOWSTONE_DUST,random.nextInt(4) + 2);
                break;
            case QUARTZ_ORE:
                item = new ItemStack(Material.QUARTZ);
                break;
            case LAPIS_ORE:
                item = new ItemStack(Material.INK_SACK, random.nextInt(6) + 3, (short)4);
                break;
            default:
                item = new ItemStack(material);
                break;
        }

        return item;
    }

}
