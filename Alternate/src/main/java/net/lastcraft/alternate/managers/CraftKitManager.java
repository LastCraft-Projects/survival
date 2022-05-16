package net.lastcraft.alternate.managers;

import net.lastcraft.alternate.api.Kit;
import net.lastcraft.alternate.api.manager.KitManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CraftKitManager implements KitManager {
    private Map<String, Kit> kits = new HashMap<>();

    @Override
    public Kit getKit(String name) {
        return kits.get(name.toLowerCase());
    }

    @Override
    public Map<String, Kit> getKits() {
        return kits;
    }

    @Override
    public List<Kit> getStarterKit() {
        return kits.values()
                .stream()
                .filter(Kit::isStart)
                .collect(Collectors.toList());
    }

    @Override
    public void addKit(Kit kit) {
        String name = kit.getName().toLowerCase();
        if (kits.containsKey(name))
            return;
        kits.put(name.toLowerCase(), kit);
    }

    @Override
    public void removeKit(Kit kit) {
        String name = kit.getName();
        kits.remove(name.toLowerCase());
    }
}
