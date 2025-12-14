package lol.sylvie.cuteorigins.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import lol.sylvie.cuteorigins.origin.Origin;
import lol.sylvie.cuteorigins.power.effect.Effect;
import lol.sylvie.cuteorigins.power.effect.impl.ExhaustionEffect;
import lol.sylvie.cuteorigins.state.StateManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FoodData.class)
public class FoodDataMixin {
    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"))
    private float origins$hungerUpdate(float value, @Local(argsOnly = true) ServerPlayer player) {
        Origin origin = StateManager.getPlayerState(player).getOrigin();
        if (origin == null) return value;
        for (Effect effect : origin.getEffectsOfType(ExhaustionEffect.class)) {
            ExhaustionEffect multiplierEffect = (ExhaustionEffect) effect;
            value *= multiplierEffect.getMultiplier();
        }
        return value;
    }
}
