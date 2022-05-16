package net.lastcraft.alternate.api.events;

import lombok.Getter;
import net.lastcraft.alternate.api.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
public class UserTeleportByCommandEvent extends UserEvent {

    private final Command command;
    private final Location location;

    /**
     * Получить сендера (если он есть)
     * @return - игрок сендер
     */
    private Player sender;

    public UserTeleportByCommandEvent(User user, Command command, Location location) {
        super(user);
        this.command = command;
        this.location = location;
    }

    public UserTeleportByCommandEvent(User user, Command command, Location location, Player sender) {
        this(user, command, location);
        this.sender = sender;
    }

    public Location getLocation() {
        return location.clone();
    }

    public enum Command {
        SPAWN, TPACCEPT, TP, TOP, S, JUMP, TPPOS, BACK, HOME, CHUNK
    }
}
