package net.lastcraft.alternate.config;

import lombok.Getter;
import net.lastcraft.alternate.Alternate;
import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.Kit;
import net.lastcraft.alternate.api.manager.KitManager;
import net.lastcraft.alternate.object.CraftKit;
import net.lastcraft.api.util.LocationUtil;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.dartaapi.utils.bukkit.BlockUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class ConfigData {

    private final Alternate alternate;
    
    private String prefix;
    private String dataBase;
    private final Map<String, Integer> voidWorld = new HashMap<>();

    private boolean trade;
    private boolean spawnTp;
    private boolean spawnFlyOff;

    private boolean warpSystem;
    private boolean globalLocalChat;
    private boolean callSystem;
    private boolean kitSystem;
    private boolean homeSystem;
    private boolean bedHomeSystem;
    private boolean portalCreate;

    private final Map<Group, Integer> homeLimit = new HashMap<>();
    private final Map<Group, Integer> warpLimit = new HashMap<>();

    public ConfigData(Alternate alternate) {
        this.alternate = alternate;

        alternate.saveDefaultConfig();
    }

    public void load() {
        FileConfiguration config = alternate.getConfig();

        voidWorld.clear();
        for (String world : config.getConfigurationSection("voidSpawn").getKeys(false)) {
            int level = config.getInt("voidSpawn." + world);
            voidWorld.put(world.toLowerCase(), level);
        }

        dataBase = config.getString("dataBase");
        prefix = config.getString("prefix");

        AlternateAPI.setSpawn(LocationUtil.stringToLocation(config.getString("spawn"), true));
        spawnTp = config.getBoolean("spawnTp");
        spawnFlyOff = config.getBoolean("spawnFlyOff");

        globalLocalChat = config.getBoolean("globalLocalChat");

        warpSystem = config.getBoolean("warpSystem");
        homeSystem = config.getBoolean("homeSystem");
        callSystem = config.getBoolean("callSystem");
        kitSystem = config.getBoolean("kitSystem");
        bedHomeSystem = config.getBoolean("bedHomeSystem");
        portalCreate = config.getBoolean("portalCreate");

        trade = config.getBoolean("trade");
    }

    public void init() {
        if (kitSystem)
            loadKit();

        if (homeSystem)
            loadHomeLimit();

        if (warpSystem) {
            loadWarpLimit();
            AlternateSql.loadWarps();
        }
    }

    public boolean isBedHomeSystem() {
        return bedHomeSystem;
    }

    private void loadHomeLimit() {
        homeLimit.clear();
        homeLimit.put(Group.DEFAULT, alternate.getConfig().getInt("setHomeDefault"));

        for (String string : alternate.getConfig().getStringList("setHomeLimit")) {
            if (!string.contains(":")) {
                continue;
            }

            String groupName = string.split(":")[0];
            Group group = Group.getGroupByName(groupName);
            if (group == Group.DEFAULT) {
                continue;
            }

            int limit = Integer.parseInt(string.split(":")[1]);
            homeLimit.put(group, limit);
        }
    }

    private void loadKit() {
        KitManager kitManager = AlternateAPI.getKitManager();
        kitManager.getKits().clear();
        FileConfiguration config = alternate.getConfig();

        try {
            for (String kitName : config.getConfigurationSection("Kits").getKeys(false)) {
                String patch = "Kits." + kitName + ".";
                boolean start = false;
                int cooldown = config.getInt(patch + "cooldown");
                Group group = Group.DEFAULT;
                Group defaultGroup = null;

                if (config.contains(patch + "start")) {
                    start = config.getBoolean(patch + "start");
                }

                if (config.contains(patch + "group")) {
                    group = Group.getGroup(config.getInt(patch + "group"));
                }

                if (config.contains(patch + "mainGroup")) {
                    defaultGroup = Group.getGroup(config.getInt(patch + "mainGroup"));
                }

                ItemStack icon = BlockUtil.itemStackFromString(config.getString(patch + "icon"));
                List<ItemStack> itemStack = config.getStringList(patch + "items")
                        .stream()
                        .map(BlockUtil::itemStackFromString)
                        .collect(Collectors.toList());

                Kit kit = new CraftKit(kitName, itemStack, icon, cooldown, group, defaultGroup);
                kit.setStart(start);

                kitManager.addKit(kit);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadWarpLimit() {
        warpLimit.clear();
        warpLimit.put(Group.DEFAULT, alternate.getConfig().getInt("setWarpDefault"));

        for (String string : alternate.getConfig().getStringList("setWarpLimit")) {
            if (!string.contains(":")) {
                continue;
            }

            String groupName = string.split(":")[0];
            Group group = Group.getGroupByName(groupName);
            if (group == Group.DEFAULT) {
                continue;
            }

            int limit = Integer.parseInt(string.split(":")[1]);
            warpLimit.put(group, limit);
        }
    }

    public Integer getHomeLimit(Group group) {
        return homeLimit.get(group);
    }

    public int getInt(String patch) {
        return alternate.getConfig().getInt(patch);
    }
}
