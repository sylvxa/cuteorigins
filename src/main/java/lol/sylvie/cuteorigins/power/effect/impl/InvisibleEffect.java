package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.condition.Condition;
import lol.sylvie.cuteorigins.power.effect.Effect;
import net.minecraft.resources.Identifier;

public class InvisibleEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("invisible");
    private final Condition condition;

    public InvisibleEffect(Condition condition) {
        super(IDENTIFIER, false);
        this.condition = condition;
    }

    public Condition getCondition() {
        return condition;
    }

    public static Effect fromJson(JsonObject object) {
        return new CannotSeeEffect(Condition.fromJson(object.getAsJsonObject("condition")));
    }
}
