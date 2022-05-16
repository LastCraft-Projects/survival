package net.lastcraft.market.auction;

import lombok.AllArgsConstructor;
import net.lastcraft.base.locale.Language;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

@AllArgsConstructor
public enum AuctionItemType {
    ARMOR(new ItemStack(Material.IRON_CHESTPLATE), getArmor()),
    WEAPON(new ItemStack(Material.DIAMOND_SWORD), getWeapons()),
    TOOLS(new ItemStack(Material.IRON_PICKAXE), getTools()),
    FOOD(new ItemStack(Material.APPLE), getFood()),
    POTION(new ItemStack(Material.POTION), getPotions()),
    BLOCKS(new ItemStack(Material.BRICK), getBlocks()),
    ENCHANTING(new ItemStack(Material.ENCHANTMENT_TABLE), getEnchanting()),
    RESOURCES(new ItemStack(Material.EMERALD), getResources()),
    FARMING(new ItemStack(Material.SAPLING), getFarming()),
    OTHER(new ItemStack(Material.CHEST), getOthers()),
    ALL(new ItemStack(Material.BARRIER), new ArrayList<>()); //просто так

    private final ItemStack item;
    private final ArrayList<Material> items;

    public ItemStack getItem() {
        return item.clone();
    }

    public String getName(Language lang) {
        return lang.getMessage("AUCTION_TYPE_" + name() + "_NAME");
    }

    public ArrayList<Material> getItems() {
        return items;
    }

    public static AuctionItemType getType(ItemStack item) {
        Material material = item.getType();

        return Arrays.stream(values())
                .filter(auctionItemType -> auctionItemType.items.contains(material))
                .findFirst()
                .orElse(OTHER);
    }

    private static ArrayList<Material> getFarming() {
        ArrayList<Material> ma = new ArrayList<>();
        ma.add(Material.SEEDS);
        ma.add(Material.MELON_SEEDS);
        ma.add(Material.PUMPKIN_SEEDS);
        ma.add(Material.WHEAT);
        ma.add(Material.SUGAR_CANE);
        ma.add(Material.CACTUS);
        return ma;
    }

    private static ArrayList<Material> getResources() {
        ArrayList<Material> ma = new ArrayList<>();
        ma.add(Material.DIAMOND);
        ma.add(Material.IRON_INGOT);
        ma.add(Material.GOLD_INGOT);
        ma.add(Material.REDSTONE);
        ma.add(Material.COAL);
        ma.add(Material.GOLD_NUGGET);
        ma.add(Material.EMERALD);
        return ma;
    }

    private static ArrayList<Material> getArmor() {
        ArrayList<Material> ma = new ArrayList<>();
        ma.add(Material.DIAMOND_HELMET);
        ma.add(Material.DIAMOND_CHESTPLATE);
        ma.add(Material.DIAMOND_LEGGINGS);
        ma.add(Material.DIAMOND_BOOTS);
        ma.add(Material.CHAINMAIL_HELMET);
        ma.add(Material.CHAINMAIL_CHESTPLATE);
        ma.add(Material.CHAINMAIL_LEGGINGS);
        ma.add(Material.CHAINMAIL_BOOTS);
        ma.add(Material.GOLD_HELMET);
        ma.add(Material.GOLD_CHESTPLATE);
        ma.add(Material.GOLD_LEGGINGS);
        ma.add(Material.GOLD_BOOTS);
        ma.add(Material.IRON_HELMET);
        ma.add(Material.IRON_CHESTPLATE);
        ma.add(Material.IRON_LEGGINGS);
        ma.add(Material.IRON_BOOTS);
        ma.add(Material.DIAMOND_HELMET);
        ma.add(Material.DIAMOND_CHESTPLATE);
        ma.add(Material.DIAMOND_LEGGINGS);
        ma.add(Material.DIAMOND_BOOTS);
        return ma;
    }

