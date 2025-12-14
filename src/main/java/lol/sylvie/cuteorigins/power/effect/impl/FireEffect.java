package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.condition.Condition;
import lol.sylvie.cuteorigins.power.effect.Effect;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class FireEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("fire");

    private final Condition condition;

    protected FireEffect(Condition condition) {
        super(IDENTIFIER, false);
        this.condition = condition;
    }

    @Override
    public void onTick(ServerPlayer player) {
        if (this.condition.test(player)) {
            player.igniteForTicks(40);
        }
    }

    public static Effect fromJson(JsonObject object) {
        return new FireEffect(Condition.fromJson(object.getAsJsonObject("condition")));
    }
}
