package net.lastcraft.alternate.commands;

import net.lastcraft.alternate.api.AlternateAPI;
import net.lastcraft.alternate.api.AlternateGui;
import net.lastcraft.alternate.api.manager.UserManager;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.api.JSONMessageAPI;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.manager.GuiManager;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.locale.Language;

public abstract class AlternateCommand implements CommandInterface {
    protected static final GuiManager<AlternateGui> GUI_MANAGER = AlternateAPI.getGuiManager();
    protected static final JSONMessageAPI JSON_MESSAGE_API = LastCraft.getJsonMessageAPI();;
    protected static final UserManager USER_MANAGER = AlternateAPI.getUserManager();
    protected static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();

    private final String cooldownType = getClass().getSimpleName();
    protected final ConfigData configData;
    protected final SpigotCommand spigotCommand;

    protected AlternateCommand(ConfigData configData, boolean playersOnly, String name, String... aliases) {
        this.configData = configData;
        spigotCommand = COMMANDS_API.register(name, this, aliases);
        spigotCommand.setOnlyPlayers(playersOnly);
    }

    public String getCooldownType() {
        return cooldownType;
    }

    protected void setMinimalGroup(int level) {
        spigotCommand.setMinimalGroup(level);
    }

    protected void setMinimalGroup(Group group) {
        spigotCommand.setMinimalGroup(group);
    }

    protected void send(String key, GamerEntity gamerEntity, String displayName) {
        if (displayName == null)
            sendMessageLocale(gamerEntity, key);
        else
            sendMessageLocale(gamerEntity, key + "_TO", displayName);

    }

    public void sendMessage(GamerEntity gamerEntity, String message){
        gamerEntity.sendMessage(configData.getPrefix() + message);
    }

    public void sendMessageLocale(GamerEntity gamerEntity, String key, Object... objects){
        Language lang = gamerEntity.getLanguage();
        gamerEntity.sendMessage(configData.getPrefix() + lang.getMessage(key, objects));
    }
}
