package lol.sylvie.cuteorigins.mixin;

import lol.sylvie.cuteorigins.origin.Origin;
import lol.sylvie.cuteorigins.power.effect.Effect;
import lol.sylvie.cuteorigins.power.effect.impl.CannotSeeEffect;
import lol.sylvie.cuteorigins.power.effect.impl.InvisibleEffect;
import lol.sylvie.cuteorigins.state.StateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Unique
    private boolean isInvisibleTo(ServerPlayer observer) {
        Origin origin = StateManager.getPlayerState(observer).getOrigin();
        if (origin == null) return false;
        CannotSeeEffect effect = (CannotSeeEffect) origin.getFirstEffect(CannotSeeEffect.class);

        if (effect == null) return false;
        return effect.getCondition().test((Entity) (Object) this);
    }

    @Inject(method = "broadcastToPlayer", at = @At("HEAD"), cancellable = true)
    public void origins$broadcast(ServerPlayer spectator, CallbackInfoReturnable<Boolean> cir) {
        if (isInvisibleTo(spectator)) cir.setReturnValue(false);
    }

    @Inject(method = "isInvisibleTo", at = @At("HEAD"), cancellable = true)
    public void origins$invisibleTo(Player player, CallbackInfoReturnable<Boolean> cir) {
        if ((player instanceof ServerPlayer serverPlayer) && isInvisibleTo(serverPlayer)) cir.setReturnValue(false);
    }

    @Inject(method = "isInvisible", at = @At("HEAD"), cancellable = true)
    public void origins$invisible(CallbackInfoReturnable<Boolean> cir) {
        Entity thisEntity = (Entity) (Object) this;
        if (thisEntity instanceof ServerPlayer player) {
            Origin origin = StateManager.getPlayerState(player).getOrigin();
            if (origin == null) return;
            for (Effect effect : origin.getEffectsOfType(InvisibleEffect.class)) {
                InvisibleEffect invisibleEffect = (InvisibleEffect) effect;
                if (invisibleEffect.getCondition().test(player)) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
    }
}
