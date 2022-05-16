package net.lastcraft.skyblock.gui.guis;

import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.util.Cooldown;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import net.lastcraft.skyblock.api.SkyBlockGui;
import net.lastcraft.skyblock.api.event.IslandAsyncCreateEvent;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.island.IslandType;
import net.lastcraft.skyblock.gui.AcceptGui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ChoisedGui extends SkyBlockGui {

    private static final String KEY = "SKYBLOCK_CHOISED_GUI_NAME";

    private boolean reset = false;

    public ChoisedGui(Player player) {
        super(player, "");
        inventory = API.createInventory(player, lang.getMessage(KEY), 5);
    }

    public void setReset(boolean reset) {
        this.reset = reset;
    }

    @Override
    protected void setItems(Player player) {
        BukkitGamer mainGamer = GAMER_MANAGER.getGamer(player);
        if (mainGamer == null)
            return;

        int slot = 10;
        for (IslandType type : IslandType.values()) {
            ItemStack item = ItemUtil.getBuilder(type.getItem())
                    .setName("§e" + lang.getMessage(type.getKeyName()))
                    .setLore(lang.getList(type.getKeyLore()))
                    .build();

            inventory.setItem(slot, API.createItem(item, (clicker, clickType, slotClicked) -> {
                if (Cooldown.hasCooldown(clicker.getName(), "resetIsland")
                        || Cooldown.hasCooldown(clicker.getName(), "startIsland"))
                    return;
                if (reset) {
                    Island island = ISLAND_MANAGER.getIsland(clicker);
                    if (island == null)
                        return;
                    new AcceptGui(clicker, AcceptGui.Type.RESET).open(() -> {
                        island.reset(type);
                        setReset(false);
                        Cooldown.addCooldown(clicker.getName(), "resetIsland", 2 * 60 * 60 * 20L);
                        mainGamer.sendMessageLocale("ISLAND_RESET");
                    }, null);
                    return;
                }

                BukkitUtil.runTaskAsync(() -> {
                    Island newIsland = ISLAND_MANAGER.createIsland(clicker, type);
                    IslandAsyncCreateEvent event = new IslandAsyncCreateEvent(clicker, newIsland);
                    BukkitUtil.callEvent(event);
                    Cooldown.addCooldown(clicker.getName(), "startIsland", 10 * 20L);
                });
                clicker.closeInventory();
            }));

            slot++;
            if ((slot - 1) % 8 == 0)
                slot += 2;
        }
    }
}