    private static ArrayList<Material> getTools() {
        ArrayList<Material> ma = new ArrayList<>();
        ma.add(Material.WOOD_PICKAXE);
        ma.add(Material.STONE_PICKAXE);
        ma.add(Material.IRON_PICKAXE);
        ma.add(Material.DIAMOND_PICKAXE);
        ma.add(Material.WOOD_AXE);
        ma.add(Material.STONE_AXE);
        ma.add(Material.IRON_AXE);
        ma.add(Material.DIAMOND_AXE);
        ma.add(Material.WOOD_SPADE);
        ma.add(Material.STONE_SPADE);
        ma.add(Material.IRON_SPADE);
        ma.add(Material.DIAMOND_SPADE);
        ma.add(Material.WOOD_HOE);
        ma.add(Material.STONE_HOE);
        ma.add(Material.IRON_HOE);
        ma.add(Material.DIAMOND_HOE);
        ma.add(Material.FISHING_ROD);
        return ma;
    }

    private static ArrayList<Material> getWeapons() {
        ArrayList<Material> ma = new ArrayList<>();
        ma.add(Material.WOOD_SWORD);
        ma.add(Material.STONE_SWORD);
        ma.add(Material.IRON_SWORD);
        ma.add(Material.DIAMOND_SWORD);
        ma.add(Material.WOOD_AXE);
        ma.add(Material.STONE_AXE);
        ma.add(Material.IRON_AXE);
        ma.add(Material.DIAMOND_AXE);
        ma.add(Material.BOW);
        return ma;
    }

    private static ArrayList<Material> getFood() {
        ArrayList<Material> ma = new ArrayList<>();
        for (Material m : Material.values()) {
            if (!m.isEdible() || m == Material.POTION)
                continue;
            ma.add(m);
        }
        return ma;
    }

    private static ArrayList<Material> getPotions() {
        ArrayList<Material> ma = new ArrayList<>();
        ma.add(Material.GLOWSTONE_DUST);
        ma.add(Material.NETHER_WARTS);
        ma.add(Material.BLAZE_POWDER);
        ma.add(Material.BLAZE_ROD);
        ma.add(Material.SUGAR);
        ma.add(Material.GHAST_TEAR);
        ma.add(Material.SPIDER_EYE);
        ma.add(Material.FERMENTED_SPIDER_EYE);
        ma.add(Material.MAGMA_CREAM);
        ma.add(Material.SPECKLED_MELON);
        ma.add(Material.GOLDEN_CARROT);
        ma.add(Material.RABBIT_FOOT);
        ma.add(Material.GLASS_BOTTLE);
        ma.add(Material.POTION);
        ma.add(Material.SLIME_BALL);
        return ma;
    }

    private static ArrayList<Material> getEnchanting() {
        ArrayList<Material> ma = new ArrayList<>();
        ma.add(Material.ENCHANTED_BOOK);
        ma.add(Material.EXP_BOTTLE);
        ma.add(Material.BOOKSHELF);
        ma.add(Material.ENCHANTMENT_TABLE);
        return ma;
    }

    private static ArrayList<Material> getBlocks() {
        ArrayList<Material> ma = new ArrayList<>();
        for (Material m : Material.values()) {
            if (!m.isBlock())
                continue;
            ma.add(m);
        }
        return ma;
    }

    private static ArrayList<Material> getOthers() {
        ArrayList<Material> ma = new ArrayList<>();
        for (Material m : Material.values()) {
            if (AuctionItemType.getArmor().contains(m)
                    || AuctionItemType.getTools().contains(m)
                    || AuctionItemType.getWeapons().contains(m)
                    || AuctionItemType.getFood().contains(m)
                    || AuctionItemType.getPotions().contains(m)
                    || AuctionItemType.getEnchanting().contains(m)
                    || AuctionItemType.getBlocks().contains(m))
                continue;
            ma.add(m);
        }
        return ma;
    }
}
