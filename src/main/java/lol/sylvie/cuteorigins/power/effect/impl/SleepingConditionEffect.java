package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.condition.Condition;
import lol.sylvie.cuteorigins.power.effect.Effect;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class SleepingConditionEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("sleeping_condition");

    private final Condition condition;
    private final Component message;

    protected SleepingConditionEffect(Condition condition, Component message) {
        super(IDENTIFIER, false);
        this.condition = condition;
        this.message = message;
    }

    public Condition getCondition() {
        return condition;
    }

    public Component getMessage() {
        return message;
    }

    public static Effect fromJson(JsonObject object) {
        return new SleepingConditionEffect(Condition.fromJson(object.getAsJsonObject("condition")), Component.translatable(object.get("message").getAsString()));
    }
}
