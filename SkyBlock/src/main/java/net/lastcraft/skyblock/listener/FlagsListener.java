package net.lastcraft.skyblock.listener;

import com.google.common.collect.ImmutableList;
import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.Warp;
import net.lastcraft.alternate.api.events.UserCreateWarpEvent;
import net.lastcraft.alternate.api.events.UserEvent;
import net.lastcraft.alternate.api.events.UserTeleportByCommandEvent;
import net.lastcraft.alternate.api.events.UserTeleportToWarpEvent;
import net.lastcraft.api.BorderAPI;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.skyblock.SkyBlock;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.event.absract.IslandListener;
import net.lastcraft.skyblock.api.event.module.IslandSetFlagEvent;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.island.IslandFlag;
import net.lastcraft.skyblock.api.manager.EntityManager;
import net.lastcraft.skyblock.api.territory.IslandTerritory;
import net.lastcraft.skyblock.module.FlagModule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.projectiles.ProjectileSource;

public class FlagsListener extends IslandListener {
    private final ImmutableList<Material> interactMaterials = ImmutableList.of( //вещи для флага use
            Material.ENDER_CHEST, Material.WORKBENCH,
            Material.ENCHANTMENT_TABLE, Material.REDSTONE_COMPARATOR, Material.REDSTONE_COMPARATOR,
            Material.REDSTONE_COMPARATOR_OFF, Material.REDSTONE_COMPARATOR_ON, Material.DIODE,
            Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON,
            Material.ANVIL, Material.DARK_OAK_DOOR, Material.COMMAND_REPEATING,
            Material.ACACIA_DOOR, Material.BIRCH_DOOR, Material.TRAP_DOOR,
            Material.IRON_DOOR, Material.JUNGLE_DOOR, Material.IRON_TRAPDOOR,
            Material.SPRUCE_DOOR, Material.SPRUCE_DOOR, Material.ACACIA_FENCE_GATE,
            Material.WOODEN_DOOR, Material.WOOD_DOOR, Material.BED,
            Material.NOTE_BLOCK, Material.LEVER, Material.STONE_PLATE,
            Material.GOLD_PLATE, Material.IRON_PLATE, Material.WOOD_PLATE,
            Material.STONE_BUTTON, Material.WOOD_BUTTON, Material.TRIPWIRE_HOOK,
            Material.BIRCH_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.FENCE_GATE,
            Material.JUNGLE_FENCE_GATE, Material.SPRUCE_FENCE_GATE, Material.DETECTOR_RAIL
    );

    private final BorderAPI borderAPI = LastCraft.getBorderAPI();
    private final EntityManager entityManager = SkyBlockAPI.getEntityManager();

    public FlagsListener(SkyBlock skyBlock) {
        super(skyBlock);
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent e) { //PVP flag
        Entity entity = e.getEntity();         //игрок
        Entity damagerEntity = e.getDamager(); //кто дамажит
        Location location = entity.getLocation();

        if (!isSkyBlockWorld(location) || !(entity instanceof Player))
            return;

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island == null)
            return;

        Player damager = getDamager(damagerEntity, e);
        if (damager == null)
            return;

        BukkitGamer gamer = GAMER_MANAGER.getGamer(damager);
        if (gamer == null) {
            e.setCancelled(true);
            return;
        }

        FlagModule module = island.getModule(FlagModule.class);
        if (module == null)
            return;

        if (module.isFlag(IslandFlag.PVP))
            return;

