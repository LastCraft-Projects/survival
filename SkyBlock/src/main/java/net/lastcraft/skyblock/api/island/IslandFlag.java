package net.lastcraft.skyblock.api.island;

import lombok.Getter;
import net.lastcraft.base.gamer.constans.Group;
import org.bukkit.Material;

import java.util.Arrays;

@Getter
public enum IslandFlag {
    PVP(1, Material.GOLD_CHESTPLATE, false), //можно ли тут ПВП
    HUNGER(2, Material.BEETROOT_SOUP, true, Group.DIAMOND), //будет ли тут тратится голод у игроков на этом острове
    USE(3, Material.CHEST, true), //можно ли другим игрокам юзать рычаги/двери и тд
    ENTITY_EXPLODE(4, Material.TNT, false, Group.GOLD), //Взрыв энтити, или же взрыв блока
    OPENED(5, Material.BARRIER, false), //можно ли другим зайти на твой остров через island tp(друзья могут всегда) и ставить там варпы
    ENTITY_DAMAGE(6, Material.LEATHER, false), // урон по мобам
    PICKUP(7, Material.SNOW, true),//поднимать ресы другим игрокам
    DROP(8, Material.DROPPER, true), //можно ли выбрасывать шмот другим игрокам
    DROP_LEAVES(9, Material.LEAVES, true, Group.GOLD), //опадение листвы
    SPAWN_MOB(10, Material.MOB_SPAWNER, true),       //спавн мобов в территории
    INVINCIBLE(11, Material.TOTEM, false, Group.MAGMA), //неязвимость для острова всего и всех его участников
    FIRE_SPREAD(12, Material.FIREBALL, true, Group.DIAMOND), //распр огня
    ENTITY_GRIF(13, Material.MONSTER_EGG, true, Group.GOLD), //мобы которые гриферят
    ;

    private final int id;
    private final Material material;

    private final boolean defaultValue;
    private Group group = Group.DEFAULT;

    IslandFlag(int id, Material material, boolean defaultValue) {
        this.id = id;
        this.material = material;
        this.defaultValue = defaultValue;
    }

    IslandFlag(int id, Material material, boolean defaultValue, Group group) {
        this(id, material, defaultValue);
        this.group = group;
    }

    public static IslandFlag getFlag(int idFlag) {
        return Arrays.stream(values())
                .filter((container) -> container.getId() == idFlag)
                .findFirst()
                .orElse(null);
    }


}
