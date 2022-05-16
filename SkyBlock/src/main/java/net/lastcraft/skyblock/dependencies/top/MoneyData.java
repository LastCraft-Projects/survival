package net.lastcraft.skyblock.dependencies.top;

import net.lastcraft.dartaapi.armorstandtop.data.StandTopData;

import java.util.function.Supplier;

public class MoneyData extends StandTopData {

    private double money = 0;

    public MoneyData(int playerID) {
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
