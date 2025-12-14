package lol.sylvie.cuteorigins.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import lol.sylvie.cuteorigins.origin.Origin;
import lol.sylvie.cuteorigins.power.effect.Effect;
import lol.sylvie.cuteorigins.power.effect.impl.ClimbAnywhereEffect;
import lol.sylvie.cuteorigins.power.effect.impl.DamageMultiplierEffect;
import lol.sylvie.cuteorigins.power.effect.impl.WaterBreathingEffect;
import lol.sylvie.cuteorigins.state.StateManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow private Optional<BlockPos> lastClimbablePos;

    @Shadow public abstract boolean addEffect(MobEffectInstance effect);

    @Unique
    public boolean canClimb(boolean original) {
        if (original) {
            return true;
        }

        LivingEntity living = (LivingEntity) (Object) this;
        if (!(living instanceof ServerPlayer player)) return original;

        if (player.isSpectator() || !player.horizontalCollision) {
            return false;
        }

        Direction direction = player.getDirection();
        BlockPos pos = player.blockPosition().offset(direction.getUnitVec3i());
        if (player.level().getBlockState(pos).isAir()) {
            return false;
        }

        this.lastClimbablePos = Optional.of(player.blockPosition());
        return true;
    }

    @ModifyReturnValue(method = "onClimbable", at = @At("RETURN"))
    private boolean origins$isClimbing(boolean original) {
        LivingEntity living = (LivingEntity) (Object) this;
        if (!(living instanceof ServerPlayer player)) return original;

        Origin origin = StateManager.getPlayerState(player).getOrigin();
        if (origin == null) return original;

        boolean modified = canClimb(original);

        if (modified && origin.getFirstEffect(ClimbAnywhereEffect.class) != null && !player.isShiftKeyDown() && !original) {
            addEffect(new MobEffectInstance(MobEffects.LEVITATION, 6, 1, false, false, false));
        }

        return modified;
    }

    @ModifyReturnValue(method = "canBreatheUnderwater", at = @At("RETURN"))
    private boolean origins$canBreatheInWater(boolean original) {
        LivingEntity living = (LivingEntity) (Object) this;
        if (!(living instanceof ServerPlayer player)) return original;

        Origin origin = StateManager.getPlayerState(player).getOrigin();
        if (origin == null) return original;
        return original || origin.getFirstEffect(WaterBreathingEffect.class) != null;
    }
}
