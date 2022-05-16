package net.lastcraft.anarchy.command;

import gnu.trove.map.TIntObjectMap;
import net.lastcraft.anarchy.gui.AnarchyMenuGui;
import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.locale.Language;
import org.bukkit.entity.Player;

public class AnarchystartCommand implements CommandInterface {

    private static final int LANGUAGE_DAFAULT_ID = Language.getDefault().getId();

    private final TIntObjectMap<AnarchyMenuGui> menus;

    public AnarchystartCommand(TIntObjectMap<AnarchyMenuGui> menus) {
        this.menus = menus;

        SpigotCommand command = COMMANDS_API.register("anarchystart", this);
        command.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        AnarchyMenuGui anarchyMenuGui = menus.get(gamer.getLanguage().getId());
        if (anarchyMenuGui != null) {
            anarchyMenuGui.open(player);
            return;
        }

        menus.get(LANGUAGE_DAFAULT_ID).open(player);
    }
}
