package net.lastcraft.alternate.object;

import lombok.Getter;
import net.lastcraft.alternate.Alternate;
import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.Warp;
import net.lastcraft.alternate.api.events.UserTeleportToWarpEvent;
import net.lastcraft.alternate.api.manager.UserManager;
import net.lastcraft.alternate.api.manager.WarpManager;
import net.lastcraft.alternate.config.AlternateSql;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.util.Head;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.api.util.LocationUtil;
import net.lastcraft.base.gamer.IBaseGamer;
import net.lastcraft.base.locale.Language;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import net.lastcraft.dartaapi.utils.core.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Date;

public class CraftWarp implements Warp {
    private static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();
    private static final UserManager USER_MANAGER = AlternateAPI.getUserManager();
    private static final WarpManager WARP_MANAGER = AlternateAPI.getWarpManager();

    @Getter
    private final String name;
    @Getter
    private final int ownerID;
    @Getter
    private final Date date;

    @Getter
    private boolean isPrivate;
    private ItemStack icon;

    private IBaseGamer gamer;

    private String locationString;
    private Location location;

    public CraftWarp(String name, int owner, Location location, boolean isPrivate) {
        this.name = name;
        this.ownerID = owner;
        this.location = location;
        this.date = new Date(System.currentTimeMillis());
        this.isPrivate = isPrivate;
    }

    public CraftWarp(String name, int owner, String locationString, long date, boolean isPrivate) {
        this.name = name;
        this.ownerID = owner;
        this.locationString = locationString;
        this.date = new Date(date);
        this.isPrivate = isPrivate;
    }

    @Override
    public String getNameOwner() {
        return getOwner().getDisplayName();
    }

    @Override
    public IBaseGamer getOwner() {
        if (gamer == null) {
            gamer = GAMER_MANAGER.getOrCreate(ownerID);
        }

        return gamer;
    }

    @Override
    public Collection<Player> getNearbyPlayers(int size) {
        return PlayerUtil.getNearbyPlayers(location, size);
    }

    @Override
    public ItemStack getIcon() {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(ownerID);
        if (gamer != null) {
            icon = gamer.getHead();
        }

        if (icon == null) {
            IBaseGamer offlineGamer = getOwner();
            if (offlineGamer == null) {
                icon = ItemUtil.getBuilder(Material.SKULL_ITEM)
                        .setDurability((short) 3)
                        .build();
            } else {
                icon = Head.getHeadByValue(offlineGamer.getSkin().getValue());
            }
        }

        return icon.clone();
    }

    @Override
    public Location getLocation() {
        if (location == null) {
            location = LocationUtil.stringToLocation(locationString, true);

            if (location.getWorld() == null) {
                location = AlternateAPI.getSpawn();
            }
        }

        return location.clone();
    }

    @Override
    public World getWorld() {
        return getLocation().getWorld();
    }

    @Override
    public void setPrivate(boolean flag) {
        if (this.isPrivate == flag) {
            return;
        }

        this.isPrivate = flag;
        AlternateSql.setWarpPrivate(this);
    }

    @Override
    public void teleport(Player player) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return;
        }

        if (isPrivate && !gamer.isFriend(ownerID) && !gamer.isStaff()) {
            gamer.sendMessageLocale("WARP_CLOSE", getName());
            return;
        }

        User user = USER_MANAGER.getUser(player);
        if (user == null) {
            return;
        }

        UserTeleportToWarpEvent event = new UserTeleportToWarpEvent(user, this);
        BukkitUtil.callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        Language lang = gamer.getLanguage();
        if (user.teleport(getLocation())) {
            gamer.sendMessage(Alternate.getConfigData().getPrefix() + lang.getMessage("WARP_TO", getName()));
        }
    }

    @Override
    public Warp save() {
        AlternateAPI.getWarpManager().addWarp(this);
        return this;
    }

    @Override
    public void remove() {
        WARP_MANAGER.removeWarp(this);
    }

    @Override
    public String toString() {
        return "Warp{name = " + name + ", " +
                "owner = " + ownerID + ", " +
                "private = " + isPrivate + ", " +
                "location = " + location + "}";
    }
}
