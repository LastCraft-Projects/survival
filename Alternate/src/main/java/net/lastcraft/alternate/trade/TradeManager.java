package net.lastcraft.alternate.trade;

import net.lastcraft.alternate.Alternate;
import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.manager.UserManager;
import net.lastcraft.alternate.object.CraftUser;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.base.locale.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class TradeManager implements Listener {

    private final GamerManager gamerManager = LastCraft.getGamerManager();
    private final UserManager userManager = AlternateAPI.getUserManager();

    private final List<Trade> trades = new ArrayList<>();

    public TradeManager(Alternate alternate) {
        Bukkit.getPluginManager().registerEvents(this, alternate);
    }

    public void sendRequest(Player who, Player target) {
        BukkitGamer gamer = gamerManager.getGamer(who);
        BukkitGamer targetGamer = gamerManager.getGamer(target);
        if (gamer == null || targetGamer == null)
            return;
        CraftUser targetUser = (CraftUser) userManager.getUser(target);
        if (target == null)
            return;
        if (!targetUser.addTradeRequest(who)) {
            gamer.sendMessageLocale("CALL_ERROR");
            return;
        }
        Language lang = targetGamer.getLanguage();

        //todo сообщение о том, что вам отправили /trade (отправить target)
        //todo заявка активна 120 сек только
    }

    public void acceptRequest(Player who, Player target) {
        BukkitGamer gamer = gamerManager.getGamer(who);
        BukkitGamer targetGamer = gamerManager.getGamer(target);
        if (gamer == null || targetGamer == null)
            return;
        CraftUser targetUser = (CraftUser) userManager.getUser(target);
        if (target == null)
            return;
        if (targetUser.getTradeReguests().remove(who.getName()) == null) {
            gamer.sendMessageLocale("TRADE_ERROR2", target.getDisplayName());
            return;
        }
        Trade trade = new Trade(who, target, gamer.getLanguage(), targetGamer.getLanguage());
        if (!trade.isInit()) {
            gamer.sendMessageLocale("TRADE_BROKEN");
            targetGamer.sendMessageLocale("TRADE_BROKEN");
            return;
        }

        trades.add(trade);
    }

    //todo эвенты для работы гуи
}
