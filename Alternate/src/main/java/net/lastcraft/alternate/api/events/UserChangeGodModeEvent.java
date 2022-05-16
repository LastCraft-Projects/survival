package net.lastcraft.alternate.api.events;

import net.lastcraft.alternate.api.User;

public class UserChangeGodModeEvent extends StateChangeEvent {

    public UserChangeGodModeEvent(User user, boolean god) {
        super(user, god);
    }
}
