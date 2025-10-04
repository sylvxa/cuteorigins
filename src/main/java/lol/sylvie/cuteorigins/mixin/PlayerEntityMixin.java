package lol.sylvie.cuteorigins.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import lol.sylvie.cuteorigins.mixininterfaces.Phasable;
import lol.sylvie.cuteorigins.origin.Origin;
import lol.sylvie.cuteorigins.power.effect.Effect;
import lol.sylvie.cuteorigins.power.effect.impl.*;
import lol.sylvie.cuteorigins.state.StateManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    // Damage logic
    @ModifyArgs(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/PlayerLikeEntity;damage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    public void origins$modifyDamage(Args args) {
        DamageSource source = args.get(1);

        PlayerEntity thisPlayer = (PlayerEntity) (Object) this;
        if (!(thisPlayer instanceof ServerPlayerEntity player)) return;
        Origin origin = StateManager.getPlayerState(player).getOrigin();
        if (origin == null) return;

        for (Effect effect : origin.getEffectsOfType(DamageMultiplierEffect.class)) {
            DamageMultiplierEffect multiplierEffect = (DamageMultiplierEffect) effect;
            RegistryKey<DamageType> damageType = multiplierEffect.getDamageType(player).getKey().orElse(null);
            if ((source.isOf(damageType) || damageType == null) && multiplierEffect.getCondition().test(player)) {
                float amount = args.get(2);
                args.set(2, amount * multiplierEffect.getMultiplier());
            }
        }
    }

    @Inject(method = "isInvulnerableTo", at = @At("RETURN"), cancellable = true)
    public void origins$isInvulnerableTo(ServerWorld world, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity thisPlayer = (PlayerEntity) (Object) this;
        if (!(thisPlayer instanceof ServerPlayerEntity player)) return;
        Origin origin = StateManager.getPlayerState(player).getOrigin();
        if (origin == null) return;

        for (Effect effect : origin.getEffectsOfType(DamageImmunityEffect.class)) {
            DamageImmunityEffect multiplierEffect = (DamageImmunityEffect) effect;
            if (multiplierEffect.isImmuneTo(thisPlayer, source.getType())) {
                cir.setReturnValue(true);
                return;
            }
        }
    }

    // Attack logic
    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 3)
    public float origins$modifyAttack(float i) {
        PlayerEntity thisPlayer = (PlayerEntity) (Object) this;
        if (!(thisPlayer instanceof ServerPlayerEntity player)) return i;
        Origin origin = StateManager.getPlayerState(player).getOrigin();
        if (origin == null) return i;

        for (Effect effect : origin.getEffectsOfType(DamageBonusEffect.class)) {
            DamageBonusEffect multiplierEffect = (DamageBonusEffect) effect;
            if (multiplierEffect.getCondition().test(player)) {
                i = i * multiplierEffect.getMultiplier();
            }
        }

        return i;
    }

    @ModifyReturnValue(method = "canHarvest", at = @At("RETURN"))
    public boolean origins$canHarvest(boolean original, @Local(argsOnly = true) BlockState blockState) {
        PlayerEntity thisPlayer = (PlayerEntity) (Object) this;
        if (!(thisPlayer instanceof ServerPlayerEntity player)) return original;
        Origin origin = StateManager.getPlayerState(player).getOrigin();
        if (origin == null) return original;

        for (Effect effect : origin.getEffectsOfType(ModifyHarvestEffect.class)) {
            ModifyHarvestEffect harvestEffect = (ModifyHarvestEffect) effect;
            Block block = blockState.getBlock();
            if (harvestEffect.inWhitelist(block)) {
                return true;
            } else if (harvestEffect.inBlackList(block)) {
                return false;
            }
        }
        return original;
    }

    // Removes some cost from the enchantment table
    // I know that capping the cost at the experience level can result in some cheesing, but it's very unlikely we get there anyways
    @ModifyVariable(method = "applyEnchantmentCosts", at = @At("HEAD"), argsOnly = true)
    public int origins$addEnchantmentCostOffset(int originalCost) {
        PlayerEntity thisPlayer = (PlayerEntity) (Object) this;
        if (!(thisPlayer instanceof ServerPlayerEntity player)) return originalCost;
        Origin origin = StateManager.getPlayerState(player).getOrigin();
        if (origin == null) return originalCost;

        int sumOffset = origin.getEffectsOfType(EnchantmentDiscountEffect.class).stream().mapToInt(m -> ((EnchantmentDiscountEffect) m).getDiscount()).sum();
        return Math.min(Math.max(originalCost - sumOffset, 0), player.experienceLevel); // A negative value will add xp, which I don't see much point in.
    }

    @ModifyReturnValue(method = "isSpectator", at = @At("RETURN"))
    public boolean origins$disableCollision(boolean original) {
        PlayerEntity thisPlayer = (PlayerEntity) (Object) this;
        if (!(thisPlayer instanceof Phasable player)) return original;
        return original || player.origins$isPhasing();
    }
}
