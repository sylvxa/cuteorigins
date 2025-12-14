package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.effect.Effect;
import net.minecraft.resources.Identifier;

public class ExhaustionEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("exaustion_modifier");
    private final float multiplier;

    protected ExhaustionEffect(float multiplier) {
        super(IDENTIFIER, false);
        this.multiplier = multiplier;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public static Effect fromJson(JsonObject object) {
        return new ExhaustionEffect(object.get("multiplier").getAsFloat());
    }
}
