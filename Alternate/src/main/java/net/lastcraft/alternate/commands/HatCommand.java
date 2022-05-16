package net.lastcraft.alternate.commands;

import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class HatCommand extends AlternateCommand {

    public HatCommand(ConfigData configData) {
        super(configData, true, "hat");
        setMinimalGroup(configData.getInt("hatCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType() == Material.AIR) {
            gamer.sendMessageLocale("HAT_ERROR");
            return;
        }

        if (hand.getType().getMaxDurability() != 0) {
            gamer.sendMessageLocale( "HAT_ERROR");
            return;
        }

        PlayerInventory inv = player.getInventory();
        ItemStack head = inv.getHelmet();
        inv.setHelmet(hand);
        inv.setItemInMainHand(head);

        sendMessageLocale(gamerEntity,  "HAT");
    }
}