        entity.setFireTicks(0);
        gamer.sendMessageLocale("ISLAND_NO_PVP");
        e.setCancelled(true);
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent e) { //HUNGER
        Entity entity = e.getEntity();

        if (!(entity instanceof Player) || !isSkyBlockWorld(entity.getWorld()))
            return;

        Player player = (Player) entity;
        Island island = ISLAND_MANAGER.getIsland(player.getLocation());
        if (island == null) {
            return;
        }

        FlagModule module = island.getModule(FlagModule.class);
        if (module == null || !module.isFlag(IslandFlag.HUNGER)) {
            return;
        }

        e.setCancelled(player.getFoodLevel() >= e.getFoodLevel());
    }


    @EventHandler
    public void onCreateWarp(UserCreateWarpEvent e) {
        Warp warp = e.getWarp();
        User user = e.getUser();

        BukkitGamer gamer = GAMER_MANAGER.getGamer(user.getName());
        if (gamer == null) {
            e.setCancelled(true);
            return;
        }

        Player player = gamer.getPlayer();
        Location location = warp.getLocation();

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island == null) {
            return;
        }

        if (island.hasMember(player) || gamer.isFriend(island.getOwner().getPlayerID())) { //друзья владельца могут создать
            return;
        }

        FlagModule module = island.getModule(FlagModule.class);
        if (module == null || module.isFlag(IslandFlag.OPENED)) {
            return;
        }

        gamer.sendMessageLocale("ISLAND_NOT_CREATE_WARP");
        e.setCancelled(true);
    }

    @EventHandler
    public void onRemovedWarp(IslandSetFlagEvent e) { //при закрытии острова удалить все
        Island island = e.getIsland();                //варпы и их владельцам написать про это + всех левых ТП на спавн
        IslandFlag flag = e.getFlag();
        boolean result = e.isResult();

        if (flag != IslandFlag.OPENED || result) {
            return;
        }

        IslandTerritory territory = island.getTerritory();
        IslandMainListener.removedWarps(territory);

        for (Player player : entityManager.getPlayers(territory)) {
            if (island.hasMember(player)) {
                continue;
            }

            player.teleport(AlternateAPI.getSpawn());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();

        if (!isSkyBlockWorld(player.getWorld()) || block == null) {
            return;
        }

        if (!interactMaterials.contains(block.getType())) {
            return;
        }

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            e.setCancelled(true);
            return;
        }

        Island island = ISLAND_MANAGER.getIsland(block.getLocation());
        if (island == null || island.hasMember(gamer)) {
            return;
        }

        FlagModule module = island.getModule(FlagModule.class);
        if (module == null || module.isFlag(IslandFlag.USE)) {
            return;
        }

        //gamer.sendMessageLocale("ISLAND_NOT_USED");
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        Entity entity = e.getEntity();
        Location location = entity.getLocation();

        if (!isSkyBlockWorld(location)) {
            return;
        }

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island == null) {
            return;
        }

        FlagModule module = island.getModule(FlagModule.class);
        if (module == null || module.isFlag(IslandFlag.ENTITY_EXPLODE)) {
            return;
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamager(EntityDamageByEntityEvent e) { //отключить урон по мобам
        Entity entityDamager = e.getDamager();
        Entity entity = e.getEntity();

        Location location = entity.getLocation();
        if (!isSkyBlockWorld(location))
            return;

        if (!(entity instanceof Animals) && !(entity instanceof Villager))
            return;

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island == null)
            return;

        Player damager = getDamager(entityDamager, e);
        if (damager == null)
            return;

        BukkitGamer gamer = GAMER_MANAGER.getGamer(damager);
        if (gamer == null) {
            e.setCancelled(true);
            return;
        }

        if (island.hasMember(damager))
            return;

        FlagModule module = island.getModule(FlagModule.class);
        if (module == null)
            return;

        if (module.isFlag(IslandFlag.ENTITY_DAMAGE))
            return;

        e.setCancelled(true);
        entity.setFireTicks(0);
        gamer.sendMessageLocale("ISLAND_NOT_YOU");
    }

    static Player getDamager(Entity entity, Cancellable e) {
        Player damager = null;
        if (entity instanceof Player) {
            damager = (Player) entity;
        }

        if (entity instanceof Projectile) {
            Projectile projectile = (Projectile) entity;
            ProjectileSource source = projectile.getShooter();
            if (source instanceof Player) {
                damager = (Player) source;
            }
        }

        if (entity instanceof AreaEffectCloud) {
            AreaEffectCloud areaEffectCloud = (AreaEffectCloud) entity;
            ProjectileSource source = areaEffectCloud.getSource();
            if (source instanceof Player) {
                damager = (Player) source;
            }
        }

        if (entity instanceof Firework) {
            e.setCancelled(true);
        }

        return damager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) { //отключение флага если сняли донат
        Player player = e.getPlayer();
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        Island island = ISLAND_MANAGER.getIsland(player);
        if (island == null)
            return;

        if (!island.hasOwner(gamer) || gamer.isMagma())
            return;

        FlagModule flagModule = island.getModule(FlagModule.class);
        if (flagModule == null || !flagModule.isFlag(IslandFlag.INVINCIBLE)) {
            return;
        }

        flagModule.setFlag(IslandFlag.INVINCIBLE, false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProjectile(ProjectileHitEvent e) { //запретить удочкой по мобу
        Projectile projectile = e.getEntity();
        Entity entity = e.getHitEntity();
        if (entity == null) {
            return;
        }

        Location location = entity.getLocation();

        ProjectileSource source = projectile.getShooter();
        if (!(source instanceof Player)) {
            return;
        }

        Player damager = (Player) source;

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island == null || island.hasMember(damager)) {
            return;
        }

        FlagModule module = island.getModule(FlagModule.class);
        if (module == null || module.isFlag(IslandFlag.ENTITY_DAMAGE)) {
            return;
        }

        projectile.remove();
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) { //запретить выкидывать вещи на острове
        Player player = e.getPlayer();
        Location location = player.getLocation();

        if (!isSkyBlockWorld(location))
            return;

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            e.setCancelled(true);
            return;
        }

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island == null || island.hasMember(gamer)) {
            return;
        }

        FlagModule module = island.getModule(FlagModule.class);
        if (module == null || module.isFlag(IslandFlag.DROP)) {
            return;
        }

        gamer.sendMessageLocale("ISLAND_NOT_YOU");
        e.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) { //поднимание айтемов
        Player player = e.getPlayer();
        Item item = e.getItem();
        if (item == null || item.getItemStack() == null) {
            return;
        }

        Location location = item.getLocation();
        if (location == null || !isSkyBlockWorld(location)) {
            return;
        }

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island == null || island.hasMember(player)) {
            return;
        }

        FlagModule module = island.getModule(FlagModule.class);
        if (module == null || module.isFlag(IslandFlag.PICKUP)) {
            return;
        }

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplodeEvent(EntityDamageByEntityEvent e) { //запретить криперам взрывать стойки брони
        Entity damager = e.getDamager();
        Entity entity = e.getEntity();

        if (entity == null || damager == null)
            return;

        if (!(damager instanceof Creeper) || !(entity instanceof ArmorStand)) {
            return;
        }

        Location location = entity.getLocation();
        if (!isSkyBlockWorld(location)) {
            return;
        }

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island == null)
            return;

        FlagModule module = island.getModule(FlagModule.class);
        if (module == null || module.isFlag(IslandFlag.ENTITY_EXPLODE)) {
            return;
        }

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplodeEvent(HangingBreakByEntityEvent e) { //запретить криперам взрывать рамки и картины
        Entity damager = e.getRemover();
        Entity hanging = e.getEntity();

        if (hanging == null || damager == null)
            return;

        if (!(damager instanceof Creeper))
            return;

        if (!(hanging instanceof Painting) && !(hanging instanceof ItemFrame))
            return;

        Location location = hanging.getLocation();

        if (!isSkyBlockWorld(location))
            return;

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island == null)
            return;

        FlagModule module = island.getModule(FlagModule.class);
        if (module == null || module.isFlag(IslandFlag.ENTITY_EXPLODE))
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onLeave(LeavesDecayEvent e) { //запретить опадение листы
        Block block = e.getBlock();
        Location location = block.getLocation();

        if (!isSkyBlockWorld(location))
            return;

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island == null)
            return;

        FlagModule module = island.getModule(FlagModule.class);
        if (module == null)
            return;

        if (module.isFlag(IslandFlag.DROP_LEAVES))
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onSpawnMob(CreatureSpawnEvent e) { //отключить спавн мобов на территории
        LivingEntity livingEntity = e.getEntity();
        Location location = livingEntity.getLocation();

        if (!isSkyBlockWorld(location))
            return;

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island == null)
            return;

        FlagModule module = island.getModule(FlagModule.class);
        if (module == null)
            return;

        if (module.isFlag(IslandFlag.SPAWN_MOB) || livingEntity.getType() == EntityType.VILLAGER)
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onInvincible(EntityDamageEvent e) { //неязвимость от всего для игроков на острове
        Entity entity = e.getEntity();
        if (!(entity instanceof Player))
            return;

        Location location = entity.getLocation();

        if (!isSkyBlockWorld(location))
            return;

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island == null)
            return;

        FlagModule module = island.getModule(FlagModule.class);
        if (module == null)
            return;

        if (!module.isFlag(IslandFlag.INVINCIBLE))
            return;

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockSpread(BlockSpreadEvent e) { //офнуть распр огня
        if (e.getSource().getType() != Material.FIRE && e.getSource().getType() != Material.FIREBALL) {
            return;
        }
        disableFire(e.getBlock(), e);
    }

    private void disableFire(Block block, Cancellable e) {
        Location location = block.getLocation();

        if (!isSkyBlockWorld(location))
            return;

        Island island = ISLAND_MANAGER.getIsland(location);
        if (island == null)
            return;

        FlagModule module = island.getModule(FlagModule.class);
        if (module == null)
            return;

        if (module.isFlag(IslandFlag.FIRE_SPREAD))
            return;

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBurn(BlockBurnEvent e) { //офнуть распр огня
        disableFire(e.getBlock(), e);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(UserTeleportByCommandEvent e) {//при тп на чужой остров офать год и флай и если надо, то отменять ТП
        disableGodFly(e, e.getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleportToWarp(UserTeleportToWarpEvent e) {
        disableGodFly(e, e.getWarp().getLocation());
    }

    private void disableGodFly(UserEvent e, Location to) {
        if (to == null) {
            return;
        }

        User user = e.getUser();
        Player player = user.getPlayer();
        if (player == null) {
            return;
        }

        Island island = ISLAND_MANAGER.getIsland(to);
        if (island == null) {
            borderAPI.removeBoard(player);
            return;
        }

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        if (island.hasMember(player) || gamer.isFriend(island.getOwner().getPlayerID()))
            return;

        FlagModule flagModule = island.getModule(FlagModule.class);
        if (flagModule == null)
            return;

        if (flagModule.isFlag(IslandFlag.OPENED)) {
            user.setFly(false, false);
            user.setGod(false, false);
        } else {
            gamer.sendMessageLocale("ISLAND_CLOSED");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onMobGrif(EntityChangeBlockEvent  e) { //отменяем гриферство плохих мобов
        Entity entity = e.getEntity();                 //(таскание земли эндерманом, ломание калитки зомби и тд)
        if (!(entity instanceof Monster))
            return;

        Island island = ISLAND_MANAGER.getIsland(entity.getLocation());
        if (island == null)
            return;

        FlagModule flagModule = island.getModule(FlagModule.class);
        if (flagModule == null)
            return;

        if (flagModule.isFlag(IslandFlag.ENTITY_GRIF))
            return;

        e.setCancelled(true);
    }

    //todo если OPENED = false запретить тп на остров чужим

}