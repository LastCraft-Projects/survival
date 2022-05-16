package net.lastcraft.skyblock.gui;

import net.lastcraft.api.manager.GuiManager;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.skyblock.SkyBlock;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.SkyBlockGui;
import net.lastcraft.skyblock.api.entity.SkyGamer;
import net.lastcraft.skyblock.api.event.IslandAddMemberEvent;
import net.lastcraft.skyblock.api.event.IslandAsyncCreateEvent;
import net.lastcraft.skyblock.api.event.IslandAsyncRemoveEvent;
import net.lastcraft.skyblock.api.event.absract.IslandListener;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.manager.SkyGamerManager;
import net.lastcraft.skyblock.command.IslandsCommand;
import net.lastcraft.skyblock.craftisland.CraftSkyGamer;
import net.lastcraft.skyblock.gui.guis.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GuiListener extends IslandListener {

    private final GuiManager<SkyBlockGui> manager = SkyBlockAPI.getSkyGuiManager();
    private final SkyGamerManager skyGamerManager = SkyBlockAPI.getSkyGamerManager();
    private final ScheduledExecutorService executorService;

    public GuiListener(SkyBlock skyBlock, IslandsCommand islandsCommand) {
        super(skyBlock);

        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            try {
                for (SkyGamer skyGamer : skyGamerManager.getSkyGamers().values()) {
                    CraftSkyGamer craftSkyGamer = (CraftSkyGamer) skyGamer;
                    Map<String, Long> requests = craftSkyGamer.getRequests();
                    for (Map.Entry<String, Long> request : requests.entrySet()) {
                        String name = request.getKey();
                        Long time = request.getValue();
                        if (time + 122 * 1000 < System.currentTimeMillis())
                            requests.remove(name);

                        Player player = Bukkit.getPlayerExact(name);
                        if (player == null || !player.isOnline())
                            requests.remove(name);
                    }
                }

                for (String name : manager.getPlayerGuis().keySet()) {
                    Map<String, SkyBlockGui> guiList = manager.getPlayerGuis().get(name);
                    if (guiList == null)
                        continue;

                    guiList.values().forEach(skyBlockGui -> {
                        if (skyBlockGui != null)
                            skyBlockGui.update();
                    });
                }

                islandsCommand.update();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 5, 1L, TimeUnit.SECONDS); //1 секунда

        //создаем все ГУИ
        manager.createGui(ProfileGui.class);
        manager.createGui(RequestIslandGui.class);
        manager.createGui(FlagsGui.class);
        manager.createGui(GeneratorGui.class);
        manager.createGui(BiomeGui.class);
        manager.createGui(MembersGui.class);
        manager.createGui(UpgradeGui.class);
        manager.createGui(ChoisedGui.class);
        manager.createGui(IgnoreGui.class);
    }

    @EventHandler
    public void onRestart(IslandAsyncRemoveEvent e) {
        Island island = e.getIsland();
        List<Player> players = island.getOnlineMembers();
        players.forEach(manager::removeALL);
    }

    @EventHandler
    public void onCreate(IslandAsyncCreateEvent e) {
        manager.removeALL(e.getPlayer());
    }

    @EventHandler
    public void onRemoveMember(IslandAddMemberEvent e) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(e.getMemberID());
        if (gamer == null)
            return;

        Player player = gamer.getPlayer();
        manager.removeALL(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        manager.removeALL(player);
    }
}
