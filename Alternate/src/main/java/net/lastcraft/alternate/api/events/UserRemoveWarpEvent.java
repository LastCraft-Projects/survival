package net.lastcraft.alternate.api.events;

import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.Warp;

public class UserRemoveWarpEvent extends UserWarpEvent {

    public UserRemoveWarpEvent(User user, Warp warp) {
        super(user, warp);
    }
}
