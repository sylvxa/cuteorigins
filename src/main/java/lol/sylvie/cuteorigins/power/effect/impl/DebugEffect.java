package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.effect.Effect;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class DebugEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("debug");
    protected DebugEffect() {
        super(IDENTIFIER, true);
    }

    @Override
    public void onRespawn(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("Respawn event"));
    }

    @Override
    public InteractionResult onAttack(Player player, Entity target) {
        player.displayClientMessage(Component.literal("Attack event"), false);
        return InteractionResult.PASS;
    }

    @Override
    public void onChosen(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("Chosen event"));
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("Removed event"));
    }

    @Override
    public void onAction(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("Action event"));
    }

    @Override
    public void onTick(ServerPlayer player) {
        //player.sendMessage(Text.literal("Tick event"));
    }

    public static Effect fromJson(JsonObject object) {
        return new DebugEffect();
    }
}
