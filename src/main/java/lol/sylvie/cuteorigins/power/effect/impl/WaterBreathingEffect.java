package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.effect.Effect;
import net.minecraft.resources.Identifier;

public class WaterBreathingEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("water_breathing");

    protected WaterBreathingEffect() {
        super(IDENTIFIER, false);
    }

    public static Effect fromJson(JsonObject object) {
        return new WaterBreathingEffect();
    }
}
