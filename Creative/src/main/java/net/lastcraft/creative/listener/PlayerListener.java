package net.lastcraft.creative.listener;

import com.google.common.collect.ImmutableList;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.Cooldown;
import net.lastcraft.creative.CreativeAddon;
import net.lastcraft.creative.gui.CreativeMenuGui;
import net.lastcraft.dartaapi.listeners.DListener;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class PlayerListener extends DListener<CreativeAddon> {

    private final GamerManager gamerManager = LastCraft.getGamerManager();

    private final ImmutableList<Material> blockItems = ImmutableList.of(
            Material.SHULKER_SHELL, Material.BLACK_SHULKER_BOX,
            Material.ORANGE_SHULKER_BOX, Material.BLUE_SHULKER_BOX, Material.BROWN_SHULKER_BOX,
            Material.CYAN_SHULKER_BOX, Material.GRAY_SHULKER_BOX, Material.GREEN_SHULKER_BOX,
            Material.LIGHT_BLUE_SHULKER_BOX, Material.LIME_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX,
            Material.SILVER_SHULKER_BOX, Material.WHITE_SHULKER_BOX, Material.RED_SHULKER_BOX,
            Material.YELLOW_SHULKER_BOX, Material.PINK_SHULKER_BOX, Material.PURPLE_SHULKER_BOX
    );

    public PlayerListener(CreativeAddon javaPlugin) {
        super(javaPlugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        World world = player.getWorld();
        PlayerInventory playerInventory = player.getInventory();

        if (!world.getName().equalsIgnoreCase("creative")) {
            playerInventory.setItem(4, new ItemStack(Material.AIR));
            return;
        }

        playerInventory.setItem(4, ItemUtil.getBuilder(Material.NETHER_STAR)
                .setName("§eПомощь по режиму")
                .setLore(Arrays.asList(
                        "§7",
                        "§7Полезная информация",
                        "§7для игры на Creative"
                ))
                .build());
        playerInventory.setHeldItemSlot(4);
         //todo локализация
    }

    @EventHandler
    public void onTeleport(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        World world = player.getWorld();
        PlayerInventory playerInventory = player.getInventory();
        if (!world.getName().equalsIgnoreCase("creative")) {
            playerInventory.setItem(4, new ItemStack(Material.AIR));
            return;
        }

        playerInventory.setItem(4, ItemUtil.getBuilder(Material.NETHER_STAR)
                .setName("§eПомощь по режиму")
                .setLore(Arrays.asList(
                        "§7",
                        "§7Полезная информация",
                        "§7для игры на Creative"
                )).build());
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onFix(BlockPhysicsEvent e) {
        Block block = e.getBlock();
        if (block.getType() != Material.REDSTONE_WIRE && block.getType() != Material.REDSTONE)
            return;

        Block under = block.getWorld().getBlockAt(block.getLocation().subtract(0, 1, 0));
        if (under.getType() != Material.AIR)
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        if (!blockItems.contains(block.getType()))
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        ItemStack itemHand = e.getItem();
        Block block = e.getClickedBlock();
        if (block != null && blockItems.contains(block.getType())) {
            e.setCancelled(true);
        }

        Player player = e.getPlayer();
        if (itemHand == null)
            return;

        ItemMeta itemMeta = itemHand.getItemMeta();
        if (itemMeta == null)
            return;

        String displayName = itemMeta.getDisplayName();
        if (displayName == null || !displayName.equalsIgnoreCase("§eПомощь по режиму")) //todo чекать по локализации
            return;

        e.setCancelled(true);
        if (Cooldown.hasCooldown(player.getName(), "usables"))
            return;

        Cooldown.addCooldown(player.getName(), "usables", 20L);

        BukkitGamer gamer = gamerManager.getGamer(player);
        if (gamer == null)
            return;

        CreativeMenuGui creativeMenuGui = javaPlugin.getMenus().getOrDefault(
                gamer.getLanguage().getId(),
                javaPlugin.getMenus().get(Language.getDefault().getId()));
        creativeMenuGui.open(player);
    }
}
