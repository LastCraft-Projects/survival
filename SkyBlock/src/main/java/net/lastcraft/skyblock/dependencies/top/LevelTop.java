package net.lastcraft.skyblock.dependencies.top;

import net.lastcraft.dartaapi.armorstandtop.data.StandTopData;

import java.util.function.Supplier;

public class LevelTop extends StandTopData {

    private double level = -1;

    public LevelTop(int playerID) {
        super(playerID, "ISLAND_TOP_LEVEL", false);
    }

    public void update(int playerID, double level) {
        this.playerID = playerID;
        this.level = level;
    }

    @Override
    protected Supplier<Integer> getSupplier() {
        return () -> (int) level;
    }
}