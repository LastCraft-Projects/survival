package net.lastcraft.skyblock.listener;

import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.event.AsyncGamerJoinEvent;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.scoreboard.DisplaySlot;
import net.lastcraft.api.scoreboard.Objective;
import net.lastcraft.api.scoreboard.ScoreBoardAPI;
import net.lastcraft.dartaapi.achievements.achievement.AchievementPlayer;
import net.lastcraft.dartaapi.achievements.manager.AchievementManager;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import net.lastcraft.skyblock.SkyBlock;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.entity.SkyGamer;
import net.lastcraft.skyblock.api.event.*;
import net.lastcraft.skyblock.api.event.absract.IslandListener;
import net.lastcraft.skyblock.api.event.absract.IslandMemberEvent;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.manager.SkyGamerManager;
import net.lastcraft.skyblock.dependencies.SkyBlockBoard;
import net.lastcraft.skyblock.utils.IslandLoader;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.List;

public class SkyGamerListener extends IslandListener {

    private final SkyGamerManager manager = SkyBlockAPI.getSkyGamerManager();
    private final ScoreBoardAPI scoreBoardAPI = LastCraft.getScoreBoardAPI();

    private final AchievementManager achievementManager;
    private final SkyBlockBoard skyBlockBoard;

    private Objective objectives;

    public SkyGamerListener(SkyBlock skyBlock) {
        super(skyBlock);

        this.achievementManager = skyBlock.getAchievementManager();
        this.skyBlockBoard = new SkyBlockBoard();

        registerObjectives();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED)
            return;

        if (!IslandLoader.isLoad()) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Server started, pls wait....");
            return;
        }

        String name = e.getName();

        BukkitGamer gamer = GAMER_MANAGER.getGamer(name);
        if (gamer == null) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cОшибка при загрузке данных...");
            return;
        }

        AchievementPlayer achievementPlayer = achievementManager.getPlayerManager().getAchievementPlayer(name);
        if (achievementPlayer == null) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cПерезайдите, ошибка при загрузке данных");
            return;
        }

        SkyGamer skyGamer = IslandLoader.getSkyGamer(achievementPlayer);
        if (skyGamer == null) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cПерезайдите, ошибка при загрузке данных");
            return;
        }
        manager.addSkyGamer(skyGamer);

        if (manager.getSkyGamer(name) != null)
            return;

        manager.removeSkyGamer(name);
        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cПерезайдите, ошибка при загрузке данных");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        String name = player.getName();

        manager.removeSkyGamer(name);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        player.setGameMode(GameMode.SURVIVAL);

        if (player.hasPlayedBefore())
            return;

        player.teleport(AlternateAPI.getSpawn());
    }

    @EventHandler
    public void onSetSpawn(PlayerSpawnLocationEvent e) {
        Player player = e.getPlayer();
        if (player.hasPlayedBefore())
            return;

        e.setSpawnLocation(AlternateAPI.getSpawn());
    }

    @EventHandler
    public void onJoinAsync(AsyncGamerJoinEvent e) {
        Player player = e.getPlayer();
        skyBlockBoard.sendBoard(player);

        Island island = ISLAND_MANAGER.getIsland(player);
        if (island == null)
            return;

        objectives.setScore(player, island.getLevel());
    }

    @EventHandler
    public void onIslandLvlUp(IslandLevelUpEvent e) {
        Island island = e.getIsland();
        int level = e.getLevel();
        BukkitUtil.runTaskAsync(() -> {
            List<Player> players = island.getOnlineMembers();
            players.forEach(player -> objectives.setScore(player, level));
        });
    }

    @EventHandler
    public void onMemberChange(IslandMemberEvent e) {
        Island island = e.getIsland();
        int member = e.getMemberID();
        BukkitGamer gamer = GAMER_MANAGER.getGamer(member);
        if (gamer == null)
            return;

        Player player = gamer.getPlayer();
        if (player == null || !player.isOnline())
            return;

        if (e instanceof IslandRemoveMemberEvent)
            objectives.setScore(player, 0);

        if (e instanceof IslandAddMemberEvent)
            objectives.setScore(player, island.getLevel());
    }

    @EventHandler
    public void onIslandCreate(IslandAsyncCreateEvent e) {
        Island island = e.getIsland();
        Player player = e.getPlayer();

        objectives.setScore(player, island.getLevel());
        skyBlockBoard.sendBoard(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRemoveIsland(IslandAsyncRemoveEvent e) {
        Island island = e.getIsland();
        List<Player> players = island.getOnlineMembers();
        players.forEach(player -> {
            objectives.setScore(player, 0);
            skyBlockBoard.sendBoard(player);
        });

    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        if (e.getRecipe().getResult().getType() != Material.END_CRYSTAL)
            return;
        e.setCancelled(true);
    }

    private void registerObjectives() {
        objectives = scoreBoardAPI.createObjective("level", "dummy");
        objectives.setDisplayName("§e❖");
        objectives.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objectives.setPublic(true);
    }

}
