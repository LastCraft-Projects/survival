package net.lastcraft.skyblock.listener;

import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.Warp;
import net.lastcraft.alternate.api.manager.UserManager;
import net.lastcraft.alternate.api.manager.WarpManager;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.TitleAPI;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.gamer.IBaseGamer;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.Cooldown;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import net.lastcraft.skyblock.SkyBlock;
import net.lastcraft.skyblock.api.event.IslandAsyncCreateEvent;
import net.lastcraft.skyblock.api.event.IslandAsyncRemoveEvent;
import net.lastcraft.skyblock.api.event.IslandRemoveMemberEvent;
import net.lastcraft.skyblock.api.event.absract.IslandListener;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.island.member.IslandMember;
import net.lastcraft.skyblock.api.territory.IslandTerritory;
import net.lastcraft.skyblock.module.HomeModule;
import net.lastcraft.skyblock.utils.FaweUtils;
import net.lastcraft.skyblock.utils.ItemsContainer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;

public class IslandMainListener extends IslandListener {

    private final TitleAPI titleAPI = LastCraft.getTitlesAPI();
    private final UserManager userManager = AlternateAPI.getUserManager();

    private static final WarpManager WARP_MANAGER = AlternateAPI.getWarpManager();

    public IslandMainListener(SkyBlock skyBlock) {
        super(skyBlock);
    }

    @EventHandler
    public void onCreateIsland(IslandAsyncCreateEvent e) throws IOException {
        Player player = e.getPlayer();
        Island island = e.getIsland();
        IslandTerritory territory = island.getTerritory();

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return;
        }

        User user = userManager.getUser(player);
        if (user == null) {
            return;
        }

        Language lang = gamer.getLanguage();
        Location locationHome = island.getTerritory().getMiddleChunk().getMiddle();

        Group group = gamer.getGroup();
        String schematicName = island.getIslandType().getNameFile();
        List<ItemStack> items = ItemsContainer.getItems(group).getItems();
        FaweUtils.pasteSchematic(locationHome, schematicName, items);
        Biome biome = island.getIslandType().getBiome();
        FaweUtils.setBiome(territory, biome);

        island.getModule(HomeModule.class).setHome(locationHome);

        BukkitUtil.runTaskLater(20L, () -> {
            if (user.teleport(locationHome)) {
                locationHome.getWorld().spawnEntity(locationHome, EntityType.COW);

                titleAPI.sendTitle(player,
                        lang.getMessage("ISLAND_CREATE_TITLE"),
                        lang.getMessage("ISLAND_CREATE_SUBTITLE"));
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onGrow(BlockSpreadEvent e) { //todo запретить распространение хоруса и других блоков на чужую территорию
        Block source = e.getSource(); //с которого началось
        Block block = e.getBlock(); //какой блок изменился
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRemoveIsland(IslandAsyncRemoveEvent event) {
        Island island = event.getIsland();

        Location spawn = AlternateAPI.getSpawn();
        for (IslandMember islandMember : island.getMembers()) {
            IBaseGamer iBaseGamer = islandMember.getGamer();
            if (!iBaseGamer.isOnline()) {
                continue;
            }

            BukkitGamer gamer = (BukkitGamer) iBaseGamer;

            Player player = gamer.getPlayer();
            if (gamer.getPlayerID() != island.getOwner().getPlayerID())
                gamer.sendMessageLocale("ISLAND_REMOVED_MEMBER");

            BukkitUtil.runTask(() -> player.teleport(spawn));
        }

        IslandTerritory territory = island.getTerritory();
        removedWarps(territory);

        island.delete();
    }

    static void removedWarps(IslandTerritory territory) {
        BukkitUtil.runTaskAsync(() -> {
            for (Warp warp : WARP_MANAGER.getWarps().values()) { //удаление варпов c места, где удалили остров
                if (!isSkyBlockWorld(warp.getWorld()) || !territory.canInteract(warp.getLocation())) {
                    continue;
                }

                IBaseGamer ownerWarp = warp.getOwner();
                BukkitGamer gamer = GAMER_MANAGER.getGamer(ownerWarp.getPlayerID());
                if (gamer != null)
                    gamer.sendMessageLocale("ISLAND_WARP_REMOVED", warp.getName());

                warp.remove();
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRemovePlayer(IslandRemoveMemberEvent e) {  //при удалении с острова игрока - удалить его варпы
        Island island = e.getIsland();
        IslandTerritory territory = island.getTerritory();
        int removedPlayer = e.getMemberID();

        for (Warp warp : WARP_MANAGER.getWarps().values()) {
            if (!isSkyBlockWorld(warp.getWorld()))
                continue;
            if (warp.getOwnerID() != removedPlayer)
                continue;
            if (!territory.canInteract(warp.getLocation()))
                continue;

            warp.remove();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true) //кликаешь по обсидиану пустым ведром и у тебя лава в ведре
    public void onObsidianToLava(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Action action = e.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK)
            return;

        if (!isSkyBlockWorld(player.getWorld()))
            return;

        Block block = e.getClickedBlock();
        if (block.getType() != Material.OBSIDIAN)
            return;

        ItemStack item = e.getItem();
        if (item == null || item.getType() != Material.BUCKET)
            return;

        Island island = ISLAND_MANAGER.getIsland(player);
        if (island == null || !island.hasMember(player))
            return;

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        if (Cooldown.hasCooldown(gamer, "obsidian_to_lava")){
            Language lang = gamer.getLanguage();
            int time = Cooldown.getSecondCooldown(gamer, "obsidian_to_lava");
            gamer.sendMessageLocale("COOLDOWN", String.valueOf(time),
                    StringUtil.getCorrectWord(time, "TIME_SECOND_1", lang));
            return;
        }
        Cooldown.addCooldown(gamer, "obsidian_to_lava", 20L * 30);

        block.setType(Material.AIR);
        item.setAmount(item.getAmount() - 1);
        player.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET));
        e.setCancelled(true);
    }

    //todo сделать чтобы игроки не застревали в порталах в АД
    //todo запретить выходить за территорию своего острова
}
