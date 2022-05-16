package net.lastcraft.alternate.commands.tp;

import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.events.UserTeleportByCommandEvent;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.alternate.object.CraftUser;
import net.lastcraft.alternate.util.TeleportingUtil;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import org.bukkit.entity.Player;

public class TpacceptCommand extends TpaCommand {

    public TpacceptCommand(ConfigData configData) {
        super(configData, "tpaccept", "tpyes");
    }

    @Override
    protected void accept(Player sender, Player who) {
        User userWho = USER_MANAGER.getUser(who);
        CraftUser senderUser = (CraftUser) USER_MANAGER.getUser(sender);
        BukkitGamer senderGamer = GAMER_MANAGER.getGamer(sender);
        BukkitGamer gamerWho = GAMER_MANAGER.getGamer(who);

        UserTeleportByCommandEvent event = new UserTeleportByCommandEvent(userWho,
                UserTeleportByCommandEvent.Command.TPACCEPT,
                sender.getLocation(),
                sender);
        BukkitUtil.callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        sendMessageLocale(senderGamer, "TPACCEPT_SENDER", who.getDisplayName());
        sendMessageLocale(gamerWho, "TPACCEPT_WHO", sender.getDisplayName());

        senderUser.getCallReguests().remove(who.getName());

        TeleportingUtil.teleport(who, this, () -> userWho.teleport(sender.getLocation()));
    }
}
