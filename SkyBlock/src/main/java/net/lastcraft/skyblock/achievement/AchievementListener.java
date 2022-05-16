package net.lastcraft.skyblock.achievement;

import net.lastcraft.base.gamer.IBaseGamer;
import net.lastcraft.dartaapi.achievements.achievement.Achievement;
import net.lastcraft.dartaapi.achievements.achievement.AchievementPlayer;
import net.lastcraft.dartaapi.achievements.achievement.AchievementPlayerData;
import net.lastcraft.dartaapi.achievements.manager.AchievementManager;
import net.lastcraft.skyblock.SkyBlock;
import net.lastcraft.skyblock.achievement.type.IslandBreakAchievement;
import net.lastcraft.skyblock.achievement.type.IslandBreedAchievement;
import net.lastcraft.skyblock.achievement.type.IslandCraftAchievement;
import net.lastcraft.skyblock.achievement.type.IslandFurnaceAchievement;
import net.lastcraft.skyblock.api.event.IslandAsyncCreateEvent;
import net.lastcraft.skyblock.api.event.IslandAsyncResetEvent;
import net.lastcraft.skyblock.api.event.absract.IslandListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class AchievementListener extends IslandListener {

    private final AchievementManager achievementManager;

    public AchievementListener(SkyBlock skyBlock) {
        super(skyBlock);

        this.achievementManager = skyBlock.getAchievementManager();
    }

    @EventHandler
    public void onCreateIsland(IslandAsyncCreateEvent e) {
        complete(e.getPlayer(), IslandAchievements.START);
    }

    @EventHandler
    public void onRestartIsland(IslandAsyncResetEvent e) {
        IBaseGamer owner = e.getIsland().getOwner();
        Player player = Bukkit.getPlayerExact(owner.getName());
        complete(player, IslandAchievements.RESTART);
    }

    private void complete(Player player, IslandAchievements islandAchievements) {
        if (player == null || !player.isOnline())
            return;

        AchievementPlayer achievementPlayer = achievementManager.getPlayerManager().getAchievementPlayer(player);
        if (achievementPlayer == null)
            return;

        Achievement achievement = achievementManager.getAchievement(islandAchievements.getId());
        if (achievementPlayer.hasAchievement(achievement))
            return;

        achievementPlayer.complete(achievement);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreeding(EntityBreedEvent e) {
        LivingEntity breeder = e.getBreeder();
        if (!(breeder instanceof Player))
            return;

        Player player = (Player) breeder;

        AchievementPlayer achievementPlayer = achievementManager.getPlayerManager().getAchievementPlayer(player);
        if (achievementPlayer == null)
            return;

        IslandBreedAchievement achievement = IslandAchievements.getBreedAchievement(e.getEntity().getType());
        if (achievement == null || achievementPlayer.hasAchievement(achievement))
            return;

        AchievementPlayerData achievementPlayerData = achievementPlayer.getAchievementsData(achievement);
        if (achievementPlayerData == null)
            return;

        int cache = achievementPlayerData.getLocalInfo().getOrDefault(IslandBreedAchievement.KEY, 0);
        cache++;

        achievementPlayerData.addLocalInfo(IslandBreedAchievement.KEY, cache);
        if (cache < achievement.getAmount())
            return;

        achievementPlayer.complete(achievement);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFurnace(FurnaceExtractEvent e) {
        Player player = e.getPlayer();
        Material material = e.getItemType();

        AchievementPlayer achievementPlayer = achievementManager.getPlayerManager().getAchievementPlayer(player);
        if (achievementPlayer == null)
            return;

        IslandFurnaceAchievement achievement = IslandAchievements.getFurnaceAchievement(material);
        if (achievement == null || achievementPlayer.hasAchievement(achievement))
            return;

        AchievementPlayerData achievementPlayerData = achievementPlayer.getAchievementsData(achievement);
        if (achievementPlayerData == null)
            return;

        int cache = achievementPlayerData.getLocalInfo().getOrDefault(IslandFurnaceAchievement.KEY, 0);
        cache += e.getItemAmount();

        achievementPlayerData.addLocalInfo(IslandFurnaceAchievement.KEY, cache);
        if (cache < achievement.getAmount())
            return;

        achievementPlayer.complete(achievement);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCraft(CraftItemEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack itemStack = e.getRecipe().getResult();

        AchievementPlayer achievementPlayer = achievementManager.getPlayerManager().getAchievementPlayer(player);
        if (achievementPlayer == null)
            return;

        IslandCraftAchievement achievement = IslandAchievements.getCraftAchievement(itemStack);
        if (achievement == null || achievementPlayer.hasAchievement(achievement))
            return;

        AchievementPlayerData achievementPlayerData = achievementPlayer.getAchievementsData(achievement);
        if (achievementPlayerData == null)
            return;

        int cache = achievementPlayerData.getLocalInfo().getOrDefault(IslandCraftAchievement.KEY, 0);
        cache += getCraftAmount(e);

        achievementPlayerData.addLocalInfo(IslandCraftAchievement.KEY, cache);
        if (cache < achievement.getAmount())
            return;

        achievementPlayer.complete(achievement);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onGenerator(BlockBreakEvent e) {
        if (!e.getBlock().hasMetadata("generator"))
            return;

        complete(e.getPlayer(), IslandAchievements.GENERATOR);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();

        Material material = block.getType();

        AchievementPlayer achievementPlayer = achievementManager.getPlayerManager().getAchievementPlayer(player);
        if (achievementPlayer == null)
            return;

        IslandBreakAchievement achievement = IslandAchievements.getBreakAchievement(material);
        if (achievement == null || achievementPlayer.hasAchievement(achievement))
            return;

        AchievementPlayerData achievementPlayerData = achievementPlayer.getAchievementsData(achievement);
        if (achievementPlayerData == null)
            return;

        int cache = achievementPlayerData.getLocalInfo().getOrDefault(IslandBreakAchievement.KEY, 0);
        cache ++;

        achievementPlayerData.addLocalInfo(IslandBreakAchievement.KEY, cache);
        if (cache < achievement.getAmount())
            return;

        achievementPlayer.complete(achievement);
    }

    private int getCraftAmount(CraftItemEvent e) {
        Player player = (Player) e.getWhoClicked();
        CraftingInventory inventory = e.getInventory();
        ItemStack craftItemStack = e.getRecipe().getResult();

        if (e.isShiftClick()) {
            int itemsChecked = 0;
            int possibleCreations = 1;

            int amountCanBeMade = 0;

            for (ItemStack item : inventory.getMatrix()) {
                if (item != null && item.getType() != Material.AIR) {
                    if (itemsChecked == 0) {
                        possibleCreations = item.getAmount();
                        itemsChecked++;
                    } else {
                        possibleCreations = Math.min(possibleCreations, item.getAmount());
                    }
                }
            }

            int amountOfItems = craftItemStack.getAmount() * possibleCreations;
            for (int s = 0; s <= inventory.getSize(); s++) {
                ItemStack test = player.getInventory().getItem(s);
                if(test == null || test.getType() == Material.AIR) {
                    amountCanBeMade += craftItemStack.getMaxStackSize();
                    continue;
                }
                if (test.isSimilar(craftItemStack)) {
                    amountCanBeMade += craftItemStack.getMaxStackSize() - test.getAmount();
                }
            }

            return amountOfItems > amountCanBeMade ? amountCanBeMade : amountOfItems;
        } else {
            return craftItemStack.getAmount();
        }
    }


}
