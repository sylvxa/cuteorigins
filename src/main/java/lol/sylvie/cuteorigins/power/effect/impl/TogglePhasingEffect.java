package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.mixininterfaces.Phasable;
import lol.sylvie.cuteorigins.power.effect.Effect;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class TogglePhasingEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("toggle_phasing");

    protected TogglePhasingEffect() {
        super(IDENTIFIER, true);
    }

    @Override
    public void onAction(ServerPlayer player) {
        Phasable phasable = player;
        if (!phasable.origins$canPhase()) {
            player.displayClientMessage(Component.translatable("message.cuteorigins.cannot_phase"), true);
            return;
        }

        boolean phasing = !phasable.origins$isPhasing();
        phasable.origins$setAndSyncPhasing(phasing);

        if (phasing) {
            player.push(0d, -0.1d, 0d);
            player.hurtMarked = true;
        }
    }

    public static Effect fromJson(JsonObject object) {
        return new TogglePhasingEffect();
    }
}
