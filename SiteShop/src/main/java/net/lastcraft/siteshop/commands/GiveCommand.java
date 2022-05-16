package net.lastcraft.siteshop.commands;

import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import net.lastcraft.siteshop.SiteShop;
import net.lastcraft.siteshop.guis.SSGui;

public final class GiveCommand implements CommandInterface {

    private final SiteShop siteShop;

    public GiveCommand(SiteShop siteShop) {
        this.siteShop = siteShop;

        SpigotCommand command = COMMANDS_API.register("give", this, "выдать");
        command.setOnlyPlayers(true);
        command.setCooldown(40, this.toString());
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;

        if (strings.length > 0 && gamer.isDeveloper()) {
            if (strings[0].equalsIgnoreCase("reload")) {
                siteShop.reloadConfig();
                gamer.sendMessage("Конфиг плагина SiteShop перезагружен, вещи обнавлены");
            }
        }

        BukkitUtil.runTaskLater(20L, () -> {
            SSGui ssGui = new SSGui(siteShop.getItemManager(), gamer);
            ssGui.loadFromMysql();
            ssGui.updateGui();
            BukkitUtil.runTask(ssGui::open);
        });
    }
}
