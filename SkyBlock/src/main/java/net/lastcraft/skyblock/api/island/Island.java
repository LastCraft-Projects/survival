package net.lastcraft.skyblock.api.island;

import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.gamer.IBaseGamer;
import net.lastcraft.skyblock.api.entity.IslandEntity;
import net.lastcraft.skyblock.api.island.member.IslandMember;
import net.lastcraft.skyblock.api.island.member.MemberType;
import net.lastcraft.skyblock.api.territory.IslandTerritory;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public interface Island {

    int getIslandID();

    /**
     * может ли игрок тут строить
     * @param player - кто строит тут
     * @param location - локация, где ломает блоки
     * @return - да или нет
     */
    boolean canBuild(Player player, Location location);
    boolean containsLocation(Location location);

    /**
     * участник острова игрок или нет
     */
    boolean hasMember(Player player);
    boolean hasMember(IBaseGamer gamer);
    boolean hasMember(IslandMember member);

    /**
     * узнать овнера
     * @return - овнер острова
     */
    IBaseGamer getOwner();
    boolean hasOwner(IBaseGamer gamer);
    boolean hasOwner(int playerID);
    boolean hasOwner(Player player);

    /**
     * получить территорию острова
     * @return - территория
     */
    IslandTerritory getTerritory();

    /**
     * получить всех мемебров этого острова
     * @param type - каких именно
     * @return - мемберы
     */
    List<IslandMember> getMembers(MemberType type);
    List<IslandMember> getMembers();

    /**
     * список игроков (оффлайн или онлайн, не суть важно)
     * получать нужно в другом потоке иначе жопа
     * @return - список
     */
    List<IBaseGamer> getGamerMembers();

    /**
     * какой схематик стоит на острове
     * @return - схематик
     */
    IslandType getIslandType();

    /**
     * добавить игрока на остров
     * @param player - игрок
     * @param memberType - тип игрока
     */
    void addPlayerToIsland(Player player, MemberType memberType);
    void removePlayerFromIsland(Player player);
    void removePlayerFromIsland(int playerID);

    void changeMemberType(IslandMember islandMember, MemberType newMemberType);

    /**
     * отослать сообщение всем онлайн игрокам
     * @param key - ключ
     * @param objects - что заменить
     */
    void broadcastMessageLocale(String key, Object... objects);

    /**
     * узнать кто он на острове
     * @param playerID - игрок
     * @return - кто он
     */
    MemberType getMemberType(int playerID);
    MemberType getMemberType(IBaseGamer gamer);

    /**
     * узнать биом острова
     * @return - биом
     */
    Biome getBiome();

    /**
     * установить биом острову
     * @param biome - биом
     */
    void setBiome(Biome biome);
    void resetBiome(); //поставить дефолтный биом

    /**
     * работа с деньгами острова
     * @return - баланс
     */
    int getMoney();
    boolean changeMoney(int money);

    /**
     * сколько человек может быть добавлено на остров (макс)
     * зависит от группы доната владельца острова
     * @return - лимит
     */
    int getLimitMembers();

    /**
     * добавить модуль
     * @param clazz - класс модуля
     */
    <T extends IslandModule> void addModule(Class<T> clazz);

    /**
     * список модулей
     * @return - модулей
     */
    Map<String, IslandModule> getModules();

    /**
     * получить модуль
     * @param clazz - класс модуля
     * @return - модуль
     */
    <T extends IslandModule> T getModule(Class<T> clazz);

    /**
     * получить всех энтити на этом острове
     * @return - список энтити
     */
    List<IslandEntity> getEntities();

    /**
     * получить всех онлайн игроков острова
     */
    List<Player> getOnlineMembers();
    List<BukkitGamer> getOnlineGamers();

    /**
     * получить уровень острова
     * @return - уровень
     */
    int getLevel();

    /**
     * отсетить теру под 0 и поставить стандартный биом (FOREST)
     */
    void reset(IslandType islandType);

    /**
     * удалить остров
     */
    void delete();
}
