package net.lastcraft.alternate.commands.info;

import net.lastcraft.alternate.commands.AlternateCommand;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ListCommand extends AlternateCommand {

    public ListCommand(ConfigData configData) {
        super(configData, false, "list");
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] strings) {
        StringBuilder best = new StringBuilder();
        StringBuilder players = new StringBuilder();
        StringBuilder donate = new StringBuilder();
        StringBuilder staff = new StringBuilder();

        int online = Bukkit.getOnlinePlayers().size();
        for (Player player : Bukkit.getOnlinePlayers()) {
            BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
            if (gamer == null)
                continue;
            String displayName = player.getDisplayName();
            if (gamer.isStaff()) {
                staff.append(displayName).append("§f, ");
                continue;
            }
            if (gamer.getGroup() == Group.DEFAULT) {
                players.append(displayName).append("§f, ");
                continue;
            }
            if (gamer.getGroup() == Group.SHULKER || gamer.getGroup() == Group.YOUTUBE) {
                best.append(displayName).append("§f, ");
                continue;
            }
            donate.append(displayName).append("§f, ");
        }
        Language lang = gamerEntity.getLanguage();
        gamerEntity.sendMessagesLocale("LIST",
                "§a" + online + " §f" + StringUtil.getCorrectWord(online, "PLAYERS_1", lang),
                correct(best, lang),
                correct(players, lang),
                correct(donate, lang),
                correct(staff, lang)
        );

    }

    private String correct(StringBuilder stringBuilder, Language lang) {
        String list = String.valueOf(stringBuilder);
        if (list.length() == 0)
            return lang.getMessage("LIST_EMPTY");

        return list.substring(0, list.length() - 4);
    }
}
