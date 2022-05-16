package net.lastcraft.skyblock.generator;

import lombok.Getter;
import net.lastcraft.base.locale.Language;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

@Getter
public enum Generators {

    DEFAULT(0, new ItemStack(Material.COBBLESTONE), 0),
    FIRST(1, new ItemStack(Material.SAND), 15000, chance -> {
        if (chance <= 0.02)
            return Material.SAND;
        if (chance <= 0.04)
            return Material.GRAVEL;
        if (chance <= 0.05)
            return Material.CLAY;
        if (chance <= 0.07)
            return Material.DIRT;
        return Material.COBBLESTONE;
    }),
    SECOND(2, new ItemStack(Material.COAL), 20000, chance -> {
        if (chance <= 0.03)
            return Material.COAL_ORE;
        if (chance <= 0.04)
            return Material.IRON_ORE;
        return Material.COBBLESTONE;
    }),
    THIRD(3, new ItemStack(Material.GOLD_INGOT), 28000, chance -> {
        if (chance <= 0.03)
            return Material.GOLD_ORE;
        if (chance <= 0.04)
            return Material.IRON_ORE;
        if (chance <= 0.05)
            return Material.REDSTONE_ORE;
        return Material.COBBLESTONE;
    }),
    FOURTH(4, new ItemStack(Material.DIAMOND), 40000, chance -> {
        if (chance <= 0.03)
            return Material.GOLD_ORE;
        if (chance <= 0.04)
            return Material.DIAMOND_ORE;
        return Material.COBBLESTONE;
    }),
    FIFTH(5, new ItemStack(Material.INK_SACK, 1, (short) 4), 32000, chance -> {
        if (chance <= 0.05)
            return Material.LAPIS_ORE;
        if (chance <= 0.09)
            return Material.REDSTONE_ORE;
        if (chance <= 0.1)
            return Material.EMERALD_ORE;
        return Material.COBBLESTONE;
    }),
    ;

    Generators(int ID, ItemStack itemStack, int price) {
        this.ID = ID;
        this.itemStack = itemStack;
        this.price = price;
    }

    Generators(int ID, ItemStack itemStack, int price, Generator generator) {
        this(ID, itemStack, price);
        this.generator = generator;
    }

    private final int ID;
    private final ItemStack itemStack;
    private final int price;
    private Generator generator;

    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    public String getName(Language lang) {
        return lang.getMessage("ISLAND_" + this.name() + "_GENERATOR_NAME");
    }

    public List<String> getLore(Language lang) {
        return lang.getList("ISLAND_" + this.name() + "_GENERATOR_LORE");
    }

    /**
     * шанс 1% что будет заспавнено что-то другое
     */
    public Material getBlock() {
        if (generator == null)
            return Material.COBBLESTONE;

        return generator.getBlock(Math.random());
    }

    public static Generators get(int level) {
        return Arrays.stream(values())
                .filter(generator -> generator.ID == level)
                .findFirst()
                .orElse(DEFAULT);
    }
}
