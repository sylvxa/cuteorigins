package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.condition.Condition;
import lol.sylvie.cuteorigins.power.effect.Effect;
import lol.sylvie.cuteorigins.util.JsonHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import java.util.Objects;

public class PotionImmunityEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("potion_immunity");

    private final Identifier effect;

    protected PotionImmunityEffect(Identifier effect) {
        super(IDENTIFIER, false);
        this.effect = effect;
    }

    @Override
    public void onTick(ServerPlayer player) {
        Registry<MobEffect> statusEffects = player.registryAccess().lookupOrThrow(Registries.MOB_EFFECT);
        Holder<MobEffect> statusEffect = statusEffects.get(effect).orElseThrow();

        if (player.hasEffect(statusEffect)) {
            player.removeEffect(statusEffect);
        }
    }

    public static Effect fromJson(JsonObject object) {
        return new PotionImmunityEffect(JsonHelper.jsonStringToIdentifier(object.get("status_effect")));
    }
}
