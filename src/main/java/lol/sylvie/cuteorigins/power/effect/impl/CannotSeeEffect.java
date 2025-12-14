package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.condition.Condition;
import lol.sylvie.cuteorigins.power.effect.Effect;
import net.minecraft.resources.Identifier;

public class CannotSeeEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("cannot_see");
    private final Condition condition;

    public CannotSeeEffect(Condition condition) {
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
