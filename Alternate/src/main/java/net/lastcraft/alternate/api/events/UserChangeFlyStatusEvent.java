package net.lastcraft.alternate.api.events;

import net.lastcraft.alternate.api.User;

public class UserChangeFlyStatusEvent extends StateChangeEvent {

    public UserChangeFlyStatusEvent(User user, boolean fly) {
        super(user, fly);
    }
}
