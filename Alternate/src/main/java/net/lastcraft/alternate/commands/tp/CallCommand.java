package net.lastcraft.alternate.commands.tp;

import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.commands.AlternateCommand;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.alternate.object.CraftUser;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.api.util.ChatUtil;
import net.lastcraft.base.locale.Language;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CallCommand extends AlternateCommand{

    public CallCommand(ConfigData configData) {
        super(configData, true, "call", "tpa");
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player sender = gamer.getPlayer();

        if (strings.length < 1) {
            COMMANDS_API.notEnoughArguments(gamerEntity, "CALL_FORMAT");
            return;
        }

        Player otherPlayer = Bukkit.getPlayer(strings[0]);
        if (otherPlayer == null) {
            COMMANDS_API.playerOffline(gamerEntity, strings[0]);
            return;
        }

        if (otherPlayer.getName().equalsIgnoreCase(gamer.getName())) {
            gamer.sendMessageLocale("CALL_YOU");
            return;
        }

        User otherUser = USER_MANAGER.getUser(otherPlayer);
        BukkitGamer otherGamer = GAMER_MANAGER.getGamer(otherPlayer);
        if (otherGamer == null || otherUser == null)
            return;

        Language otherLang = otherGamer.getLanguage();
        if (!otherUser.isTpToggle() && !otherGamer.getFriends().contains(gamer.getPlayerID()) && !gamer.isModerator()) {
            gamer.sendMessageLocale("TPTOGGLE", otherPlayer.getDisplayName());
            return;
        }

        CraftUser craftUser = (CraftUser) otherUser;
        if (!craftUser.addCallRequest(sender)) {
            gamer.sendMessageLocale("CALL_ERROR");
            return;
        }

        otherGamer.sendMessage("");
        otherGamer.sendMessageLocale("CALL_INVITE_1", sender.getDisplayName());

        BaseComponent call_invite_2 = new TextComponent(otherLang.getMessage( "CALL_INVITE_2"));
        BaseComponent[] showTextInvite2 = new BaseComponent[]{ChatUtil.getComponentFromList(otherLang.getList("CALL_HOVER_ACCEPT", sender.getDisplayName()))};
        BaseComponent hover2 = new TextComponent("§c/tpaccept " + sender.getName() + otherLang.getMessage("HOVER"));
        hover2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, showTextInvite2));
        hover2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + sender.getName()));
        call_invite_2.addExtra(hover2);
        otherGamer.sendMessage(call_invite_2);

        BaseComponent call_invite_3 = new TextComponent(otherLang.getMessage( "CALL_INVITE_3"));
        BaseComponent[] showTextInvite3 = new BaseComponent[]{ChatUtil.getComponentFromList(otherLang.getList("CALL_HOVER_IGNORE", sender.getDisplayName()))};
        BaseComponent hover3 = new TextComponent("§c/tpdeny " + sender.getName() + otherLang.getMessage("HOVER"));
        hover3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, showTextInvite3));
        hover3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny " + sender.getName()));
        call_invite_3.addExtra(hover3);
        otherGamer.sendMessage(call_invite_3);

        otherGamer.sendMessageLocale("CALL_INVITE_4");
        otherGamer.sendMessage("");

        sendMessageLocale(gamerEntity, "CALL", otherPlayer.getDisplayName());
    }
}