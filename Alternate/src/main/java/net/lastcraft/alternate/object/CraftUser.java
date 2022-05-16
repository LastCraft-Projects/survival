package net.lastcraft.alternate.object;

import lombok.Getter;
import lombok.Setter;
import net.lastcraft.alternate.Alternate;
import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.Home;
import net.lastcraft.alternate.api.Kit;
import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.events.UserChangeGamemodeEvent;
import net.lastcraft.alternate.api.manager.UserManager;
import net.lastcraft.alternate.config.AlternateSql;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.util.LocationUtil;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.sql.GlobalLoader;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Getter
public class CraftUser implements User {
    private static final UserManager MANAGER = AlternateAPI.getUserManager();
    private static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();

    private final Map<String, Long> callReguests = new ConcurrentHashMap<>();
    private final Map<String, Long> tradeReguests = new ConcurrentHashMap<>();
    private final String name;

    @Setter
    private boolean recipeSee;

    private Player player;

    private boolean fly;
    private boolean god;
    private boolean tpToggle;
    private Location lastLocation;
    private Location bedLocation;
    private Map<Kit, Timestamp> kits;
    private Map<String, Home> homes;

    private transient long lastActivity;

    private boolean firstJoin;
    private boolean saved = false;

    public CraftUser(String name, boolean fly, boolean god, boolean tpToggle, boolean first,
                     Location lastLocation, Map<Kit, Timestamp> kits, Map<String, Home> homes,
                     Location bedLocation) {
        this.name = name;
        this.kits = kits;

        BukkitGamer gamer = GAMER_MANAGER.getGamer(name);
        this.firstJoin = gamer != null && first;
        this.fly = fly;
        this.god = god;
        this.tpToggle = tpToggle;
        this.lastLocation = lastLocation;
        this.homes = homes;
        this.bedLocation = bedLocation;
        this.lastActivity = System.currentTimeMillis();
    }

    @Override
    public Player getPlayer() {
        if (player != null) {
            return player;
        }

        return player = Bukkit.getPlayerExact(getName());
    }

    @Override
    public Map<Kit, Timestamp> getKits() {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(name);
        if (gamer == null)
            return kits;
        int playerID = gamer.getPlayerID();
        for (Map.Entry<Kit, Timestamp> kitEntry : kits.entrySet()) {
            Kit kit = kitEntry.getKey();
            long time = kitEntry.getValue().getTime();
            if (System.currentTimeMillis() > time) {
                kits.remove(kit);
                AlternateSql.removeKitFromSql(playerID, kit);
            }
        }
        return kits;
    }

    @Override
    public void addKit(Kit kit) {
        getKits();
        long time = System.currentTimeMillis() + kit.getCooldown() * 1000;
        kits.put(kit, new Timestamp(time));
        BukkitGamer gamer = GAMER_MANAGER.getGamer(name);
        if (gamer == null)
            return;

        int playerID = gamer.getPlayerID();
        AlternateSql.addKitToSql(playerID, kit.getName(), time);
    }

    @Override
    public boolean isCooldown(Kit kit) {
        return getKits().containsKey(kit);
    }

    @Override
    public int getCooldown(Kit kit) {
        Timestamp time = getKits().get(kit);
        if (time == null)
            return 0;

        return (int) ((time.getTime() - System.currentTimeMillis()) / 1000);
    }

    @Override
    public void addHome(String name, Location loc) {
        if (homes.containsKey(name))
            return;

        Home home = new CraftHome(name, loc);
        homes.put(name, home);

        BukkitGamer gamer = GAMER_MANAGER.getGamer(this.name);
        if (gamer == null) {
            //BukkitUtil.runTaskAsync(() -> {
            //    int playerID = GlobalLoader.containsPlayerID(this.name);
            //    AlternateSql.addHomeToSql(playerID, name, home.getLocation());
            //});
            return;
        }

        int playerID = gamer.getPlayerID();
        AlternateSql.addHomeToSql(playerID, name, home.getLocation());

    }

    @Override
    public void removeHome(String name) {
        homes.remove(name);
        BukkitGamer gamer = GAMER_MANAGER.getGamer(this.name);
        if (gamer == null) {
            //BukkitUtil.runTaskAsync(() -> {
            //    int playerID = GlobalLoader.containsPlayerID(this.name);
            //    AlternateSql.removeHomeFromSql(playerID, name);
            //});
            return;
        }
        int playerID = gamer.getPlayerID();
        AlternateSql.removeHomeFromSql(playerID, name);

    }

    @Override
    public boolean isOnline() {
        return getPlayer() != null && getPlayer().isOnline();
    }

