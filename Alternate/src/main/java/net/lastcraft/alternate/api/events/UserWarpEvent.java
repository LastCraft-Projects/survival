package net.lastcraft.alternate.api.events;

import lombok.Getter;
import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.api.Warp;

public abstract class UserWarpEvent extends UserEvent {

    @Getter
    private final Warp warp;

    protected UserWarpEvent(User user, Warp warp) {
        super(user);
        this.warp = warp;
    }
}
