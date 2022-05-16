package net.lastcraft.market.command;

import net.lastcraft.api.JSONMessageAPI;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.JsonBuilder;
import net.lastcraft.market.shop.ShopGui;
import net.lastcraft.market.shop.ShopManager;
import org.bukkit.entity.Player;

public final class ShopCommand implements CommandInterface {

    private final JSONMessageAPI jsonMessageAPI = LastCraft.getJsonMessageAPI();

    private final ShopManager shopManager;

    public ShopCommand(ShopManager shopManager) {
        this.shopManager = shopManager;

        SpigotCommand spigotCommand = COMMANDS_API.register("shop", this, "магазин");
        spigotCommand.setOnlyPlayers(true);
    }

    public void execute(GamerEntity gamerEntity, String cmd, String[] args) {
        Language lang = gamerEntity.getLanguage();
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        if (args.length < 2 && !gamer.isDiamond()) {
            gamerEntity.sendMessageLocale("NO_PERMS_GROUP", Group.DIAMOND.getNameEn());
            return;
        }

        if (args.length > 1 && !args[1].equalsIgnoreCase("npckek")) {
            gamerEntity.sendMessageLocale("NO_PERMS_GROUP", Group.DIAMOND.getNameEn());
            return;
        }

        if (args.length < 1) {
            COMMANDS_API.notEnoughArguments(gamerEntity, "SHOP_FORMAT");
            JsonBuilder names = new JsonBuilder();
            names.addText(lang.getMessage("SHOP_NAMES"));
            int amountShop = 0;
            for (String name : shopManager.getShopsNames()) {
                amountShop++;
                names.addRunCommand("§a" + name, "/shop " + name,
                        lang.getMessage("SHOP_HOVER_TEST"));
                if (amountShop < shopManager.getShopsNames().size())
                    names.addText("§f, ");

            }
            jsonMessageAPI.send(player, names);
            return;
        }

        String guiName = args[0].toLowerCase();
        ShopGui shopGui = shopManager.getGuis().get(guiName + lang);
        if (shopGui == null) {
            gamer.sendMessageLocale("SHOP_NOT_FOUND", guiName);
            return;
        }

        shopGui.getDInventory().openInventory(player);
    }
}
