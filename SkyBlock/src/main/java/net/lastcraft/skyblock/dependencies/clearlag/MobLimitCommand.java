package net.lastcraft.skyblock.dependencies.clearlag;

import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.entity.IslandEntity;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.manager.EntityManager;
import net.lastcraft.skyblock.api.manager.IslandManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;

public class MobLimitCommand implements CommandInterface {

    private final ClearLagg clearLagg;
    private final IslandManager islandManager = SkyBlockAPI.getIslandManager();
    private final EntityManager entityManager = SkyBlockAPI.getEntityManager();

    MobLimitCommand(ClearLagg clearLagg) {
        this.clearLagg = clearLagg;
        SpigotCommand command = COMMANDS_API.register("mobs", this);
        command.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        Island island = islandManager.getIsland(player);
        if (island == null) {
            gamer.sendMessageLocale("ISLAND_NO_ISLAND");
            return;
        }
        Island to = islandManager.getIsland(player.getLocation());
        if (to == null || to.getIslandID() != island.getIslandID()) {
            gamer.sendMessageLocale("ISLAND_MOB_COMMAND_ERROR");
            return;
        }
        BukkitUtil.runTaskAsync(() -> {
            List<IslandEntity> islandEntities = entityManager.getEntities(island.getTerritory());
            int animals = (int) islandEntities.stream()
                    .filter(islandEntity -> islandEntity.getType() == EntityType.VILLAGER || islandEntity.isAnimal())
                    .count();
            int monster = (int) islandEntities.stream()
                    .filter(IslandEntity::isMonster)
                    .count();
            int players = (int) islandEntities.stream()
                    .filter(IslandEntity::isPlayer)
                    .count();
            int drops = (int) islandEntities.stream()
                    .filter(islandEntity -> islandEntity.getType() == EntityType.DROPPED_ITEM)
                    .count();

            int limit = clearLagg.getLimit(island.getOwner());
            gamer.sendMessagesLocale("ISLAND_MOB_COMMAND", animals, monster, players, drops, animals, limit);

            //if (!gamer.isDeveloper()) //чиста островов от лерых мобов
            //    return;
            //for (Island playerIsland : islandManager.getPlayerIsland().values()) {
            //    IslandTerritory islandTerritory = playerIsland.getTerritory();
            //    List<IslandEntity> removedEntities = entityManager.getEntities(islandTerritory);
            //    List<IslandEntity> removed = removedEntities.stream()
            //            .filter(islandEntity -> islandEntity.getType() == EntityType.VILLAGER || islandEntity.isAnimal())
            //            .collect(Collectors.toList());
            //    int limitIsland = clearLagg.getLimit(playerIsland.getOwner());
            //    int count = 0;
            //    for (int i = 0; i < removed.size(); i++) {
            //        if (i + 1 > limitIsland) {
            //            count++;
            //            IslandEntity player = removed.get(i);
            //            if (player != null)
            //                player.remove();
            //        }
            //    }
            //    if (count > 0)
            //        gamer.sendMessage("Я удалил на острове " + playerIsland.getOwner().getDisplayName() + " " + count + " энтити");
            //}
        });

    }
}
