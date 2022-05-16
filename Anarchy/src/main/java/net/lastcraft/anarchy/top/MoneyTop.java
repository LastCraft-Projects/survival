package net.lastcraft.anarchy.top;

import net.lastcraft.dartaapi.armorstandtop.data.StandTopData;

import java.util.function.Supplier;

public class MoneyTop extends StandTopData {

    private double money = 0;

    public MoneyTop(int playerID) {
        super(playerID, "MONEY_1", true);
    }

    public void update(int playerID, double money) {
        this.playerID = playerID;
        this.money = money;
    }

    @Override
    protected Supplier<Integer> getSupplier() {
        return () -> (int) money;
    }
}
