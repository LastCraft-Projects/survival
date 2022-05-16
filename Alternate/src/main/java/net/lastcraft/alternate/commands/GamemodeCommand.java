package net.lastcraft.alternate.commands;

import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.locale.Language;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Locale;

public class GamemodeCommand extends AlternateCommand {

    public GamemodeCommand(ConfigData configData) {
        super(configData, true, "gamemode", "adventure",
                "adventuremode", "creative", "creativemode", "gm", "gmc", "gms", "gma", "survival", "survivalmode");
        setMinimalGroup(configData.getInt("gamemodeCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String label, String[] args) {
        BukkitGamer gamer = ((BukkitGamer) gamerEntity);

        Player player;
        GameMode gameMode;

        if (args.length == 0) {
            gameMode = matchGameMode(label);

            if (gameMode == null) {
                COMMANDS_API.notEnoughArguments(gamer, "GAMEMODE_FORMAT");
                return;
            }

            player = gamer.getPlayer();
        } else if (args.length == 1) {
            gameMode = matchGameMode(args[0]);
            player = gamer.getPlayer();
            if (gameMode == null && gamer.getGroup() == Group.ADMIN) {
                gameMode = matchGameMode(label);

                if (gameMode == null) {
                    COMMANDS_API.notEnoughArguments(gamer, "GAMEMODE_FORMAT");
                    return;
                }

                player = Bukkit.getPlayer(args[0]);
                if (player == null) {
                    COMMANDS_API.playerOffline(gamerEntity, args[0]);
                    return;
                }
            } else if (gameMode == null){
                COMMANDS_API.notEnoughArguments(gamer, "GAMEMODE_FORMAT");
                return;
            }
        } else if (args.length == 2 && gamer.getGroup() == Group.ADMIN) {
            gameMode = matchGameMode(args[0].toLowerCase(Locale.ENGLISH));

            if(gameMode == null){
                player = Bukkit.getPlayer(args[0]);
            } else {
                player = Bukkit.getPlayer(args[1]);
            }
        } else {
            player = gamer.getPlayer();
            gameMode = player.getGameMode();
        }

        assert player != null;
        if(!gamer.getName().equals(player.getName())){
            String modeKey = "SURVIVAL_MODE";
            Language lang = gamerEntity.getLanguage();
            if(gameMode == GameMode.CREATIVE){
                modeKey = "CREATIVE_MODE";
            } else if(gameMode == GameMode.ADVENTURE) {
                modeKey = "ADVENTURE_MODE";
            }
            sendMessageLocale(gamerEntity, "GAMEMODE_TO",
                    player.getDisplayName(),
                    lang.getMessage(modeKey));
        }

        if (gameMode != null){
            String modeKey = "SURVIVAL_MODE";
            gamer = GAMER_MANAGER.getGamer(player);
            if (gamer == null)
                return;
            Language lang = gamer.getLanguage();
            if(gameMode == GameMode.CREATIVE)
                modeKey = "CREATIVE_MODE";
            else if(gameMode == GameMode.ADVENTURE)
                modeKey = "ADVENTURE_MODE";

            sendMessageLocale(gamer, "GAMEMODE", lang.getMessage(modeKey));

            User user = USER_MANAGER.getUser(player);
            if (user == null)
                return;

            user.setGamemode(gameMode);
        }
    }

    private GameMode matchGameMode(String modeString) {
        GameMode mode;
        if (modeString.equalsIgnoreCase("gmc") || modeString.equalsIgnoreCase("egmc")
                || modeString.contains("creat") || modeString.equalsIgnoreCase("1") || modeString.equalsIgnoreCase("c")){
            mode = GameMode.CREATIVE;
        }
        else if (modeString.equalsIgnoreCase("gms") || modeString.equalsIgnoreCase("egms")
                || modeString.contains("survi") || modeString.equalsIgnoreCase("0") || modeString.equalsIgnoreCase("s")){
            mode = GameMode.SURVIVAL;
        }
        else if (modeString.equalsIgnoreCase("gma") || modeString.equalsIgnoreCase("egma")
                || modeString.contains("advent") || modeString.equalsIgnoreCase("2") || modeString.equalsIgnoreCase("a")){
            mode = GameMode.ADVENTURE;
        } else {
            mode = null;
        }

        return mode;
    }
}
