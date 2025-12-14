package lol.sylvie.cuteorigins.gui.binds;

import lol.sylvie.cuteorigins.item.impl.KeybindItem;
import lol.sylvie.cuteorigins.origin.Origin;
import lol.sylvie.cuteorigins.power.Power;
import lol.sylvie.cuteorigins.state.StateManager;
import net.minecraft.server.level.ServerPlayer;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.connection.GeyserConnection;

import java.util.List;

public class BedrockOriginBindMenu {
    protected static SimpleForm getOriginBindMenu(ServerPlayer player) {
        SimpleForm.Builder builder = SimpleForm.builder().title("Grab Keybinds");

        Origin origin = StateManager.getPlayerState(player).getOrigin();
        if (origin == null) {
            return null;
        }

        List<Power> powers = origin.powers().stream().filter(power -> power.getEffect().hasAction()).toList();
        for (Power power : powers) {
            builder.button(power.getName().getString());
        }

        builder.validResultHandler(response -> {
            int selectedIndex = response.clickedButtonId();
            Power selectedPower = powers.get(selectedIndex);
            player.addItem(KeybindItem.getKeybind(selectedPower.getIdentifier()));
        });

        return builder.build();
    }

    public static boolean openForBedrockPlayers(ServerPlayer player) {
        GeyserConnection connection = GeyserApi.api().connectionByUuid(player.getUUID());
        if (connection == null) return false;

        SimpleForm gui = getOriginBindMenu(player);
        if (gui == null) return true;

        connection.sendForm(gui);
        return true;
    }
}
