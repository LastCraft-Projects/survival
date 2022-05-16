package net.lastcraft.skyblock.command;

import com.google.common.collect.ImmutableList;
import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.CommandTabComplete;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.manager.GuiManager;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.sound.SoundAPI;
import net.lastcraft.api.sound.SoundType;
import net.lastcraft.api.util.ChatUtil;
import net.lastcraft.base.gamer.GamerBase;
import net.lastcraft.base.gamer.IBaseGamer;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.sql.GlobalLoader;
import net.lastcraft.base.util.Cooldown;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.base.util.TimeUtil;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import net.lastcraft.market.api.MarketAPI;
import net.lastcraft.market.api.MarketPlayer;
import net.lastcraft.market.api.MarketPlayerManager;
import net.lastcraft.skyblock.api.SkyBlockAPI;
import net.lastcraft.skyblock.api.SkyBlockGui;
import net.lastcraft.skyblock.api.entity.SkyGamer;
import net.lastcraft.skyblock.api.event.IslandAsyncRemoveEvent;
import net.lastcraft.skyblock.api.event.IslandRemoveMemberEvent;
import net.lastcraft.skyblock.api.island.Island;
import net.lastcraft.skyblock.api.island.IslandFlag;
import net.lastcraft.skyblock.api.island.member.MemberType;
import net.lastcraft.skyblock.api.manager.IslandManager;
import net.lastcraft.skyblock.api.manager.SkyGamerManager;
import net.lastcraft.skyblock.craftisland.CraftSkyGamer;
import net.lastcraft.skyblock.gui.AcceptGui;
import net.lastcraft.skyblock.gui.guis.*;
import net.lastcraft.skyblock.module.FlagModule;
import net.lastcraft.skyblock.module.HomeModule;
import net.lastcraft.skyblock.module.IgnoreModule;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class IslandCommand implements CommandInterface, CommandTabComplete {
    private final IslandManager manager = SkyBlockAPI.getIslandManager();
    private final GuiManager<SkyBlockGui> guiManager = SkyBlockAPI.getSkyGuiManager();
    private final SkyGamerManager skyGamerManager = SkyBlockAPI.getSkyGamerManager();
    private final GamerManager gamerManager = LastCraft.getGamerManager();
    private final MarketPlayerManager marketPlayerManager = MarketAPI.getMarketPlayerManager();
    private final SoundAPI soundAPI =  LastCraft.getSoundAPI();

    private final ImmutableList<String> stringsTabCompleteNoIsland = ImmutableList.of(
            "islands", "cancel", "accept", "create", "tp", "help"
    );
    private final ImmutableList<String> stringsTabCompleteHasIsland = ImmutableList.of(
            "islands", "addmoney", "tp", "ignore", "leave", "home", "flag", "biome", "help"
    );

    public IslandCommand() {
        SpigotCommand command = LastCraft.getCommandsAPI().register("is", this,
                "island", "skyblock", "start", "скайблок", "остров");
        command.setOnlyPlayers(true);
        command.setCommandTabComplete(this);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String cmd, String[] args) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();
        if (player == null)
            return;

        Language lang = gamer.getLanguage();

        SkyGamer skyGamer = skyGamerManager.getSkyGamer(player);
        if (skyGamer == null)
            return;

        int length = args.length;

        if (length < 1) {
            ProfileGui gui = guiManager.getGui(ProfileGui.class, player);
            if (gui != null)
                gui.open();
            return;
        }

        Island island = manager.getIsland(player);
        String command = args[0].toLowerCase();
        MemberType memberType = MemberType.NOBODY;
        if (island != null) {
            memberType = island.getMemberType(gamer);
        }

        switch (command.toLowerCase()) {
            case "destroy":
                if (island == null) {
                    gamer.sendMessageLocale("ISLAND_NO_ISLAND");
                    return;
                }
                if (!island.hasOwner(gamer)) {
                    gamer.sendMessageLocale("ISLAND_NOT_OWNER");
                    return;
                }

                if (Cooldown.hasCooldown(player.getName(), "deleteIsland")) {
                    gamer.sendMessageLocale("ISLAND_DESTROY_COOLDOWN", TimeUtil.leftTime(lang,
                                    Cooldown.getSecondCooldown(player.getName(), "deleteIsland") * 1000));
                    return;
                }

                new AcceptGui(player, AcceptGui.Type.DELETE).open(() -> {
                    BukkitUtil.runTaskAsync(() -> {
                        IslandAsyncRemoveEvent islandAsyncRemoveEvent = new IslandAsyncRemoveEvent(island);
                        BukkitUtil.callEvent(islandAsyncRemoveEvent);
                    });
                    Cooldown.addCooldown(player.getName(), "deleteIsland", 2 * 60 * 60 * 20L);
                    gamer.sendMessageLocale("ISLAND_REMOVED");
                }, null);

                break;
            case "islands":
            case "skyblocks":
            case "острова":
                Command islandsCommand = COMMANDS_API.getCommand("islands");
                islandsCommand.execute(player, cmd, args);
                break;
            case "accept":
            case "принять":
            case "request":
                Command acceptCommand = COMMANDS_API.getCommand("accept");
                acceptCommand.execute(player, cmd, args);
                break;
            case "cancel":
            case "отменить":
            case "против":
                Command cancelCommand = COMMANDS_API.getCommand("cancel");
                cancelCommand.execute(player, cmd, args);
                break;
            case "leave":
            case "покинуть":
                if (island == null) {
                    gamer.sendMessageLocale("ISLAND_NO_ISLAND");
                    return;
                }

                if (island.hasOwner(gamer)) {
                    gamer.sendMessageLocale("ISLAND_LEAVE_OWNER");
                    return;
                }

                new AcceptGui(player, AcceptGui.Type.LEAVE).open(() -> {
                    island.removePlayerFromIsland(player);
                    player.teleport(AlternateAPI.getSpawn());
                    gamer.sendMessageLocale("ISLAND_LEAVE", island.getOwner().getDisplayName());
                }, null);

                break;
            case "create":
            case "start":
            case "начать":
                if (island != null) {
                    gamer.sendMessageLocale("ISLAND_ALREADY_CREATE");
                    return;
                }

                ChoisedGui choisedGui = guiManager.getGui(ChoisedGui.class, player);
                if (choisedGui != null)
                    choisedGui.open();

                break;
            case "biome":
            case "биом":
                if (island == null) {
                    gamer.sendMessageLocale("ISLAND_NO_ISLAND");
                    return;
                }

                BiomeGui gui = guiManager.getGui(BiomeGui.class, player);
                if (gui != null)
                    gui.open();
                break;
            case "delete":
            case "remove":
            case "удалить":
                if (island == null) {
                    gamer.sendMessageLocale("ISLAND_NO_ISLAND");
                    return;
                }

                if (memberType == MemberType.MEMBER) {
                    gamer.sendMessageLocale("ISLAND_NOT_OWNER");
                    return;
                }

                if (args.length < 2) {
                    COMMANDS_API.notEnoughArguments(gamerEntity, "ISLAND_COMMAND_REMOVE_FORMAT");
                    return;
                }

                String removedName = args[1];
                Player removedPlayer = Bukkit.getPlayerExact(removedName);

                if (gamer.getName().equalsIgnoreCase(removedName)) {
                    gamer.sendMessageLocale("ISLAND_DELETE_MEMBER_OWNER");
                    return;
                }

                BukkitUtil.runTaskAsync(() -> {
                    IBaseGamer removedBaseGamer = null;
                    if (removedPlayer != null && removedPlayer.isOnline())
                        removedBaseGamer = gamerManager.getGamer(removedName);
                    else if (GlobalLoader.containsPlayerID(removedName) != -1)
                        removedBaseGamer = gamerManager.getOrCreate(removedName);

                    if (removedBaseGamer == null) {
                        COMMANDS_API.playerNeverPlayed(gamerEntity, removedName);
                        return;
                    }

                    Island targetIsland2 = manager.getIsland(removedBaseGamer.getPlayerID());
                    if (targetIsland2 == null || targetIsland2.getIslandID() != island.getIslandID()) {
                        gamer.sendMessageLocale("ISLAND_DELETE_MEMBER_ERROR");
                        return;
                    }

                    if (targetIsland2.hasOwner(removedBaseGamer)) {
                        gamer.sendMessageLocale("ISLAND_DELETE_MEMBER_ERROR2");
                        return;
                    }

                    IslandRemoveMemberEvent memberEvent = new IslandRemoveMemberEvent(island,
                            removedBaseGamer.getPlayerID());
                    BukkitUtil.callEvent(memberEvent);

                    if (memberEvent.isCancelled())
                        return;

                    island.removePlayerFromIsland(removedBaseGamer.getPlayerID());
                    gamer.sendMessageLocale("ISLAND_DELETE", removedBaseGamer.getDisplayName());

                    IBaseGamer finalRemovedBaseGamer = removedBaseGamer;
                    BukkitUtil.runTaskAsync(() -> {
                        island.broadcastMessageLocale("ISLAND_MEMBER_REMOVED",
                                finalRemovedBaseGamer.getDisplayName(), gamer.getDisplayName());
                        if (finalRemovedBaseGamer instanceof GamerBase) {
                            BukkitGamer removedGamer = (BukkitGamer) finalRemovedBaseGamer;
                            BukkitUtil.runTask(() -> removedGamer.getPlayer().teleport(AlternateAPI.getSpawn()));
                            removedGamer.sendMessageLocale("ISLAND_DELETE_TO", gamer.getDisplayName(),
                                    island.getOwner().getDisplayName());
                        }
                    });
                });

                break;
            case "home":
            case "go":
                HomeCommand.toHome(player, gamer, island);
                break;
            case "flag":
            case "флаг":
                if (island == null) {
                    gamer.sendMessageLocale("ISLAND_NO_ISLAND");
                    return;
                }

                FlagsGui flagGui = guiManager.getGui(FlagsGui.class, player);
                if (flagGui != null)
                    flagGui.open();
                break;
            case "sethome":
                if (island == null) {
                    gamer.sendMessageLocale("ISLAND_NO_ISLAND");
                    return;
                }

                if (memberType == MemberType.MEMBER || memberType == MemberType.NOBODY) {
                    gamer.sendMessageLocale( "ISLAND_NOT_OWNER");
                    return;
                }

                if (!player.getLocation().getWorld().getName().equalsIgnoreCase(SkyBlockAPI.getSkyBlockWorldName())) {
                    gamer.sendMessageLocale("ISLAND_SETHOME_ERROR");
                    return;
                }

                if (!island.canBuild(player, player.getLocation())) {
                    gamer.sendMessageLocale("ISLAND_SETHOME_ERROR");
                    return;
                }

                island.getModule(HomeModule.class).setHome(player.getLocation());
                gamer.sendMessageLocale("ISLAND_SETHOME");

                break;
            case "reset":
            case "пересоздать":
            case "restart":
                if (island == null) {
                    gamer.sendMessageLocale("ISLAND_NO_ISLAND");
                    return;
                }

                if (island.getOwner().getPlayerID() != gamer.getPlayerID()) {
                    gamer.sendMessageLocale( "ISLAND_NOT_OWNER");
                    return;
                }

                //if (!player.getLocation().getWorld().getName().equalsIgnoreCase(SkyBlockAPI.getSkyBlockWorldName())) {
                //    player.sendMessage("ты не в том мире");
                //    return;
                //}

                if (Cooldown.hasCooldown(player.getName(), "resetIsland")) {
                    gamer.sendMessageLocale("ISLAND_DESTROY_COOLDOWN", TimeUtil.leftTime(lang,
                            Cooldown.getSecondCooldown(player.getName(), "resetIsland") * 1000));
                    return;
                }

                ChoisedGui resetGui = guiManager.getGui(ChoisedGui.class, player);
                if (resetGui != null) {
                    resetGui.setReset(true);
                    resetGui.open();
                }

                break;
            case "add":
            case "addmember":
            case "invite":
            case "добавить":
                if (island == null) {
                    gamer.sendMessageLocale("ISLAND_NO_ISLAND");
                    return;
                }

                if (memberType == MemberType.MEMBER) {
                    gamer.sendMessageLocale("ISLAND_NOT_OWNER");
                    return;
                }

                if (args.length < 2) {
                    COMMANDS_API.notEnoughArguments(gamerEntity, "ISLAND_COMMAND_ADD_FORMAT");
                    return;
                }

                int limit = island.getLimitMembers();
                if (island.getMembers().size() - 1 >= limit) {
                    gamer.sendMessageLocale("ISLAND_ADDMEMBER_LIMIT", String.valueOf(limit));
                    return;
                }

                String targetName = args[1];
                Player target = Bukkit.getPlayerExact(targetName);
                if (target == null || !target.isOnline()) {
                    COMMANDS_API.playerOffline(gamerEntity, targetName);
                    return;
                }

                if (gamer.getName().equalsIgnoreCase(targetName)) {
                    gamer.sendMessageLocale("ISLAND_ADDMEMBER_YOU");
                    return;
                }

                Island targetIsland = manager.getIsland(targetName);
                if (targetIsland != null) {
                    gamer.sendMessageLocale("ISLAND_ADDMEMBER_ERROR");
                    return;
                }

                CraftSkyGamer targetSkyGamer = (CraftSkyGamer) skyGamerManager.getSkyGamer(targetName);
                BukkitGamer targetGamer = gamerManager.getGamer(targetName);
                if (targetGamer == null)
                    return;

                Language langTarget = targetGamer.getLanguage();

                if (!targetSkyGamer.addRequest(player)) {
                    gamer.sendMessageLocale("CALL_ERROR");
                    return;
                }

                targetGamer.sendMessage("");
                targetGamer.sendMessageLocale("ISLAND_ADD_PLAYER_REQUEST_1", player.getDisplayName());

                BaseComponent call_invite_2 = new TextComponent(langTarget.getMessage( "ISLAND_ADD_PLAYER_REQUEST_2"));
                BaseComponent[] showTextInvite2 = new BaseComponent[]{ChatUtil.getComponentFromList(langTarget.getList("CALL_HOVER_ACCEPT", player.getDisplayName()))};
                BaseComponent hover2 = new TextComponent("§c/accept " + player.getName() + langTarget.getMessage("HOVER"));
                hover2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, showTextInvite2));
                hover2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept " + player.getName()));
                call_invite_2.addExtra(hover2);
                targetGamer.sendMessage(call_invite_2);

                BaseComponent call_invite_3 = new TextComponent(langTarget.getMessage( "ISLAND_ADD_PLAYER_REQUEST_3"));
                BaseComponent[] showTextInvite3 = new BaseComponent[]{ChatUtil.getComponentFromList(langTarget.getList("CALL_HOVER_IGNORE", player.getDisplayName()))};
                BaseComponent hover3 = new TextComponent("§c/cancel " + player.getName() + langTarget.getMessage("HOVER"));
                hover3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, showTextInvite3));
                hover3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cancel " + player.getName()));
                call_invite_3.addExtra(hover3);
                targetGamer.sendMessage(call_invite_3);

                targetGamer.sendMessageLocale("ISLAND_ADD_PLAYER_REQUEST_4");
                targetGamer.sendMessage("");

                gamer.sendMessage(SkyBlockAPI.getPrefix() + lang.getMessage("ISALAND_ADD_REQUEST",
                        target.getDisplayName()));
                break;
            case "requests":
            case "запросы":
                if (island != null) {
                    gamer.sendMessageLocale("ISLAND_REQUESTS_ERROR");
                    return;
                }
                RequestIslandGui requestIslandGui = guiManager.getGui(RequestIslandGui.class, player);
                if (requestIslandGui != null)
                    requestIslandGui.open();
                break;
            case "ignore":
            case "addignore":
            case "ignored":
            case "заблокировать":
            case "блокировать":
                if (island == null) {
                    gamer.sendMessageLocale("ISLAND_NO_ISLAND");
                    return;
                }
                if (memberType == MemberType.MEMBER || memberType == MemberType.NOBODY) {
                    gamer.sendMessageLocale("ISLAND_NOT_OWNER");
                    return;
                }

                if (args.length < 2) {
                    IgnoreGui ignoreGui = guiManager.getGui(IgnoreGui.class, player);
                    if (ignoreGui != null)
                        ignoreGui.open();
                    return;
                }
                String ignoredName = args[1];
                Player ignoredPlayer = Bukkit.getPlayerExact(ignoredName);
                BukkitUtil.runTaskAsync(() -> {
                    IBaseGamer blockedGamer = null;
                    if (ignoredPlayer != null && ignoredPlayer.isOnline())
                        blockedGamer = gamerManager.getGamer(ignoredName);
                    else if (GlobalLoader.containsPlayerID(ignoredName) != -1)
                        blockedGamer = gamerManager.getOrCreate(ignoredName);

                    if (blockedGamer == null) {
                        COMMANDS_API.playerNeverPlayed(gamerEntity, ignoredName);
                        return;
                    }

                    IgnoreModule module = island.getModule(IgnoreModule.class);
                    if (module == null)
                        return;

                    if (module.getIgnoreList().containsKey(blockedGamer.getPlayerID())) {
                        gamer.sendMessageLocale("ISLAND_IGNORE_ALREADY");
                        return;
                    }

                    module.addIgnored(blockedGamer, gamer);
                    island.broadcastMessageLocale("ISLAND_ADD_IGNORE_PLAYER",
                            blockedGamer.getDisplayName(), gamer.getDisplayName());
                    if (ignoredPlayer == null || !ignoredPlayer.isOnline()
                            || !island.containsLocation(ignoredPlayer.getLocation()))
                        return;

                    BukkitUtil.runTask(() -> ignoredPlayer.teleport(AlternateAPI.getSpawn()));
                });
                break;
            case "tp":
            case "телепортироваться":
                if (args.length < 2) {
                    COMMANDS_API.notEnoughArguments(gamerEntity, "ISLAND_COMMAND_TP_FORMAT");
                    return;
                }
                String teleportName = args[1];
                Player teleportPlayer = Bukkit.getPlayerExact(teleportName);
                BukkitUtil.runTaskAsync(() -> {
                    IBaseGamer playerIsland = null;
                    if (teleportPlayer != null && teleportPlayer.isOnline()) {
                        playerIsland = gamerManager.getGamer(teleportName);
                    } else if (GlobalLoader.containsPlayerID(teleportName) != -1) {
                        playerIsland = gamerManager.getOrCreate(teleportName);
                    }

                    if (playerIsland == null) {
                        COMMANDS_API.playerNeverPlayed(gamerEntity, teleportName);
                        return;
                    }

                    Island islandTeleport = manager.getIsland(playerIsland);
                    if (islandTeleport == null) {
                        gamer.sendMessageLocale("ISLAND_NOT_FOUND", playerIsland.getDisplayName());
                        return;
                    }

                    IgnoreModule ignoreModule = islandTeleport.getModule(IgnoreModule.class);
                    if (ignoreModule != null) {
                        if (ignoreModule.getIgnoreList().containsKey(gamer.getPlayerID())) {
                            soundAPI.play(player, SoundType.NO);
                            gamer.sendMessageLocale("ISLAND_MEMBER_BLOCKED", islandTeleport.getOwner().getDisplayName());
                            return;
                        }
                    }

                    FlagModule flagModule = islandTeleport.getModule(FlagModule.class);
                    if (flagModule != null) {
                        if (!flagModule.isFlag(IslandFlag.OPENED) && !islandTeleport.hasMember(player)) {
                            gamer.sendMessageLocale("ISLAND_CLOSED");
                            soundAPI.play(player, SoundType.NO);
                            return;
                        }
                    }

                    HomeModule homeModule = islandTeleport.getModule(HomeModule.class);
                    if (homeModule == null) {
                        return;
                    }

                    BukkitUtil.runTask(() -> homeModule.teleport(player));
                });
                break;
            case "addmoney":
                if (island == null) {
                    gamer.sendMessageLocale("ISLAND_NO_ISLAND");
                    return;
                }
                if (args.length < 2) {
                    COMMANDS_API.notEnoughArguments(gamerEntity, "ISLAND_COMMAND_ADD_MONEY");
                    return;
                }
                int addMoney;
                try {
                    addMoney = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    gamer.sendMessageLocale("ISLAND_ADDMONEY_ERROR");
                    return;
                }
                if (addMoney < 1) {
                    gamer.sendMessageLocale("ISLAND_ADDMONEY_ERROR");
                    return;
                }
                MarketPlayer marketPlayer = marketPlayerManager.getMarketPlayer(player);
                if (marketPlayer == null)
                    return;

                final int addMoneyFinal = addMoney;
                if (!marketPlayer.changeMoney(-addMoneyFinal)) {
                    gamer.sendMessageLocale("ISLAND_ADDMONEY_NO_MONEY",
                            StringUtil.getNumberFormat((int) marketPlayer.getMoney()),
                            StringUtil.getCorrectWord((int) marketPlayer.getMoney(), "MONEY_1", lang));
                    return;
                }
                island.changeMoney(addMoneyFinal);
                island.getOnlineMembers().forEach(member -> {
                    BukkitGamer bukkitGamer = gamerManager.getGamer(member);
                    if (bukkitGamer != null) {
                        Language langMember = bukkitGamer.getLanguage();
                        bukkitGamer.sendMessageLocale("ISLAND_ADDMONEY", player.getDisplayName(),
                                StringUtil.getNumberFormat(addMoneyFinal),
                                StringUtil.getCorrectWord(addMoneyFinal, "MONEY_1", langMember));
                    }
                });
                break;
            default:
                if (island == null) {
                    ProfileGui profileGui = guiManager.getGui(ProfileGui.class, player);
                    if (profileGui != null)
                        profileGui.open();
                }
                COMMANDS_API.showHelp(gamerEntity, "/island", "ISLAND_COMMAND_HELP");
                break;
        }
    }


    @Override
    public List<String> getComplete(GamerEntity gamerEntity, String alias, String[] args) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();
        if (player == null) {
            return ImmutableList.of();
        }

        Island island = manager.getIsland(gamerEntity.getName());
        if (island == null) {
            if (args.length == 1) {
                return COMMANDS_API.getCompleteString(stringsTabCompleteNoIsland, args);
            }

            switch (args[0].toLowerCase()) {
                case "accept":
                case "принять":
                case "request":
                case "cancel":
                case "отменить":
                case "против":
                    CraftSkyGamer skyGamer = (CraftSkyGamer) skyGamerManager.getSkyGamer(gamerEntity.getName());
                    if (skyGamer == null) {
                        return ImmutableList.of();
                    }

                    return COMMANDS_API.getCompleteString(skyGamer.getRequests().keySet(), args);
                case "tp":
                case "телепортироваться":
                    return COMMANDS_API.getCompleteString(gamerManager.getGamerEntities().keySet(), args);
            }

            return ImmutableList.of();
        }

        if (args.length == 1) {
            List<String> complete = new ArrayList<>(stringsTabCompleteHasIsland);
            MemberType memberType = island.getMemberType(gamer);
            switch (memberType) {
                case OWNER:
                    complete.add("ignore");
                    complete.add("add");
                    complete.add("remove");
                    complete.add("sethome");
                    complete.add("reset");
                    complete.add("destroy");
                    break;
                case CO_OWNER:
                    complete.add("ignore");
                    complete.add("add");
                    complete.add("remove");
                    complete.add("sethome");
                    break;
            }

            return COMMANDS_API.getCompleteString(complete, args);
        }

        switch (args[0].toLowerCase()) {
            case "ignore":
            case "addignore":
            case "ignored":
            case "заблокировать":
            case "блокировать":
            case "delete":
            case "remove":
            case "удалить":
            case "add":
            case "addmember":
            case "invite":
            case "добавить":
            case "tp":
            case "телепортироваться":
                return COMMANDS_API.getCompleteString(gamerManager.getGamerEntities().keySet(), args);
            default:
                return ImmutableList.of();
        }
    }
}
