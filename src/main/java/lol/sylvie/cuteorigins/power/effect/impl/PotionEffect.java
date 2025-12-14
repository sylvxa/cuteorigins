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
import net.minecraft.world.effect.MobEffectInstance;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class PotionEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("potion_effect");

    private final int potency;
    private final Identifier effect;
    private final Condition condition;

    private final HashMap<UUID, Boolean> conditionLastTick = new HashMap<>();

    protected PotionEffect(int potency, Identifier effect, Condition condition) {
        super(IDENTIFIER, false);
        this.potency = potency;
        this.effect = effect;
        this.condition = condition;
    }

    private Holder<MobEffect> getStatusEffect(ServerPlayer player) {
        Registry<MobEffect> statusEffects = player.registryAccess().lookupOrThrow(Registries.MOB_EFFECT);
        return statusEffects.get(effect).orElseThrow();
    }

    @Override
    public void onTick(ServerPlayer player) {
        Holder<MobEffect> statusEffect = getStatusEffect(player);
        boolean hasEffect = player.hasEffect(statusEffect);
        boolean conditionResult = condition.test(player);
        UUID playerUuid = player.getUUID();
        if (conditionResult) {
            if (!hasEffect) player.addEffect(new MobEffectInstance(statusEffect, MobEffectInstance.INFINITE_DURATION, potency, false, false, false));
        } else {
            if (hasEffect && conditionLastTick.getOrDefault(playerUuid, false)) player.removeEffect(statusEffect);
        }
        conditionLastTick.put(playerUuid, conditionResult);
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        Holder<MobEffect> statusEffect = getStatusEffect(player);
        boolean hasEffect = player.hasEffect(statusEffect);
        if (hasEffect) player.removeEffect(statusEffect);
    }

    public static Effect fromJson(JsonObject object) {
        return new PotionEffect(object.get("potency").getAsInt(), JsonHelper.jsonStringToIdentifier(object.get("status_effect")), Condition.fromJson(object.getAsJsonObject("condition")));
    }
}
