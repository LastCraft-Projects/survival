package net.lastcraft.alternate.commands.tp;

import net.lastcraft.alternate.api.User;
import net.lastcraft.alternate.commands.AlternateCommand;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.api.player.GamerEntity;

public class TpToggleCommand extends AlternateCommand {

    public TpToggleCommand(ConfigData configData) {
        super(configData, true, "tptoggle");
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        User user = USER_MANAGER.getUser(gamerEntity.getName());
        if (user == null) {
            return;
        }

        boolean enable = !user.isTpToggle();
        user.setTpToggle(enable);

        sendMessageLocale(gamerEntity,(enable ? "TPTOGGLE_ENABLE" : "TPTOGGLE_DISABLE"));
    }
}
