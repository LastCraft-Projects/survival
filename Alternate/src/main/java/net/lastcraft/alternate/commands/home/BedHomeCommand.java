package net.lastcraft.alternate.commands.home;

import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.commands.AlternateCommand;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.alternate.util.TeleportingUtil;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.Cooldown;
import net.lastcraft.base.util.TimeUtil;
import org.bukkit.entity.Player;

public class BedHomeCommand extends AlternateCommand {

    public BedHomeCommand(ConfigData configData) {
        super(configData, true, "home", "homes");
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        Language lang = gamerEntity.getLanguage();
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        if (Cooldown.hasCooldown(gamer, "bedHome") && !gamer.isDeveloper()) {
            long time = Cooldown.getSecondCooldown(gamer, "bedHome") * 1000 + System.currentTimeMillis();
            String finalString = TimeUtil.leftTime(lang, time, true);
            gamer.sendMessageLocale("COOLDOWN_2", finalString);
            return;
        }

        User user = USER_MANAGER.getUser(player);
        if (user == null)
            return;

        TeleportingUtil.teleport(player, this, () -> {
            if (user.teleport(user.getBedLocation())) {
                sendMessageLocale(gamerEntity,  "HOME_TO", "BED");
                Cooldown.addCooldown(gamer, "bedHome", 20 * 60 * 20);
            }
        });
    }
}