    @Override
    public void setGod(boolean god, boolean message) {
        if (this.god != god)
            saved = true;

        this.god = god;
        Player player = getPlayer();
        if (player == null || !player.isOnline())
            return;

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        Language lang = gamer.getLanguage();
        if (god) {
            if (player.getHealth() != 0) {
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);
            }
            if (message)
                gamer.sendMessage(Alternate.getConfigData().getPrefix()
                        + lang.getMessage("GOD_ENABLE"));
        } else if (message) {
            gamer.sendMessage(Alternate.getConfigData().getPrefix()
                    + lang.getMessage("GOD_DISABLE"));
        }
    }

    @Override
    public void setFly(boolean fly, boolean message) {
        if (this.fly != fly)
            saved = true;

        this.fly = fly;

        Player player = getPlayer();
        if (player == null || !player.isOnline())
            return;

        player.setAllowFlight(fly);
        player.setFlying(fly);

        if (!message)
            return;

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        Language lang = gamer.getLanguage();

        gamer.sendMessage(Alternate.getConfigData().getPrefix() + lang.getMessage((fly ? "FLY_ENABLE" : "FLY_DISABLE")));
    }

    @Override
    public void setTpToggle(boolean tpToggle) {
        if (this.tpToggle != tpToggle) {
            this.saved = true;
        }

        this.tpToggle = tpToggle;
    }

    @Override
    public void setLastLocation(Location location) {
        saved = true;
        this.lastLocation = location;
    }

    @Override
    public void setGamemode(GameMode gamemode) {
        Player player = getPlayer();
        if (player == null || !player.isOnline())
            return;

        UserChangeGamemodeEvent event = new UserChangeGamemodeEvent(this, gamemode);
        BukkitUtil.callEvent(event);

        if (event.isCancelled())
            return;

        player.setGameMode(event.getGameMode());
    }

    @Override
    public Location getBedLocation() {
        if (bedLocation == null) {
            return AlternateAPI.getSpawn();
        }

        if (!bedLocation.getChunk().isLoaded()) {
            bedLocation.getChunk().load();
        }

        Block block = bedLocation.getWorld().getBlockAt(bedLocation);
        if (block.getType() != Material.BED_BLOCK && block.getType() != Material.BED) {
            return AlternateAPI.getSpawn();
        }

        return bedLocation.clone().add(0.0, 1.5, 0.0);
    }

    @Override
    public void setBedLocation(Location location) {
        saved = true;
        this.bedLocation = location;

        BukkitGamer gamer = GAMER_MANAGER.getGamer(name);
        if (gamer == null) {
            return;
        }

        Language lang = gamer.getLanguage();
        gamer.sendMessage(Alternate.getConfigData().getPrefix() + lang.getMessage("USER_SETBED"));
    }

    @Override
    public boolean teleport(Location location) {
        Player player = getPlayer();
        if (player == null) {
            return false;
        }

        Location old = player.getLocation();
        if (player.teleport(location)) {
            setLastLocation(old);

            return true;
        }

        return false;
    }

    @Override
    public boolean checkAfk() {
        return lastActivity + TimeUnit.MINUTES.toMillis(10) < System.currentTimeMillis();
    }

    @Override
    public void updateAfkPosition() {
        lastActivity = System.currentTimeMillis();
    }

    @Override
    public void remove() {
        MANAGER.removeUser(this);
    }

    public void save() {
        if (!saved) {
            return;
        }

        BukkitGamer gamer = GAMER_MANAGER.getGamer(name);
        int playerID = gamer == null ? GlobalLoader.containsPlayerID(name) : gamer.getPlayerID();

        AlternateSql.saveData(playerID, "fly", String.valueOf(fly ? 1 : 0));
        AlternateSql.saveData(playerID, "god", String.valueOf(god ? 1 : 0));
        AlternateSql.saveData(playerID, "tpToggle", String.valueOf(tpToggle ? 1 : 0));

        if (lastLocation != null) {
            AlternateSql.saveData(playerID, "lastLocation",
                    LocationUtil.locationToString(lastLocation, true));
        }

        if (bedLocation != null) {
            AlternateSql.saveData(playerID, "bedLocation",
                    LocationUtil.locationToString(bedLocation, false));
        }

    }

    public boolean addCallRequest(Player player) {
        long time = System.currentTimeMillis();
        String name = player.getName();

        if (callReguests.containsKey(name) && callReguests.get(name) + 120 * 1000 > System.currentTimeMillis())
            return false;

        callReguests.put(name, time);
        return true;
    }

    public boolean addTradeRequest(Player player) {
        long time = System.currentTimeMillis();
        String name = player.getName();

        if (tradeReguests.containsKey(name) && tradeReguests.get(name) + 120 * 1000 > System.currentTimeMillis())
            return false;

        tradeReguests.put(name, time);
        return true;
    }

}
