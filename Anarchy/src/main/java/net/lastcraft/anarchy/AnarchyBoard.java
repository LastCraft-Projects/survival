package net.lastcraft.anarchy;

import com.sk89q.worldguard.bukkit.BukkitPlayer;
import com.sk89q.worldguard.bukkit.WGBukkit;
import net.lastcraft.anarchy.stats.StatsLoader;
import net.lastcraft.anarchy.stats.StatsPlayer;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.scoreboard.Board;
import net.lastcraft.api.scoreboard.ScoreBoardAPI;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.market.api.MarketAPI;
import net.lastcraft.market.api.MarketPlayer;
import net.lastcraft.market.api.MarketPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class AnarchyBoard {

    private static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();
    private static final ScoreBoardAPI SCORE_BOARD_API = LastCraft.getScoreBoardAPI();
    private static final MarketPlayerManager MARKET_PLAYER_MANAGER = MarketAPI.getMarketPlayerManager();

    private Board board;
    private StatsPlayer statsPlayer;
    private BukkitGamer gamer;
    private BukkitPlayer bukkitPlayer;

    public AnarchyBoard(Player player) {
        gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        MarketPlayer marketPlayer = MARKET_PLAYER_MANAGER.getMarketPlayer(player);
        if (marketPlayer == null)
            return;

        bukkitPlayer = new BukkitPlayer(WGBukkit.getPlugin(), player);
        Language lang = gamer.getLanguage();

        Group group = gamer.getGroup();
        String groupName = (lang == Language.RUSSIAN ? group.getName() : group.getNameEn());

        statsPlayer = StatsLoader.getData(gamer.getPlayerID());

        board = SCORE_BOARD_API.createBoard(); //todo локализация
        board.setDynamicDisplayName("Anarchy");
        board.setLine(13, StringUtil.getLineCode(12));
        board.setLine(12, "§f" + lang.getMessage("BOARD_GROUP") + ": " + groupName);
        board.setLine(10, StringUtil.getLineCode(10));
        board.setLine(9, "§7Статистика режима:");
        board.updater(() -> {
            double balance = marketPlayer.getMoney();
            board.setDynamicLine(11, lang.getMessage("BOARD_ONLINE") + ": §a",
                    String.valueOf(Bukkit.getOnlinePlayers().size()));

            int amount = 0;
            for (World world : Bukkit.getWorlds())
                amount += WGBukkit.getRegionManager(world).getRegionCountOfPlayer(bukkitPlayer);
            board.setDynamicLine(8, " Кол-во приватов: §a", StringUtil.getNumberFormat(amount));

            board.setDynamicLine(7, " " + lang.getMessage("BOARD_BALANCE") + ": §a",
                    StringUtil.getNumberFormat(balance));
            board.setDynamicLine(6, " " + lang.getMessage("BOARD_KILLS") + ": §c",
                    StringUtil.getNumberFormat(getStatsPlayer().getKills()));
            board.setDynamicLine(5, " " + lang.getMessage("BOARD_DEATH") + ": §e",
                    StringUtil.getNumberFormat(getStatsPlayer().getDeath()));
            //String region = "§c" + Localization.getMessage(lang, "REGION_NOT_FOUND");
            //for  (ProtectedRegion r : WGBukkit.getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation()))
            //    region = "§a" + r.getId();
            //
            //if (region.length() > 16) {
            //    region = region.substring(0, 15);
            //}
            //board.setDynamicLine(1, Localization.getMessage(lang, "BOARD_REGION") + ": ", region);
        }, 40);
        board.setLine(4, StringUtil.getLineCode(4));
        board.setLine(3, StringUtil.getLineCode(3) + "§f" + lang.getMessage("BOARD_VK")
                + ": §7vk.com/lastcraft");
        board.setLine(2, StringUtil.getLineCode(2) + "§f" + lang.getMessage("BOARD_SHOP")
                + ": §7last-craft.com");
        board.setLine(1, StringUtil.getLineCode(1) + "§f" + lang.getMessage("BOARD_DISCORD")
                + ": §7vk.cc/7BsqTk");

        board.showTo(player);
    }

    public void remove() {
        if (gamer == null)
            return;

        StatsLoader.saveData(gamer.getPlayerID(), statsPlayer.getKills(), statsPlayer.getDeath());
        board.remove();
    }

    public StatsPlayer getStatsPlayer() {
        return statsPlayer;
    }
}
