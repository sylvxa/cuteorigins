package lol.sylvie.cuteorigins.gui;

import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.gui.binds.BedrockOriginBindMenu;
import lol.sylvie.cuteorigins.gui.binds.JavaOriginBindMenu;
import lol.sylvie.cuteorigins.gui.picker.BedrockOriginPicker;
import lol.sylvie.cuteorigins.gui.picker.JavaOriginPicker;
import net.minecraft.server.level.ServerPlayer;

public class OriginGui {
    private static boolean isGeyserAvailable = false;
    static {
        try {
            Class.forName("org.geysermc.geyser.api.GeyserApi");
            isGeyserAvailable = true;
            CuteOrigins.LOGGER.info("Enabling Geyser functionality!");
        } catch (ClassNotFoundException ignored) {}
    }

    public static void openPicker(ServerPlayer player) {
        if (isGeyserAvailable && BedrockOriginPicker.openForBedrockPlayers(player)) return;
        JavaOriginPicker.open(player);
    }

    public static void openBinds(ServerPlayer player) {
        if (isGeyserAvailable && BedrockOriginBindMenu.openForBedrockPlayers(player)) return;
        JavaOriginBindMenu.open(player);
    }
}
