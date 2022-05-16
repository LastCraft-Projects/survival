package net.lastcraft.siteshop.commands;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.locale.Language;
import net.lastcraft.siteshop.SiteShop;
import net.lastcraft.siteshop.guis.BuySGui;
import net.lastcraft.siteshop.item.SSItemManager;
import org.bukkit.entity.Player;

public final class SiteShopCommand implements CommandInterface {

    private final TIntObjectMap<BuySGui> shopGuis = new TIntObjectHashMap<>();
    private final SSItemManager ssItemManager;

    public SiteShopCommand(SiteShop siteShop) {
        this.ssItemManager = siteShop.getItemManager();

        for (Language language : Language.values()) {
            BuySGui buySGui = new BuySGui(language);
            buySGui.setItems(ssItemManager);
            shopGuis.put(language.getId(), buySGui);
        }

        String nameCommand = "siteshop";
        COMMANDS_API.disableCommand(nameCommand);

        SpigotCommand command = COMMANDS_API.register(nameCommand, this);
        command.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        BuySGui buySGui = shopGuis.get(gamer.getLanguage().getId());
        if (buySGui != null) {
            buySGui.open(player);
            return;
        }

        shopGuis.get(Language.getDefault().getId()).open(player);
    }
}
