package net.lastcraft.skyblock.module;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lastcraft.base.sql.api.table.ColumnType;
import net.lastcraft.base.sql.api.table.TableColumn;
import net.lastcraft.base.sql.api.table.TableConstructor;
import net.lastcraft.skyblock.api.island.IslandModule;

@AllArgsConstructor
@Getter
public enum ModuleData {
    BIOME_MODULE(BiomeModule.class, new TableConstructor("IslandBiome",
            new TableColumn("id", ColumnType.INT_11).primaryKey(true).autoIncrement(true),
            new TableColumn("island", ColumnType.INT_11).primaryKey(true),
            new TableColumn("biome", ColumnType.INT_11).primaryKey(true))),
    BORDER_MODULE(BorderModule.class, new TableConstructor("IslandBorder",
            new TableColumn("id", ColumnType.INT_11).primaryKey(true).autoIncrement(true),
            new TableColumn("island", ColumnType.INT_11).primaryKey(true).unigue(true),
            new TableColumn("n", ColumnType.INT_11),
            new TableColumn("m", ColumnType.INT_11))),
    FLAG_MODULE(FlagModule.class, new TableConstructor("IslandFlags",
            new TableColumn("id", ColumnType.INT_11).primaryKey(true).autoIncrement(true),
            new TableColumn("island", ColumnType.INT_11).primaryKey(true),
            new TableColumn("flag", ColumnType.INT_11).primaryKey(true),
            new TableColumn("result", ColumnType.BOOLEAN))),
    GENERATOR_MODULE(GeneratorModule.class, new TableConstructor("IslandGenerator",
            new TableColumn("id", ColumnType.INT_11).primaryKey(true).autoIncrement(true),
            new TableColumn("island", ColumnType.INT_11).primaryKey(true),
            new TableColumn("generator", ColumnType.INT_11).primaryKey(true),
            new TableColumn("enable", ColumnType.BOOLEAN).setDefaultValue(0))),
    HOME_MODULE(HomeModule.class, new TableConstructor("IslandHome",
            new TableColumn("id", ColumnType.INT_11).primaryKey(true).autoIncrement(true),
            new TableColumn("island", ColumnType.INT_11).primaryKey(true).unigue(true),
            new TableColumn("home", ColumnType.TEXT))),
    IGNORE_MODULE(IgnoreModule.class, new TableConstructor("IslandIgnoreList",
            new TableColumn("id", ColumnType.INT_11).primaryKey(true).autoIncrement(true),
            new TableColumn("island", ColumnType.INT_11).primaryKey(true),
            new TableColumn("ignored", ColumnType.INT_11).primaryKey(true),
            new TableColumn("whoBanned", ColumnType.INT_11),
            new TableColumn("date", ColumnType.TIMESTAMP)))
    ;

    private final Class<? extends IslandModule> moduleClass;
    private final TableConstructor constructor;
}
