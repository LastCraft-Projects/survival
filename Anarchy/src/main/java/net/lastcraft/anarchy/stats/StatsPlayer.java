package net.lastcraft.anarchy.stats;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatsPlayer {

    private int kills;
    private int death;

    public void addKill() {
        kills++;
    }

    public void addDeath() {
        death++;
    }
}
