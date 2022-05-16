package net.lastcraft.skyblock.module;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import net.lastcraft.base.sql.api.query.MysqlQuery;
import net.lastcraft.base.sql.api.query.QuerySymbol;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.island.IslandModule;
import net.lastcraft.skyblock.generator.Generators;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class GeneratorModule extends IslandModule {

    private final TIntObjectMap<Generators> generators = new TIntObjectHashMap<>();

    private Generators activeGenerator = Generators.DEFAULT;

    public GeneratorModule(Island island) {
        super(island);

        generators.put(Generators.DEFAULT.getID(), Generators.DEFAULT);
    }

    @Override
    public void load(ResultSet rs) throws SQLException {
        int generatorId = rs.getInt("generator");
        Generators generator = Generators.get(generatorId);
        generators.putIfAbsent(generatorId, generator);

        if (rs.getBoolean("enable")) {
            activeGenerator = generator;
        }
    }

    public void buyGenerator(Generators generator) {
        if (generators.containsKey(generator.getID()))
            return;

        final int id = island.getIslandID();
        if (id == -1)
            return;

        generators.put(generator.getID(), generator);
        DATABASE.execute(MysqlQuery.insertTo("IslandGenerator")
                .set("island", id)
                .set("generator", generator.getID()));
    }

    public void setDefaultGenerator(Generators generator) {
        if (!generators.containsKey(generator.getID()))
            return;

        if (activeGenerator.getID() == generator.getID())
            return;

        final int id = island.getIslandID();
        if (id == -1)
            return;

        DATABASE.execute(MysqlQuery.update("IslandGenerator")
                .where("island", QuerySymbol.EQUALLY, id)
                .where("generator", QuerySymbol.EQUALLY, activeGenerator.getID())
                .set("enable", 0));
        DATABASE.execute(MysqlQuery.update("IslandGenerator")
                .where("island", QuerySymbol.EQUALLY, id)
                .where("generator", QuerySymbol.EQUALLY, generator.getID())
                .set("enable", 1));
        activeGenerator = generator;
    }

    @Override
    public void delete() {
        final int id = island.getIslandID();
        if (id == -1)
            return;

        DATABASE.execute(MysqlQuery.deleteFrom("IslandGenerator")
            .where("island", QuerySymbol.EQUALLY, id));
    }
}
