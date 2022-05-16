package net.lastcraft.market.auction;

import lombok.Getter;
import lombok.Setter;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.gamer.IBaseGamer;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.locale.Language;
import net.lastcraft.market.Market;
import net.lastcraft.market.api.AuctionItem;
import net.lastcraft.market.auction.gui.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AuctionManager {

    private final Map<UUID, AuctionItem> items = new ConcurrentHashMap<>();

    private final Map<Group, Integer> limitItemsToSell = new HashMap<>();

    private final Map<Integer, AuctionMainGui> mainGuis = new ConcurrentHashMap<>();
    private final Map<Integer, AuctionTypeMainGui> typeMainGuis = new ConcurrentHashMap<>();
    private final Map<Integer, Map<AuctionItemType, AuctionTypeGui>> typeGuis = new ConcurrentHashMap<>();
    @Getter
    private final Map<String, AuctionMyGui> ownerGuis = new ConcurrentHashMap<>();

    //todo переписать эту чушь с этой мапой
    private final Map<String, AuctionPlayerGui> playersGui = new ConcurrentHashMap<>();

    @Getter @Setter
    private FileConfiguration config;
    @Setter
    private File file;

    public AuctionManager(Market market) {

        for (Language lang : Language.values()) {
            mainGuis.put(lang.getId(), new AuctionMainGui(this, lang));
            typeMainGuis.put(lang.getId(), new AuctionTypeMainGui(this, lang));

            Map<AuctionItemType, AuctionTypeGui> typeGui = new HashMap<>();
            Arrays.stream(AuctionItemType.values()).forEach(type ->
                    typeGui.put(type, new AuctionTypeGui(this, lang, type)));
            this.typeGuis.put(lang.getId(), typeGui);
        }

        AtomicInteger count = new AtomicInteger();
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            try {
                count.getAndIncrement();

                mainGuis.values().forEach(AuctionMainGui::update);
                typeGuis.values().forEach(gui -> gui.values().forEach(AuctionTypeGui::update));

                ownerGuis.values().forEach(AuctionMyGui::update);

                playersGui.values().forEach(AuctionAbstractGui::update);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (count.get() > 1000) {
                saveConfig();
                count.set(0);
            }
        }, 5, 1, TimeUnit.SECONDS);
    }

    public void clearAll() {
        items.clear();
    }

    public void add(AuctionItem auctionItem) {
        items.put(auctionItem.getID(), auctionItem);

        if (file == null) {
            return;
        }

        String uuid = auctionItem.getID().toString();

        //запись в конфиг
        config.addDefault("Items", uuid);
        config.set("Items." + uuid + ".item", auctionItem.getItem());
        config.set("Items." + uuid + ".owner", auctionItem.getOwner().getPlayerID());
        config.set("Items." + uuid + ".price", auctionItem.getPrice());
        config.set("Items." + uuid + ".date", auctionItem.getDate().getTime());

        //updateGuis();
    }

    public void setLimitItemsToSell(Map<Group, Integer> limitItemsToSell) {
        this.limitItemsToSell.clear();
        this.limitItemsToSell.putAll(limitItemsToSell);
    }

    public void remove(AuctionItem auctionItem) {
        AuctionItem remove = items.remove(auctionItem.getID());

        if (remove == null || file == null)
            return;

        //удалить из конфига
        config.getConfigurationSection("Items").set(auctionItem.getID().toString(), null);
    }

    public void saveConfig() {
        if (config == null || file == null)
            return;
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<UUID, AuctionItem> getAllItems() {
        return items;
    }

    public void onDisable() {
        this.saveConfig();
    }

    public boolean checkLimit(BukkitGamer gamer) {
        return getLimit(gamer) >= items.values().stream()
                .filter(auctionItem ->
                        (auctionItem.getOwner().getPlayerID() == gamer.getPlayerID()) && !auctionItem.isExpired())
                .count();
    }

    public int getLimit(BukkitGamer gamer) {
        return limitItemsToSell.getOrDefault(gamer.getGroup(), 10);
    }

    public void openMainGui(BukkitGamer gamer) {
        AuctionMainGui gui = mainGuis.get(gamer.getLanguage().getId());
        if (gui == null)
            gui = mainGuis.get(Language.getDefault().getId());

        gui.open(gamer.getPlayer());
    }

    public void openTypeMainGui(BukkitGamer gamer) {
        AuctionTypeMainGui gui = typeMainGuis.get(gamer.getLanguage().getId());
        if (gui == null)
            gui = typeMainGuis.get(Language.getDefault().getId());

        gui.open(gamer.getPlayer());
    }

    public void openTypeGui(BukkitGamer gamer, AuctionItemType type) {
        Map<AuctionItemType, AuctionTypeGui> guis = this.typeGuis.get(gamer.getLanguage().getId());
        if (guis == null)
            guis = this.typeGuis.get(Language.getDefault().getId());

        guis.get(type).open(gamer.getPlayer());
    }

    public void removePlayerGuis(Player player) {
        playersGui.keySet().forEach(name -> {
            if (name.startsWith(player.getName().toLowerCase()))
                playersGui.remove(name);
        });
    }
    public AuctionPlayerGui getPlayerGui(BukkitGamer gamer, IBaseGamer who) {
        AuctionPlayerGui gui = playersGui.get(gamer.getName().toLowerCase() + who.getName().toLowerCase());
        if (gui == null) {
            gui = new AuctionPlayerGui(this, gamer.getLanguage(), who);
            playersGui.put(gamer.getName().toLowerCase() + who.getName().toLowerCase(), gui);
        }

        return gui;
    }

    public AuctionMyGui getOrCreateMyGui(Player player) {
        AuctionMyGui gui = this.ownerGuis.get(player.getName().toLowerCase());
        if (gui == null) {
            gui = new AuctionMyGui(this, player);
            this.ownerGuis.put(player.getName().toLowerCase(), gui);
        }

        return gui;
    }
}
