package net.lastcraft.alternate.listener;

import net.lastcraft.alternate.Alternate;
import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.manager.UserManager;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.alternate.object.CraftUser;
import net.lastcraft.api.CoreAPI;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.Inventory;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerListener implements Listener {

    private final UserManager userManager = AlternateAPI.getUserManager();
    private final GamerManager gamerManager = LastCraft.getGamerManager();
    private final CoreAPI coreAPI = LastCraft.getCoreAPI();
    private final static Map<String, Boolean> MAP_TP_ERROR_BEFORE_TP = new ConcurrentHashMap<>();
    private final ConfigData configData;

    public PlayerListener(Alternate main) {
        this.configData = Alternate.getConfigData();
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        User user = userManager.getUser(player);

        if (configData.isSpawnTp() || user.isFirstJoin()) {
            player.teleport(AlternateAPI.getSpawn());
        }

        if (user.isFly() && !configData.isSpawnFlyOff()) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }
    }

    @EventHandler
    public void onSetSpawn(PlayerSpawnLocationEvent e) {
        Player player = e.getPlayer();
        User user = userManager.getUser(player);

        if (!configData.isSpawnTp() || !user.isFirstJoin())
            return;

        e.setSpawnLocation(AlternateAPI.getSpawn());
    }

    @EventHandler
    public void onPortal(PortalCreateEvent e) {
        e.setCancelled(!configData.isPortalCreate());
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;

        Player player = (Player) e.getEntity();
        User user = userManager.getUser(player);
        if (!user.isGod())
            return;

        e.setCancelled(true);

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) { //запретить в верстаке recipe что-то менять
        Inventory top = e.getView().getTopInventory();
        InventoryType type = top.getType();

        if (type == InventoryType.WORKBENCH) {
            Player player = (Player) e.getWhoClicked();
            if (player.getGameMode() == GameMode.CREATIVE)
                e.setCancelled(true);

            CraftUser user = (CraftUser) userManager.getUser(player);
            if (user.isRecipeSee())
                e.setCancelled(true);

        }
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent e) { //запретить в верстаке recipe что-то менять
        Inventory top = e.getView().getTopInventory();
        InventoryType type = top.getType();
        CraftUser user = (CraftUser) userManager.getUser((Player)e.getPlayer());
        if (type == InventoryType.WORKBENCH){
            if (user.isRecipeSee()){
                user.setRecipeSee(false);
                e.getView().getTopInventory().clear();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        User user = userManager.getUser(player);
        if (user == null) {
            return;
        }

        if (user.checkAfk()) {
            coreAPI.sendToServer(player, "@limbo");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawnTp(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.getLocation().getBlockY() < -10) {
            BukkitGamer gamer = gamerManager.getGamer(player);
            if (gamer == null) {
                return;
            }

            String world = player.getLocation().getWorld().getName().toLowerCase();
            Integer level = configData.getVoidWorld().get(world);
            if (level == null || gamer.getGroup().getLevel() < level) {
                return;
            }

            player.teleport(AlternateAPI.getSpawn());
        }

        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()) {
            //if (user.checkAfk()) {
            //    coreAPI.sendToServer(player, "@limbo");
            //    user.updateAfkPosition();
            //}
            return;
        }

        User user = userManager.getUser(player);
        if (user == null) {
            return;
        }

        user.updateAfkPosition();
    }

    @EventHandler
    public void onSignColor(SignChangeEvent e) {
        for (int i = 0; i <= 3; i++) {
            String line = e.getLine(i);
            line = ChatColor.translateAlternateColorCodes('&', line);
            e.setLine(i, line);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        if (configData.getInt("waitTeleport") == 0) {
            return;
        }
        Player player = e.getPlayer();
        String name = player.getName();

        if (e.getFrom().getBlockX() != e.getTo().getBlockX()
                || e.getFrom().getBlockY() != e.getTo().getBlockY()
                || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
            if (MAP_TP_ERROR_BEFORE_TP.get(name) != null && PlayerListener.getMapTpErrorBeforeTp().get(name)) {
                MAP_TP_ERROR_BEFORE_TP.put(name, false);
                BukkitGamer gamer = gamerManager.getGamer(player);
                if (gamer == null) {
                    return;
                }
                gamer.sendMessageLocale("TELEPORT_MOVE_ERROR");
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        Entity entity = e.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            User user = userManager.getUser(player);

            if (user != null && user.isGod()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.setDeathMessage(null);
        Player player = e.getEntity();
        User user = userManager.getUser(player);
        if (user == null) {
            return;
        }

        user.setLastLocation(player.getLocation());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (!configData.isBedHomeSystem()) {
            e.setRespawnLocation(AlternateAPI.getSpawn());
            return;
        }

        Player player = e.getPlayer();
        User user = userManager.getUser(player);
        if (user == null) {
            return;
        }

        e.setRespawnLocation(user.getBedLocation()); //при респавне спавним у кровати
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        Entity entity = e.getEntity();
        if (damager instanceof Player) {
            Player player = (Player) damager;
            User user = userManager.getUser(player);

            if (user != null && (user.isGod() || user.isFly())) {
                if (entity instanceof Player) {
                    e.setCancelled(true);
                }
            }
        }

        if (isProjectileDamage(entity, damager)) {
            if (entity instanceof Player) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBedClick(PlayerInteractEvent e) { //когда кликнул на кровать, задать локацию
        if (!configData.isBedHomeSystem())
            return;

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        //Player player = e.getPlayer();
        //if (player.getWorld().getTime() > 18000 && player.getWorld().getTime() < 23450)
        //    return;

        User user = userManager.getUser(e.getPlayer());
        if (user == null)
            return;

        Block block = e.getClickedBlock();
        if (block == null)
            return;

        if (block.getType() != Material.BED && block.getType() != Material.BED_BLOCK)
            return;

        user.setBedLocation(block.getLocation().clone());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);
    }

    private boolean isProjectileDamage(Entity entity, Entity damager){
        if (damager instanceof Projectile) {
            Projectile projectile = (Projectile) damager;
            if (projectile.getShooter() instanceof Player){
                Player damagerPlayer = (Player)projectile.getShooter();

                User user = userManager.getUser(damagerPlayer.getName());
                if (user == null) {
                    return false;
                }

                if (user.isFly() || user.isGod()) {
                    return entity instanceof Player;
                }

            }
        }

        return false;
    }

    public static Map<String, Boolean> getMapTpErrorBeforeTp() {
        return MAP_TP_ERROR_BEFORE_TP;
    }
}
