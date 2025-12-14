package lol.sylvie.cuteorigins.mixin;

import lol.sylvie.cuteorigins.origin.Origin;
import lol.sylvie.cuteorigins.power.effect.impl.ScareCreeperEffect;
import lol.sylvie.cuteorigins.state.StateManager;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public abstract class CreeperMixin extends Monster {
    protected CreeperMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @Unique
    private boolean scaresCreepers(LivingEntity living) {
        Origin origin = StateManager.getPlayerState(living).getOrigin();
        if (origin == null) return false;
        return origin.getFirstEffect(ScareCreeperEffect.class) != null;
    }

    @Inject(at = @At("TAIL"), method = "registerGoals")
    private void origins$addGoals(CallbackInfo ci) {
        Goal goal = new AvoidEntityGoal<>(this, Player.class, this::scaresCreepers, 6.0F, 1.0D, 1.2D, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);
        this.goalSelector.addGoal(3, goal);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V", ordinal = 8), method = "registerGoals")
    private void origins$redirectTargetGoal(GoalSelector instance, int priority, Goal goal) {
        Goal newGoal = new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, (target, world) -> !scaresCreepers(target));
        goalSelector.addGoal(priority, newGoal);
    }
}