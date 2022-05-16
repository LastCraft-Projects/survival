package net.lastcraft.skyblock.module;

import lombok.Getter;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import net.lastcraft.skyblock.api.event.module.IslandSetFlagEvent;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.island.IslandFlag;
import net.lastcraft.skyblock.api.island.IslandModule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FlagModule extends IslandModule {

    @Getter
    private final Map<IslandFlag, Boolean> flags = new ConcurrentHashMap<>();

    public FlagModule(Island island) {
        super(island);
    }

    @Override
    public void load(ResultSet rs) throws SQLException {
        IslandFlag flag = IslandFlag.getFlag(rs.getInt("flag"));
        boolean result = rs.getBoolean("result");
        if (flag == null)
            return;

        flags.put(flag, result);
    }

    @Override
    public void delete() {
        final int id = island.getIslandID();
        if (id == -1)
            return;

        DATABASE.execute("DELETE FROM `IslandFlags` WHERE `island` = ?;", id);
    }

    /**
     * узнать результат работы флага
     * @return - результат
     */
    public boolean isFlag(IslandFlag flag) {
        return flags.getOrDefault(flag, flag.isDefaultValue());
    }

    /**
     * уставновить флаг
     * @param flag - флаг
     * @param result - что именно поставить
     */
    public void setFlag(IslandFlag flag, boolean result) {
        Boolean saveResult = flags.get(flag);

        if (saveResult != null && saveResult == result)
            return;

        IslandSetFlagEvent event = new IslandSetFlagEvent(island, flag, result);
        BukkitUtil.callEvent(event);

        if (event.isCancelled())
            return;

        flags.put(flag, result);

        int id = island.getIslandID();
        if (id == -1)
            return;

        if (saveResult == null) {
            DATABASE.execute("INSERT INTO `IslandFlags` (`island`, `flag`, `result`) VALUES (?, ?, ?);",
                    id, flag.getId(), result);

            return;
        }

        DATABASE.execute("UPDATE `IslandFlags` SET `result` = ? WHERE `island`= ? AND `flag` = ? LIMIT 1;",
                result, id, flag.getId());
    }
}
