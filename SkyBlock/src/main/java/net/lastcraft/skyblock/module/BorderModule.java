package net.lastcraft.skyblock.module;

import lombok.Getter;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.event.module.IslandUpgradeEvent;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.island.IslandModule;
import net.lastcraft.skyblock.api.territory.IslandTerritory;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BorderModule extends IslandModule {
    private final static double Q = 1.6;
    private final static int N1 = 3500;

    @Getter
    private int size = SkyBlockAPI.getMinSize(); //Остров размеров n * m чанков (в чанке 16 блоков)
    //todo убрать N и M и сделать даже в БД только size

    public BorderModule(Island island) {
        super(island);
    }

    public boolean canBuild(Location location) {
        IslandTerritory territory = island.getTerritory();

        boolean fl = false;
        for (int i = territory.getMiddle().getFirst() - (size - 1) / 2; i <= territory.getMiddle().getFirst() + (size - 1) / 2; ++i)
            for (int j = territory.getMiddle().getSecond() - (size - 1) / 2; j <= territory.getMiddle().getSecond() + (size - 1) / 2; ++j)
                fl |= territory.getChunks()[i][j].canInteract(location);

        return fl;
    }

    public int getPercent() {
        int amount = 1 + (SkyBlockAPI.getSize() - SkyBlockAPI.getMinSize()) / 2;
        int size = (this.size - SkyBlockAPI.getMinSize()) / 2 + 1;
        return (int)(100.0 / (double)amount * (double)size);
    }

    public boolean upgrade() {
        if (size + 2 <= SkyBlockAPI.getSize()) {
            IslandUpgradeEvent e = new IslandUpgradeEvent(island);
            BukkitUtil.callEvent(e);

            if (e.isCancelled())
                return false;

            size += 2;

            final int id = island.getIslandID();
            if (id != -1) {
                DATABASE.execute("UPDATE `IslandBorder` SET `n` = ?, `m` = ? WHERE `island` = ?;", size, size, id);
            }

            return true;
        }
        return false;
    }

    @Override
    public void load(ResultSet rs) throws SQLException {
        this.size = rs.getInt("n");
    }

    @Override
    public void preLoad() {
        final int id = island.getIslandID();
        if (id == -1)
            return;

        DATABASE.execute("INSERT INTO `IslandBorder` (`island`, `n`, `m`) VALUES (?, ?, ?);", id, size, size);
    }

    @Override
    public void delete() {
        final int id = island.getIslandID();
        if (id == -1)
            return;

        DATABASE.execute("DELETE FROM `IslandBorder` WHERE `island` = ?;", id);
    }

    public int getLevel() {
        int max = (SkyBlockAPI.getSize() - SkyBlockAPI.getMinSize()) / 2;
        int total = (SkyBlockAPI.getSize() - getSize()) / 2;

        int level = max - total + 1;
        if (level > max)
            level = max;

        return level;
    }

    public int getPriceToUpgrade() {
        return (int) (N1 * (1 - Math.pow(Q, getLevel())) / (1.0 - Q));
    }
}
