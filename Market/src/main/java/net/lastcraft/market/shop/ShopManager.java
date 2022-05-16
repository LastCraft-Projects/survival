package net.lastcraft.market.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ShopManager {

    private final Map<String, ShopGui> guis = new ConcurrentHashMap<>(); //в качестве стринга name + lang

    private final List<String> shops = new ArrayList<>(); //имена всех магазов

    public List<String> getShopsNames() {
        return shops;
    }

    public void addShop(ShopGui shopGui) {
        guis.put(shopGui.getName().toLowerCase() + shopGui.getLang(), shopGui);
    }

    public Map<String, ShopGui> getGuis() {
        return guis;
    }

    public void clearAll() {
        guis.clear();
        shops.clear();
    }
}
