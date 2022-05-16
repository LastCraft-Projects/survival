package net.lastcraft.alternate.commands.home;

import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.commands.AlternateCommand;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import org.bukkit.entity.Player;

public class SetHomeCommand extends AlternateCommand {

    public SetHomeCommand(ConfigData configData) {
        super(configData,true, "sethome", "createhome");
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] args) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();
        Language lang = gamerEntity.getLanguage();
        User user = USER_MANAGER.getUser(player);
        if (user == null)
            return;

        if (args.length < 1) {
            COMMANDS_API.notEnoughArguments(gamerEntity, "SETHOME_FORMAT");
            return;
        }

        int homes = user.getHomes().size();
        Group group = gamer.getGroup();

        Integer limit = configData.getHomeLimit(group);
        if (limit == null)
            limit = configData.getHomeLimit(Group.DEFAULT);

        if (homes == limit) {
            gamer.sendMessageLocale("HOME_WARP_LIMIT", limit, StringUtil.getCorrectWord(limit, "HOMES_1", lang));
            return;
        }

        String nameHome = args[0].toLowerCase();
        if (user.getHomes().containsKey(nameHome)) {
            gamer.sendMessageLocale("HOME_ERROR", nameHome);
            return;
        }

        sendMessageLocale(gamerEntity, "HOME_CREATE", nameHome);
        user.addHome(nameHome, player.getLocation());
    }
}
