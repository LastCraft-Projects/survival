package net.lastcraft.alternate.commands.warp;

import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.Warp;
import net.lastcraft.alternate.api.manager.WarpManager;
import net.lastcraft.alternate.commands.AlternateCommand;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.InventoryAPI;
import net.lastcraft.api.inventory.MultiInventory;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.sound.SoundAPI;
import net.lastcraft.api.sound.SoundType;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.gamer.IBaseGamer;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class PlayerWarpCommand extends AlternateCommand {

    private static final InventoryAPI INVENTORY_API = LastCraft.getInventoryAPI();
    private static final SoundAPI SOUND_API = LastCraft.getSoundAPI();
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    private final WarpManager warpManager = AlternateAPI.getWarpManager();
    private final GamerManager gamerManager = LastCraft.getGamerManager();

    public PlayerWarpCommand(ConfigData configData) {
        super(configData, true, "playerwarp", "playerwarps");
        spigotCommand.setCooldown(20, "playerwarpcooldown");
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] args) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        if (args.length < 1) {
            COMMANDS_API.notEnoughArguments(gamerEntity, "PLAYER_WARP_FORMAT");
            return;
        }

        BukkitUtil.runTaskAsync(() -> {
            IBaseGamer owner = gamerManager.getOrCreate(args[0]);
            if (owner == null) {
                COMMANDS_API.playerNeverPlayed(gamerEntity, args[0]);
                return;
            }

            List<Warp> warpList = warpManager.getWarps(owner.getPlayerID());
            PlayerWarpGui playerWarpGui = new PlayerWarpGui(player, owner, gamer.getLanguage());
            playerWarpGui.initGui(warpList, gamer);
            BukkitUtil.runTask(() -> playerWarpGui.open(player));
        });
    }

    private final class PlayerWarpGui {

        private final MultiInventory inventory;
        private final IBaseGamer owner;
        private final Language lang;

        PlayerWarpGui(Player player, IBaseGamer owner, Language language) {
            this.owner = owner;
            this.lang = language;
            this.inventory = INVENTORY_API.createMultiInventory(player,
                    language.getMessage("PLAYER_WARP_GUI_NAME", owner.getName()), 5);
        }

        void initGui(List<Warp> warps, BukkitGamer mainGamer) {
            if (warps.isEmpty()) {
                inventory.setItem(0, 22, INVENTORY_API.createItem(ItemUtil.getBuilder(Material.GLASS_BOTTLE)
                        .setName(lang.getMessage("PLAYER_WARP_ITEM_EMPTY_NAME"))
                        .setLore(lang.getList("PLAYER_WARP_ITEM_EMPTY_LORE", owner.getDisplayName()))
                        .build(), (player, clickType, slot1) -> SOUND_API.play(player, SoundType.TELEPORT)));
                return;
            }

            boolean friend = mainGamer.getFriends().contains(owner.getPlayerID());

            int slot = 10;
            int page = 0;
            for (Warp warp : warps){
                if (warp.isPrivate() && !friend)  {
                    continue;
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(warp.getDate().getTime());
                String date = SIMPLE_DATE_FORMAT.format(calendar.getTime());

                String name = warp.getName();
                Location location = warp.getLocation();
                int players = warp.getNearbyPlayers(30).size();

                inventory.setItem(page, slot, INVENTORY_API.createItem(ItemUtil.getBuilder(warp.getIcon())
                        .setName("§a" + name)
                        .setLore(lang.getList( "WARP_ITEM_LORE",
                                warp.getNameOwner(),
                                date,
                                location.getWorld().getName(),
                                String.valueOf((int) location.getX()),
                                String.valueOf((int) location.getY()),
                                String.valueOf((int) location.getZ()),
                                String.valueOf(players),
                                StringUtil.getCorrectWord(players, "PLAYERS_1", lang)))
                        .build(), (player, clickType, i) -> {
                    player.chat("/warp " + name);
                    SOUND_API.play(player, SoundType.SELECTED);
                    player.closeInventory();
                }));

                slot++;

                if ((slot - 8) % 9 == 0)
                    slot += 2;

                if (slot >= 35) {
                    slot = 10;
                    page++;
                }
            }

            INVENTORY_API.pageButton(lang, page + 1, inventory, 38, 42);
        }

        void open(Player player) {

            if (inventory != null) {
                inventory.openInventory(player);
            }
        }
    }
}
