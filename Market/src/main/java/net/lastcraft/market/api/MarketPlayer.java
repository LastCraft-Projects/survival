package net.lastcraft.market.api;

import net.lastcraft.api.depend.BaseUser;

public interface MarketPlayer extends BaseUser {


    /**
     * баланс игрока
     * @return - баланс
     */
    double getMoney();

    /**
     * изменить деньги
     * @param money - сумма
     * @return - можно будет сменить или нет
     */
    boolean changeMoney(double money);

    boolean hasMoney(double money);
}
