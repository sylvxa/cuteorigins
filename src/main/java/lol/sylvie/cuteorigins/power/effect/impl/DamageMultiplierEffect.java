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
import net.minecraft.world.damagesource.DamageType;

public class DamageMultiplierEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("damage_multiplier");
    private final float multiplier;
    private final Identifier damageType;
    private final Condition condition;

    public DamageMultiplierEffect(float multiplier, Identifier damageType, Condition condition) {
        super(IDENTIFIER, false);
        this.multiplier = multiplier;
        this.damageType = damageType;
        this.condition = condition;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public Holder<DamageType> getDamageType(ServerPlayer player) {
        Registry<DamageType> damageTypeRegistry = player.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE);
        return damageTypeRegistry.get(damageType).orElse(null);
    }

    public Condition getCondition() {
        return condition;
    }

    public static Effect fromJson(JsonObject object) {
        return new DamageMultiplierEffect(object.get("multiplier").getAsFloat(), JsonHelper.jsonStringToIdentifier(object.get("damage_type")), Condition.fromJson(object.getAsJsonObject("condition")));
    }
}
