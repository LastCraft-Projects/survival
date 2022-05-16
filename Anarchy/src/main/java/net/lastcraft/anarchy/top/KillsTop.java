package net.lastcraft.anarchy.top;

import net.lastcraft.dartaapi.armorstandtop.data.StandTopData;

import java.util.function.Supplier;

public class KillsTop extends StandTopData {

    private double kills = 0;

    public KillsTop(int playerID) {
        super(playerID, "KILLS_1", true);
    }

    public void update(int playerID, double money) {
        this.playerID = playerID;
        this.kills = money;
    }

    @Override
    protected Supplier<Integer> getSupplier() {
        return () -> (int) kills;
    }
}
