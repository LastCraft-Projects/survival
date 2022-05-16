package net.lastcraft.skyblock.api.island.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum MemberType {
    NOBODY(0, "NOBODY"), //никто
    OWNER(1, "ISLAND_OWNER"), //владелец
    CO_OWNER(2, "ISLAND_CO_OWNER"), //типа админ почти
    MEMBER(3, "ISLAND_MEMBER")  //просто может ебашить остров ;
    ;

    private final int data;
    private final String key;

    public static MemberType getMemberType(int data) {
        return Arrays.stream(values())
                .filter((container) -> container.getData() == data)
                .findFirst()
                .orElse(NOBODY);
    }

}
