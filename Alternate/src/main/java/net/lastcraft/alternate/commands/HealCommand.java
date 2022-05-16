package net.lastcraft.alternate.commands;

import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.Cooldown;
import net.lastcraft.base.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class HealCommand extends AlternateCommand {

    public HealCommand(ConfigData configData) {
        super(configData,true, "heal");
        setMinimalGroup(configData.getInt("healCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player sender = gamer.getPlayer();

        if (strings.length == 1 && gamer.getGroup() == Group.ADMIN) {
            String name = strings[0];
            Player player = Bukkit.getPlayer(name);
            if (player == null || !player.isOnline()) {
                COMMANDS_API.playerOffline(gamerEntity, name);
                return;
            }
            heal(player);
            send("HEAL", gamer, player.getDisplayName());
            return;
        }

        int level = configData.getInt("ignoreCooldownHeal");
        if (Cooldown.hasCooldown(sender.getName(), getCooldownType()) && gamer.getGroup().getLevel() < level) {
            Language lang = gamer.getLanguage();
            int time = Cooldown.getSecondCooldown(sender.getName(), getCooldownType());
            gamerEntity.sendMessageLocale("COOLDOWN", String.valueOf(time),
                    StringUtil.getCorrectWord(time, "TIME_SECOND_1", lang));
        } else {
            if (gamer.getGroup().getLevel() < level)
                Cooldown.addCooldown(sender.getName(), getCooldownType(), 5 * 60 * 20);

            heal(sender);
        }

    }

    private void heal(Player player) {
        if (player.getHealth() == 0)
            return;

        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setFireTicks(0);
        for (PotionEffect effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        send("HEAL", gamer, null);
    }
}
