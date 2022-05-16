package net.lastcraft.alternate.object;

import lombok.Getter;
import lombok.Setter;
import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.Kit;
import net.lastcraft.alternate.api.manager.KitManager;
import net.lastcraft.base.gamer.constans.Group;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CraftKit implements Kit {
    private static final KitManager KIT_MANAGER = AlternateAPI.getKitManager();

    @Setter @Getter
    private boolean start;
    private final ItemStack icon;
    @Getter
    private final String name;
    private Group group;
    private Group defaultGroup;
    @Setter @Getter
    private int cooldown;
    private List<ItemStack> itemStacks = Collections.synchronizedList(new ArrayList<>());

    public CraftKit(String name, Collection<ItemStack> itemStacks, ItemStack icon, int cooldown) {
        this(name, itemStacks, icon, cooldown, Group.DEFAULT, null);
    }

    public CraftKit(String name, Collection<ItemStack> itemStacks, ItemStack icon, int cooldown,
                    Group group, Group defaultGroup) {
        this.name = name;
        this.group = group;
        this.icon = icon;
        this.cooldown = cooldown;
        this.itemStacks.addAll(itemStacks);
        this.start = false;
        this.defaultGroup = defaultGroup;
    }

    @Override
    public List<ItemStack> getItems() {
        return itemStacks;
    }

    @Override
    public ItemStack getIcon() {
        return icon.clone();
    }

    @Override
    public void addItems(Collection<ItemStack> itemStacks) {
        this.itemStacks.addAll(itemStacks);
    }

    @Override
    public Group getMinimalGroup() {
        return group;
    }

    @Override
    public Group getDefaultGroup() {
        return defaultGroup;
    }

    @Override
    public void remove() {
        KIT_MANAGER.removeKit(this);
    }

    @Override
    public String toString() {
        return "AlternateKit{name = " + name + ", Items = {" + itemStacks.toString() + "}}";
    }
}
