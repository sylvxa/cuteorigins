package lol.sylvie.cuteorigins.gui.picker;

import lol.sylvie.cuteorigins.origin.Origin;
import lol.sylvie.cuteorigins.power.Power;
import lol.sylvie.cuteorigins.state.StateManager;
import lol.sylvie.cuteorigins.util.OriginRegistries;
import net.minecraft.server.level.ServerPlayer;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.connection.GeyserConnection;

import java.util.List;

public class BedrockOriginPicker {
    protected static CustomForm getOriginInfoGui(ServerPlayer player, Origin origin) {
        CustomForm.Builder builder = CustomForm.builder()
                .title(origin.getName().getString());

        builder.label(origin.getDescription().getString());
        for (Power power : origin.getDisplayPowers()) {
            builder.label("§l" + (power.isNegative() ? "§c" : "§f") + power.getName().getString());
            builder.label(power.getDescription().getString());
        }

        builder.validResultHandler((customForm, customFormResponse) -> StateManager.getPlayerState(player).setOrigin(player, origin));

        builder.closedOrInvalidResultHandler(() -> openForBedrockPlayers(player));

        return builder.build();
    }

    protected static SimpleForm getOriginListGui(ServerPlayer player, GeyserConnection connection) {
        List<Origin> origins = OriginRegistries.ORIGIN_REGISTRY.getOriginsAlphabetically();
        SimpleForm.Builder builder = SimpleForm.builder().title("Select an Origin");

        for (Origin origin : origins) {
            builder.button(origin.getName().getString());
        }

        builder.validResultHandler(response -> {
            int selectedIndex = response.clickedButtonId();
            CustomForm infoGui = getOriginInfoGui(player, origins.get(selectedIndex));
            connection.sendForm(infoGui);
        });

        builder.closedOrInvalidResultHandler(() -> openForBedrockPlayers(player));

        return builder.build();
    }

    public static boolean openForBedrockPlayers(ServerPlayer player) {
        GeyserConnection connection = GeyserApi.api().connectionByUuid(player.getUUID());
        if (connection == null) return false;

        SimpleForm gui = getOriginListGui(player, connection);
        connection.sendForm(gui);
        return true;
    }
}
