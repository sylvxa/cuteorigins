package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.effect.Effect;
import net.minecraft.util.Identifier;

public class ScareCreeperEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("scare_creepers");

    protected ScareCreeperEffect() {
        super(IDENTIFIER, false);
    }

    public static Effect fromJson(JsonObject object) {
        return new ScareCreeperEffect();
    }
}
