package lol.sylvie.cuteorigins.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import lol.sylvie.cuteorigins.mixininterfaces.Phasable;
import lol.sylvie.cuteorigins.origin.Origin;
import lol.sylvie.cuteorigins.power.effect.Effect;
import lol.sylvie.cuteorigins.power.effect.impl.*;
import lol.sylvie.cuteorigins.state.StateManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "isInvulnerableTo", at = @At("RETURN"), cancellable = true)
    public void origins$isInvulnerableTo(ServerLevel world, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        Player thisPlayer = (Player) (Object) this;
        if (!(thisPlayer instanceof ServerPlayer player)) return;
        Origin origin = StateManager.getPlayerState(player).getOrigin();
        if (origin == null) return;

        for (Effect effect : origin.getEffectsOfType(DamageImmunityEffect.class)) {
            DamageImmunityEffect multiplierEffect = (DamageImmunityEffect) effect;
            if (multiplierEffect.isImmuneTo(thisPlayer, source.type())) {
                cir.setReturnValue(true);
                return;
            }
        }
    }

    // Attack logic
    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 3)
    public float origins$modifyAttack(float i) {
        Player thisPlayer = (Player) (Object) this;
        if (!(thisPlayer instanceof ServerPlayer player)) return i;
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

    @ModifyReturnValue(method = "hasCorrectToolForDrops", at = @At("RETURN"))
    public boolean origins$canHarvest(boolean original, @Local(argsOnly = true) BlockState blockState) {
        Player thisPlayer = (Player) (Object) this;
        if (!(thisPlayer instanceof ServerPlayer player)) return original;
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
    @ModifyVariable(method = "onEnchantmentPerformed", at = @At("HEAD"), argsOnly = true)
    public int origins$addEnchantmentCostOffset(int originalCost) {
        Player thisPlayer = (Player) (Object) this;
        if (!(thisPlayer instanceof ServerPlayer player)) return originalCost;
        Origin origin = StateManager.getPlayerState(player).getOrigin();
        if (origin == null) return originalCost;

        int sumOffset = origin.getEffectsOfType(EnchantmentDiscountEffect.class).stream().mapToInt(m -> ((EnchantmentDiscountEffect) m).getDiscount()).sum();
        return Math.min(Math.max(originalCost - sumOffset, 0), player.experienceLevel); // A negative value will add xp, which I don't see much point in.
    }

    @ModifyReturnValue(method = "isSpectator", at = @At("RETURN"))
    public boolean origins$disableCollision(boolean original) {
        Player thisPlayer = (Player) (Object) this;
        if (!(thisPlayer instanceof Phasable player)) return original;
        return original || player.origins$isPhasing();
    }

    @ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public float origins$modifyDamage(float value, @Local(argsOnly = true) DamageSource source) {
        Player thisPlayer = (Player) (Object) this;
        if (!(thisPlayer instanceof ServerPlayer player)) return value;
        Origin origin = StateManager.getPlayerState(player).getOrigin();
        if (origin == null) return value;

        for (Effect effect : origin.getEffectsOfType(DamageMultiplierEffect.class)) {
            DamageMultiplierEffect multiplierEffect = (DamageMultiplierEffect) effect;
            ResourceKey<DamageType> damageType = multiplierEffect.getDamageType(player).unwrapKey().orElse(null);
            if ((damageType == null || source.is(damageType)) && multiplierEffect.getCondition().test(player)) {
                value *= multiplierEffect.getMultiplier();
            }
        }
        return value;
    }
}
