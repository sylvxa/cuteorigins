package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.condition.Condition;
import lol.sylvie.cuteorigins.power.effect.Effect;
import net.minecraft.resources.Identifier;

public class DamageBonusEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("damage_bonus");
    private final float multiplier;
    private final Condition condition;

    public DamageBonusEffect(float multiplier, Condition condition) {
        super(IDENTIFIER, false);
        this.multiplier = multiplier;
        this.condition = condition;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public Condition getCondition() {
        return condition;
    }

    public static Effect fromJson(JsonObject object) {
        return new DamageBonusEffect(object.get("multiplier").getAsFloat(), Condition.fromJson(object.getAsJsonObject("condition")));
    }
}
