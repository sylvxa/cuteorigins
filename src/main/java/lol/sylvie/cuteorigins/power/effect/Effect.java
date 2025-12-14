package lol.sylvie.cuteorigins.power.effect;

import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public abstract class Effect {
    private final Identifier identifier;
    private final boolean hasAction;

    protected Effect(Identifier identifier, boolean hasAction) {
        this.identifier = identifier;
        this.hasAction = hasAction;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public static Effect fromJson(JsonObject object) throws IllegalAccessException {
        throw new IllegalAccessException("Do not call Effect::fromJson, call a subclasses fromJson. (you probably forgot to specify one!)");
    }

    public void onRespawn(ServerPlayer player) {}
    public void onTick(ServerPlayer player) {}
    public InteractionResult onAttack(Player player, Entity target) {
        return InteractionResult.PASS;
    }

    public boolean hasAction() {
        return hasAction;
    }

    public void onAction(ServerPlayer player) {
        if (!hasAction) throw new RuntimeException("Attempted to use action when power type has no action.");
    }

    public void onChosen(ServerPlayer player) {}
    public void onRemoved(ServerPlayer player) {}
}
