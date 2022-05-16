package net.lastcraft.alternate.commands.get;

import com.google.common.collect.ImmutableList;
import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.Kit;
import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.manager.KitManager;
import net.lastcraft.alternate.commands.AlternateCommand;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.alternate.gui.KitGui;
import net.lastcraft.api.command.CommandTabComplete;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.TimeUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class KitCommand extends AlternateCommand implements CommandTabComplete {

    public KitCommand(ConfigData configData) {
        super(configData, true, "kit", "kits");
        spigotCommand.setCommandTabComplete(this);
    }

    private final KitManager kitManager = AlternateAPI.getKitManager();

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] args) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Language lang = gamerEntity.getLanguage();
        Player player = gamer.getPlayer();
        User user = USER_MANAGER.getUser(player);

        if (args.length == 0) {
            KitGui kitGui = GUI_MANAGER.getGui(KitGui.class, player);
            if (kitGui == null) {
                return;
            }

            kitGui.open();

            return;
        }

        String nameKit = args[0];
        Kit kit = kitManager.getKit(nameKit);
        if (kit == null) {
            gamerEntity.sendMessageLocale("KIT_NOT_FOUND", nameKit);
            return;
        }

        if (user.isCooldown(kit)) {
            long time = user.getCooldown(kit) * 1000 + System.currentTimeMillis();
            String finalString = TimeUtil.leftTime(lang, time, true);
            gamer.sendMessageLocale("COOLDOWN_2", finalString);
            return;
        }

        if (kit.getDefaultGroup() != null && kit.getDefaultGroup() != gamer.getGroup()) {
            gamerEntity.sendMessageLocale("NO_PERMS_GROUP", kit.getDefaultGroup().getNameEn());
            return;
        } else if (kit.getDefaultGroup() == null && kit.getMinimalGroup().getLevel() > gamer.getGroup().getLevel()) {
            gamerEntity.sendMessageLocale("NO_PERMS_GROUP", kit.getMinimalGroup().getNameEn());
            return;
        }

        sendMessageLocale(gamerEntity, "KIT_SELECT", kit.getName());
        Inventory inventory = player.getInventory();
        kit.getItems().forEach(inventory::addItem);
        user.addKit(kit);
    }

    @Override
    public List<String> getComplete(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        User user = USER_MANAGER.getUser(gamerEntity.getName());
        if (user == null || strings.length > 1) {
            return ImmutableList.of();
        }

        List<String> availableKits = new ArrayList<>();
        for (Kit kit : kitManager.getKits().values()) {
            if (kit.isStart()) {
                continue;
            }

            if (kit.getDefaultGroup() != null && kit.getDefaultGroup() == gamer.getGroup()) {
                availableKits.add(kit.getName().toLowerCase());
                continue;
            }

            if (kit.getDefaultGroup() == null && kit.getMinimalGroup().getLevel() <= gamer.getGroup().getLevel()) {
                availableKits.add(kit.getName().toLowerCase());
            }
        }

        return COMMANDS_API.getCompleteString(availableKits, strings);
    }
}
