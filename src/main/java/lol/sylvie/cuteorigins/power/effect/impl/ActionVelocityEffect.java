package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.effect.Effect;
import lol.sylvie.cuteorigins.util.JsonHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class ActionVelocityEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("action_velocity");

    private final Vec3 velocity;

    protected ActionVelocityEffect(Vec3 velocity) {
        super(IDENTIFIER, true);
        this.velocity = velocity;
    }

    @Override
    public void onAction(ServerPlayer player) {
        player.push(velocity);
        player.hurtMarked = true;
    }

    public static Effect fromJson(JsonObject object) {
        return new ActionVelocityEffect(JsonHelper.jsonListToVec3d(object.getAsJsonArray("velocity")));
    }
}
