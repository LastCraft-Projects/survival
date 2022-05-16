package net.lastcraft.skyblock.command;

import com.google.common.collect.ImmutableList;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.CommandTabComplete;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.manager.GuiManager;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.base.locale.Language;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.SkyBlockGui;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.manager.IslandManager;
import net.lastcraft.skyblock.api.manager.SkyGamerManager;
import net.lastcraft.skyblock.craftisland.CraftSkyGamer;
import net.lastcraft.skyblock.gui.guis.RequestIslandGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public abstract class RequestCommand implements CommandInterface, CommandTabComplete {

    private final GuiManager<SkyBlockGui> guiManager = SkyBlockAPI.getSkyGuiManager();
    final static IslandManager ISLAND_MANAGER = SkyBlockAPI.getIslandManager();
    final static SkyGamerManager MANAGER = SkyBlockAPI.getSkyGamerManager();
    final static GamerManager GAMER_MANAGER = LastCraft.getGamerManager();

    RequestCommand(String commandName, String... aliases) {
        SpigotCommand command = COMMANDS_API.register(commandName, this, aliases);
        command.setOnlyPlayers(true);
        command.setCooldown(25, "request_cooldown");
        command.setCommandTabComplete(this);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] args) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Island island = ISLAND_MANAGER.getIsland(gamer);

        if (island != null) {
            gamer.sendMessageLocale("ISLAND_REQUESTS_ERROR");
            return;
        }

        Player sender = gamer.getPlayer();
        CraftSkyGamer skyGamer = (CraftSkyGamer) MANAGER.getSkyGamer(sender);
        if (skyGamer == null)
            return;

        Map<String, Long> playerRequest = skyGamer.getRequests();
        Player player = null;
        for (String name : playerRequest.keySet()) {
            player = Bukkit.getPlayer(name);
            if (player == null || !player.isOnline()) {
                playerRequest.remove(name);
            }
        }

        if (playerRequest.size() == 1) {
            if (player != null) {
                accept(sender, player);
            }
            return;
        } else if (playerRequest.size() == 0) {
            sendMessage(gamerEntity, "TPACCEPT_ERROR");
            return;
        } else if (args.length == 0){
            RequestIslandGui gui = guiManager.getGui(RequestIslandGui.class, sender);
            if (gui != null) {
                gui.open();
            }
            return;
        }

        String name = args[0];
        player = Bukkit.getPlayer(name);
        if (player == null) {
            COMMANDS_API.playerOffline(gamerEntity, name);
            return;
        }

        if (!playerRequest.containsKey(name)) {
            sendMessage(gamerEntity, "TPACCEPT_ERROR_PLAYER", name);
            return;
        }
        accept(sender, player);
    }

    static void sendMessage(GamerEntity gamerEntity, String key, Object... replaced) {
        Language language = gamerEntity.getLanguage();
        gamerEntity.sendMessage(SkyBlockAPI.getPrefix() + language.getMessage(key, replaced));
    }

    @Override
    public final List<String> getComplete(GamerEntity gamerEntity, String s, String[] strings) {
        CraftSkyGamer skyGamer = (CraftSkyGamer) MANAGER.getSkyGamer(gamerEntity.getName());
        if (skyGamer == null || strings.length > 1) {
            return ImmutableList.of();
        }

        return COMMANDS_API.getCompleteString(skyGamer.getRequests().keySet(), strings);
    }

    abstract void accept(Player sender, Player who);
}
