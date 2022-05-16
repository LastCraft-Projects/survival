package net.lastcraft.siteshop.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.locale.Language;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public final class SSItem {

    private final int id;
    private final int price;
    private final String depend;
    private final List<ItemStack> purchasedItems;

    public ItemStack getIcon(Language lang, boolean give) {
        if  (purchasedItems.size() < 1) {
            return null;
        }

        List<String> lore = new ArrayList<>();
        if (depend != null) {
            lore.add("§8" + depend);
            lore.add("");
        }
        lore.addAll(lang.getList("SITE_SHOP_ITEM_LORE_1"));

        for (ItemStack itemStack : purchasedItems) {
            lore.add(" §8• §7" + itemStack.getType().name() + " §8x" + itemStack.getAmount());
        }
        lore.addAll(lang.getList("SITE_SHOP_ITEM_LORE_2", purchasedItems.size()));
        if (give) {
            lore.add(lang.getMessage("SITE_SHOP_ITEM_LORE_3"));
        } else {
            lore.addAll(lang.getList("SITE_SHOP_ITEM_LORE_4", price));
        }

        return ItemUtil.getBuilder(purchasedItems.get(0).clone())
                .setName("§a" + lang.getMessage("SITE_SHOP_ITEMS_NAME"))
                .setLore(lore)
                .build();
    }
}
