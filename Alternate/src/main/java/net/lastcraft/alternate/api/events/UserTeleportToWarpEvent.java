package net.lastcraft.alternate.api.events;

import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.Warp;

public class UserTeleportToWarpEvent extends UserWarpEvent {

    public UserTeleportToWarpEvent(User user, Warp warp) {
        super(user, warp);
    }
}
