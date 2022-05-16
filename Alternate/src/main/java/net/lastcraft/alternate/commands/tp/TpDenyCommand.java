package net.lastcraft.alternate.commands.tp;

import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.alternate.object.CraftUser;
import net.lastcraft.api.player.BukkitGamer;
import org.bukkit.entity.Player;

public class TpDenyCommand extends TpaCommand {

    public TpDenyCommand(ConfigData configData) {
        super(configData, "tpdeny", "tpno");
    }

    @Override
    protected void accept(Player sender, Player who) {
        User user = USER_MANAGER.getUser(sender);
        if (user == null)
            return;

        ((CraftUser)user).getCallReguests().remove(who.getName());
        BukkitGamer gamerSender = GAMER_MANAGER.getGamer(sender);
        BukkitGamer gamerWho = GAMER_MANAGER.getGamer(who);

        if (gamerSender != null)
            sendMessageLocale(gamerSender, "TPDENY", who.getDisplayName());

        if (gamerWho != null)
            sendMessageLocale(gamerWho, "TPDENY_YOU", sender.getDisplayName());

    }
}
