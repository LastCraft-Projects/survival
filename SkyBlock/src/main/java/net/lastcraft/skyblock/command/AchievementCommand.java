package net.lastcraft.skyblock.command;

import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.dartaapi.achievements.gui.AchievementGui;
import net.lastcraft.dartaapi.achievements.manager.AchievementManager;
import org.bukkit.entity.Player;

public final class AchievementCommand implements CommandInterface {

    private final AchievementManager achievementManager;
    private final SpigotCommand command;

    @Deprecated //todo удалить
    public AchievementCommand(AchievementManager achievementManager) {
        this.achievementManager = achievementManager;

        this.command = COMMANDS_API.register("achievement", this, "ach");
        this.command.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        AchievementGui achievementGui = new AchievementGui(player, "ISLAND_ACHIEVEMENT_GUI_NAME");
        achievementGui.setItems(achievementManager);
        achievementGui.addBackItem((clicker, clickType, slot) -> clicker.chat("/is"));
        achievementGui.open();
    }
}
