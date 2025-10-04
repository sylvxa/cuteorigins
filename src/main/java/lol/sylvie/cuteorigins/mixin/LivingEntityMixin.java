package lol.sylvie.cuteorigins.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import lol.sylvie.cuteorigins.origin.Origin;
import lol.sylvie.cuteorigins.power.effect.impl.ClimbAnywhereEffect;
import lol.sylvie.cuteorigins.power.effect.impl.WaterBreathingEffect;
import lol.sylvie.cuteorigins.state.StateManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow private Optional<BlockPos> climbingPos;

    @Shadow public abstract boolean addStatusEffect(StatusEffectInstance effect);

    @Unique
    public boolean canClimb(boolean original) {
        if (original) {
            return true;
        }

        LivingEntity living = (LivingEntity) (Object) this;
        if (!(living instanceof ServerPlayerEntity player)) return original;

        if (player.isSpectator() || !player.horizontalCollision) {
            return false;
        }

        Direction direction = player.getHorizontalFacing();
        BlockPos pos = player.getBlockPos().add(direction.getVector());
        if (player.getEntityWorld().getBlockState(pos).isAir()) {
            return false;
        }

        this.climbingPos = Optional.of(player.getBlockPos());
        return true;
    }

    @ModifyReturnValue(method = "isClimbing", at = @At("RETURN"))
    private boolean origins$isClimbing(boolean original) {
        LivingEntity living = (LivingEntity) (Object) this;
        if (!(living instanceof ServerPlayerEntity player)) return original;

        Origin origin = StateManager.getPlayerState(player).getOrigin();
        if (origin == null) return original;

        boolean modified = canClimb(original);

        if (modified && origin.getFirstEffect(ClimbAnywhereEffect.class) != null && !player.isSneaking() && !original) {
            addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 6, 1, false, false, false));
        }

        return modified;
    }

    @ModifyReturnValue(method = "canBreatheInWater", at = @At("RETURN"))
    private boolean origins$canBreatheInWater(boolean original) {
        LivingEntity living = (LivingEntity) (Object) this;
        if (!(living instanceof ServerPlayerEntity player)) return original;

        Origin origin = StateManager.getPlayerState(player).getOrigin();
        if (origin == null) return original;
        return original || origin.getFirstEffect(WaterBreathingEffect.class) != null;
    }
}
