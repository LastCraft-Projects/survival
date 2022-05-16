package net.lastcraft.skyblock.achievement;

import lombok.RequiredArgsConstructor;
import net.lastcraft.dartaapi.achievements.achievement.Achievement;
import net.lastcraft.skyblock.achievement.type.*;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum IslandAchievements {

    START(new IslandAchievement(0, new ItemStack(Material.APPLE), "START")),
    RESTART(new IslandAchievement(1, new ItemStack(Material.BEACON), "RESTART")),
    COBBLE_WALL(new IslandCraftAchievement(2, new ItemStack(Material.COBBLE_WALL), 24)),
    TORCH(new IslandCraftAchievement(3, new ItemStack(Material.TORCH), 30)),
    SMOOTH_BRICK(new IslandCraftAchievement(4, new ItemStack(Material.SMOOTH_BRICK), 64)),
    ARMOR_STAND(new IslandCraftAchievement(5, new ItemStack(Material.ARMOR_STAND), 10)),
    WOOD_STAIRS(new IslandCraftAchievement(6, new ItemStack(Material.WOOD_STAIRS), 20)),
    WOOD_STEP(new IslandCraftAchievement(7, new ItemStack(Material.WOOD_STEP), 20)),
    SIGN(new IslandCraftAchievement(8, new ItemStack(Material.SIGN), 10)),
    LEVER(new IslandCraftAchievement(9, new ItemStack(Material.LEVER), 10)),
    WOOD_PLATE(new IslandCraftAchievement(10, new ItemStack(Material.WOOD_PLATE), 10)),
    STONE_PLATE(new IslandCraftAchievement(11, new ItemStack(Material.STONE_PLATE), 10)),
    WOOD_BUTTON(new IslandCraftAchievement(12, new ItemStack(Material.WOOD_BUTTON), 10)),
    STONE_BUTTON(new IslandCraftAchievement(13, new ItemStack(Material.STONE_BUTTON), 10)),
    FERMENTED_SPIDER_EYE(new IslandCraftAchievement(14, new ItemStack(Material.FERMENTED_SPIDER_EYE), 10)),
    PAINTING(new IslandCraftAchievement(15, new ItemStack(Material.PAINTING), 10)),
    CARROT_STICK(new IslandCraftAchievement(16, new ItemStack(Material.CARROT_STICK))),
    BREAD(new IslandCraftAchievement(17, new ItemStack(Material.BREAD), 10)),
    HAY_BLOCK(new IslandCraftAchievement(18, new ItemStack(Material.HAY_BLOCK), 30)),
    REDSTONE_LAMP_OFF(new IslandCraftAchievement(19, new ItemStack(Material.REDSTONE_LAMP_OFF), 10)),
    PUMPKIN_PIE(new IslandCraftAchievement(20, new ItemStack(Material.PUMPKIN_PIE), 8)),
    BEETROOT_SOUP(new IslandCraftAchievement(21, new ItemStack(Material.BEETROOT_SOUP), 10)),
    BRICK(new IslandCraftAchievement(22, new ItemStack(Material.BRICK), 64)),
    LAPIS_BLOCK(new IslandCraftAchievement(23, new ItemStack(Material.LAPIS_BLOCK), 64)),
    CONCRETE_POWDER(new IslandCraftAchievement(24, new ItemStack(Material.CONCRETE_POWDER), 8)),
    DIODE(new IslandCraftAchievement(25, new ItemStack(Material.DIODE), 32)),
    PISTON_BASE(new IslandCraftAchievement(26, new ItemStack(Material.PISTON_BASE), 10)),
    COMPASS(new IslandCraftAchievement(27, new ItemStack(Material.COMPASS), 5)),
    TNT(new IslandCraftAchievement(28, new ItemStack(Material.TNT), 10)),
    QUARTZ_BLOCK(new IslandCraftAchievement(29, new ItemStack(Material.QUARTZ_BLOCK), 64)),
    REDSTONE_COMPARATOR(new IslandCraftAchievement(30, new ItemStack(Material.REDSTONE_COMPARATOR), 24)),
    BEACON(new IslandCraftAchievement(31, new ItemStack(Material.BEACON))),
    SLIME_BLOCK(new IslandCraftAchievement(32, new ItemStack(Material.SLIME_BLOCK))),
    EMPTY_MAP(new IslandCraftAchievement(33, new ItemStack(Material.EMPTY_MAP), 10)),
    ENCHANTMENT_TABLE(new IslandCraftAchievement(34, new ItemStack(Material.ENCHANTMENT_TABLE))),
    BOOKSHELF(new IslandCraftAchievement(35, new ItemStack(Material.BOOKSHELF), 32)),
    BONE_BLOCK(new IslandCraftAchievement(36, new ItemStack(Material.BONE_BLOCK), 20)),
    FURNACE(new IslandCraftAchievement(37, new ItemStack(Material.FURNACE))),
    GOLDEN_APPLE(new IslandCraftAchievement(38, new ItemStack(Material.GOLDEN_APPLE))),
    OBSIDIAN(new IslandBreakAchievement(39, new ItemStack(Material.OBSIDIAN), 32)),
    GLASS(new IslandFurnaceAchievement(40, new ItemStack(Material.GLASS), 32)),
    GOLD_INGOT(new IslandFurnaceAchievement(41, new ItemStack(Material.GOLD_INGOT), 64)),
    IRON_INGOT(new IslandFurnaceAchievement(42, new ItemStack(Material.IRON_INGOT), 64)),
    BAKED_POTATO(new IslandFurnaceAchievement(43, new ItemStack(Material.BAKED_POTATO), 32)),
    GENERATOR(new IslandAchievement(44, new ItemStack(Material.COBBLESTONE), "GENERATOR")),
    COW(new IslandBreedAchievement(45, EntityType.COW, 2)),
    SHEEP(new IslandBreedAchievement(46, EntityType.SHEEP, 2)),
    CHICKEN(new IslandBreedAchievement(47, EntityType.CHICKEN, 2)),
    PIG(new IslandBreedAchievement(48, EntityType.PIG, 2)),
    ;

    /*

44. Скрафтите сет алмазный брони. //todo сделать
15. Скрафтите 10 баннеров и поставьте их.
17. Скрафтите 10 черной шерсти.
18. Скрафтите 10 серой шерсти.
19. Скрафтите 10 красная шерсть.
20. Скрафтите 10 желтая шерсть.
21. Скрафтите 10 розовой шерсти.
22. Скрафтите 10 зеленая шерсть.
23. Скрафтите 10 оранжевой шерсти.
24. Скрафтите лук и 64 стрелы.
25. Скрафтите и возьмите в руку щит.
54. Соберите 64 булыжника.
55. Соберите 16 яблок.
56. Соберите все музыкальные диски.
57. Соберите по 32 пороха, гнилой плоти, костей и глаз пауков.
58. Соберите 24 жемчужин ендера.
59. Соберите 10 ядовитых картофелей.
60. Соберите все достижения связанные с крафтом цветной шерсти.
61. Соберите 50 березовой древесины.
62. Соберите 50 джунглевых древесины.
63. Соберите 64 фрукта хоруса.
64. Посадите 16 картофелей и соберите 32 картофеля.
65. Посадите 16 моркови и соберите 32 моркови.
66. Посадите 16 семян пшеницы и соберите 64 пшеницы.
67. Посадите 12 семян арбуза и соберите 64 арбуза.
68. Посадите 10 кактусов.
69. Посадите 12 семян тыквы и соберите 32 тыквы.
70. Посадите 8 нижних бородавок и соберите 32 бородавки.
71. Посадите 16 сахарного тростника и соберите 64 сахарных тростника.
72. Посадите 15 красных и 15 коричневых грибов.
73. Посадите 16 свекольных семян и соберите 32 свеклы.
74. Постройте железного голема.
75. Постройте снежного голема.
79. Поймайте по 10 рыб: лососей, клоунов и пуфферфишей.
80. Приготовьте 10 рыб и лосося.
82. Возьмите кровать и поспите в ней.
83. Поставьте наковальню.
84. Съешьте 10 красных и коричневых грибов.
85. Сделайте 64 печенья и съешьте их.
87. Поторгуйтесь с жителем деревни.
88. Наторгуйте 64 изумруда от жителя.
89. Спасите жителя деревни, вылечив зомби.
90. Увеличьте маяк до максимума.
91. Заимейте элитру.
92. Сделайте первую покупку в магазине на спавне
93. Добавьте друга на остров
95. Откройте все доступные улучшения и биомы для вашего острова
96. Упадите в пустоту
97. Накопите 1000 монет
98. Накопите 10000 монет
99. Накопите 100000 монет
100. Получите все 99 ачивок!
     */

    private static final Map<Material, IslandCraftAchievement> CRAFT_ACHIEVEMENTS = new HashMap<>();
    private static final Map<Material, IslandBreakAchievement> BREAK_ACHIEVEMENTS = new HashMap<>();
    private static final Map<Material, IslandFurnaceAchievement> FURNACE_ACHIEVEMENTS = new HashMap<>();
    private static final Map<EntityType, IslandBreedAchievement> BREED_ACHIEVEMENTS = new HashMap<>();

    private final Achievement achievement;

    public int getId() {
        return achievement.getId();
    }

    public static List<Achievement> getAchievements() {
        return Arrays.stream(values())
                .map(islandAchievements -> islandAchievements.achievement)
                .collect(Collectors.toList());
    }

    public static IslandCraftAchievement getCraftAchievement(ItemStack type) {
        return CRAFT_ACHIEVEMENTS.get(type.getType());
    }

    public static IslandBreakAchievement getBreakAchievement(Material material) {
        return BREAK_ACHIEVEMENTS.get(material);
    }

    public static IslandFurnaceAchievement getFurnaceAchievement(Material material) {
        return FURNACE_ACHIEVEMENTS.get(material);
    }

    public static IslandBreedAchievement getBreedAchievement(EntityType entityType) {
        if (entityType == EntityType.MUSHROOM_COW)
            return BREED_ACHIEVEMENTS.get(EntityType.COW);

        return BREED_ACHIEVEMENTS.get(entityType);
    }

    static {
        for (IslandAchievements islandAchievements : values()) {
            Achievement achievement = islandAchievements.achievement;
            if (achievement instanceof IslandCraftAchievement) {
                IslandCraftAchievement islandCraftAchievement = (IslandCraftAchievement) achievement;
                CRAFT_ACHIEVEMENTS.put(islandCraftAchievement.getItem().getType(), islandCraftAchievement);
                continue;
            }
            if (achievement instanceof IslandBreakAchievement) {
                IslandBreakAchievement islandBreakAchievement = (IslandBreakAchievement) achievement;
                BREAK_ACHIEVEMENTS.put(islandBreakAchievement.getItem().getType(), islandBreakAchievement);
                continue;
            }
            if (achievement instanceof IslandFurnaceAchievement) {
                IslandFurnaceAchievement islandFurnaceAchievement = (IslandFurnaceAchievement) achievement;
                FURNACE_ACHIEVEMENTS.put(islandFurnaceAchievement.getItem().getType(), islandFurnaceAchievement);
                continue;
            }
            if (achievement instanceof IslandBreedAchievement) {
                IslandBreedAchievement islandBreedAchievement = (IslandBreedAchievement) achievement;
                BREED_ACHIEVEMENTS.put(islandBreedAchievement.getEntityType(), islandBreedAchievement);
                continue;
            }
        }
    }
}
