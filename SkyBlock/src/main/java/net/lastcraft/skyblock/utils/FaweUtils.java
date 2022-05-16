package net.lastcraft.skyblock.utils;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.bukkit.wrapper.AsyncWorld;
import com.boydti.fawe.object.schematic.Schematic;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.biome.BaseBiome;
import lombok.experimental.UtilityClass;
import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import net.lastcraft.dartaapi.utils.core.CoreUtil;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.manager.EntityManager;
import net.lastcraft.skyblock.api.territory.IslandTerritory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class FaweUtils {

    //private final CommandsAPI COMMANDS_API = LastCraft.getCommandsAPI();
    private final AsyncWorld ASYNC_WORLD = AsyncWorld.create(new WorldCreator(SkyBlockAPI.getSkyBlockWorldName()));
    private final EntityManager ENTITY_MANAGER = SkyBlockAPI.getEntityManager();
    private final EditSession EDIT_SESSION = new EditSessionBuilder(new BukkitWorld(SkyBlockAPI.getSkyBlockWorld()))
            .limitUnlimited()
            .fastmode(true)
            .build();

    public void pasteSchematic(Location location, String schematicName, List<ItemStack> chestItems)
            throws IOException {
        File file = new File(CoreUtil.getConfigDirectory() + "/schematics/"
                + schematicName + ".schematic");

        Vector center = new Vector(location.getX(), location.getY(), location.getZ());
        Schematic schematic = FaweAPI.load(file);


        Extent extent = new AbstractDelegateExtent(EDIT_SESSION) {
            @Override
            public boolean setBlock(Vector vector, BaseBlock block) throws WorldEditException {
                if (block.getId() == BlockID.CHEST) {

                    Location chestLocation = location.clone();
                    chestLocation.setX(vector.getBlockX());
                    chestLocation.setY(vector.getBlockY());
                    chestLocation.setZ(vector.getBlockZ());

                    BukkitUtil.runTask(() -> {
                        Block chestBlock = chestLocation.getBlock();
                        chestBlock.setType(Material.CHEST);
                        chestBlock.setData((byte) block.getData());
                        Chest chest = (Chest) chestBlock.getState();
                        Inventory blockInventory = chest.getBlockInventory();
                        chestItems.forEach(blockInventory::addItem);
                    });

                    return false;
                }

                return super.setBlock(vector, block);
            }
        };

        schematic.paste(extent, center, false);

        EDIT_SESSION.flushQueue();
    }

    public Biome getBiome(Location location) {
        Biome biome = ASYNC_WORLD.getBlockAt(
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()).getBiome();
        ASYNC_WORLD.commit();
        if (biome == null)
            return Biome.FOREST;
        return biome;
    }

    public void resetBlocks(IslandTerritory territory) {
        CuboidRegion region = getRegion(territory);
        EDIT_SESSION.setBlocks(region, new BaseBlock(Material.AIR.getId()));

        BukkitUtil.runTask(() -> {
            for (org.bukkit.entity.Player player : ENTITY_MANAGER.getPlayers(territory)) {
                if (player == null || !player.isOnline())
                    continue;

                player.teleport(AlternateAPI.getSpawn());
            }
        });

        EDIT_SESSION.getEntities(region).forEach(Entity::remove);

        EDIT_SESSION.flushQueue();
    }

    public CuboidRegion getRegion(IslandTerritory territory) {
        Location up = territory.getCordAngel().getFirst();
        Location down = territory.getCordAngel().getSecond();

        return getRegion(up, down);
    }

    public CuboidRegion getRegion(Location up, Location down) {
        Vector pos1 = new Vector(down.getX(), down.getY(), down.getZ());
        Vector pos2 = new Vector(up.getX(), up.getY(), up.getZ());
        return new CuboidRegion(pos1, pos2);
    }

    public List<Entity> getEntities(IslandTerritory territory) {
        List<Entity> entities = new ArrayList<>();
        if (territory == null)
            return entities;

        entities.addAll(EDIT_SESSION.getEntities(getRegion(territory)));

        return entities;
    }

    public void setBiome(IslandTerritory territory, Biome biome) {
        BukkitUtil.runTaskAsync(() -> {
            BaseBiome baseBiome = new BaseBiome(biome.ordinal());
            getRegion(territory).forEach(block -> EDIT_SESSION.setBiome(block.getBlockX(),
                    block.getBlockY(),
                    block.getBlockZ(),
                    baseBiome));

            EDIT_SESSION.flushQueue();
        });
    }

    public void disableCommand(JavaPlugin plugin) {
        /*
        WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        Plugin fawePlugin = Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit");

        HandlerList.unregisterAll(fawePlugin);
        HandlerList.unregisterAll(worldEdit);

        Bukkit.getScheduler().cancelTasks(fawePlugin);
        Bukkit.getScheduler().cancelTasks(worldEdit);

        COMMANDS_API.disableCommands(fawePlugin);
        COMMANDS_API.disableCommands(worldEdit);

        BukkitServerInterface bukkitServerInterface = (BukkitServerInterface) worldEdit.getServerInterface();
        bukkitServerInterface.unregisterCommands();
        */

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.LOWEST)
            public void onDisableCommand(PlayerCommandPreprocessEvent e) {
                String message = e.getMessage();
                if (message.startsWith("//")
                        || message.startsWith("/worldedit")
                        || message.startsWith("/we")
                        || message.startsWith("/fawe")
                        || message.startsWith("/brush")
                        ) {
                    e.setMessage("/sosat");
                }
            }
        }, plugin);
    }
}
