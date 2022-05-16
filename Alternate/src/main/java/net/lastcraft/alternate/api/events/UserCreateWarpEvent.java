package net.lastcraft.alternate.api.events;

import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.Warp;

public class UserCreateWarpEvent extends UserWarpEvent {

    public UserCreateWarpEvent(User user, Warp warp) {
        super(user, warp);
    }
}
