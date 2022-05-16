package net.lastcraft.skyblock.command;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.manager.GuiManager;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.SkyBlockGui;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.manager.IslandManager;
import net.lastcraft.skyblock.gui.guis.ChoisedGui;
import net.lastcraft.skyblock.module.HomeModule;
import org.bukkit.entity.Player;

public final class HomeCommand implements CommandInterface {
    private final IslandManager manager = SkyBlockAPI.getIslandManager();
    private static final GuiManager<SkyBlockGui> SKY_GUI_MANAGER = SkyBlockAPI.getSkyGuiManager();

    public HomeCommand() {
        SpigotCommand command = LastCraft.getCommandsAPI().register("home", this,
                "go", "домой", "start");
        command.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();
        Island island = manager.getIsland(player);

        toHome(player, gamer, island);
    }

    static void toHome(Player player, BukkitGamer gamer, Island island) {
        if (island == null) {
            ChoisedGui choisedGui = SKY_GUI_MANAGER.getGui(ChoisedGui.class, player);
            if (choisedGui != null)
                choisedGui.open();
            return;
        }

        HomeModule homeModule = island.getModule(HomeModule.class);
        if (homeModule == null) {
            return;
        }

        homeModule.teleport(player);
    }
}
