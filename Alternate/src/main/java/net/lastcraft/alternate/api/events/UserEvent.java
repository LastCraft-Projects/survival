package net.lastcraft.alternate.api.events;

import net.lastcraft.alternate.api.User;
import net.lastcraft.api.event.DEvent;
import org.bukkit.event.Cancellable;

public abstract class UserEvent extends DEvent implements Cancellable {

    private boolean cancelled = false;
    private final User user;

    UserEvent(User user) {
        this.user = user;
    }

    /**
     * Получить юзера
     * @return - юзер
     */
    public User getUser() {
        return user;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
