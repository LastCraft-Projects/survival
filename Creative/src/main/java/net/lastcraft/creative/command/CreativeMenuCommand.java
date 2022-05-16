package net.lastcraft.creative.command;

import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.locale.Language;
import net.lastcraft.creative.gui.CreativeMenuGui;
import org.bukkit.entity.Player;

import java.util.Map;

public class CreativeMenuCommand implements CommandInterface {

    private final Map<Integer, CreativeMenuGui> menus;

    public CreativeMenuCommand(Map<Integer, CreativeMenuGui> menus) {
        this.menus = menus;
        SpigotCommand spigotCommand = COMMANDS_API.register("cm", this, "creativemenu",
                "креатив", "creative");
        spigotCommand.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();
        Language lang = gamerEntity.getLanguage();

        menus.getOrDefault(lang.getId(), menus.get(Language.getDefault().getId()))
                .open(player);
    }
}
