package lol.sylvie.cuteorigins.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import lol.sylvie.cuteorigins.mixininterfaces.Phasable;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements Phasable {
    @Shadow public ServerPlayNetworkHandler networkHandler;
    @Shadow @Final public ServerPlayerInteractionManager interactionManager;

    @Shadow public abstract void sendAbilitiesUpdate();

    @Unique
    private boolean origins$isPhasing;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {super(world, gameProfile);}
    
    @Unique
    public void origins$syncPhaseState() {
        boolean phasing = origins$isPhasing();
        GameMode oldGameMode = interactionManager.getGameMode();
        if (oldGameMode == GameMode.SPECTATOR) return;
        GameMode gameMode = phasing ? GameMode.SPECTATOR : oldGameMode;

        interactionManager.changeGameMode(gameMode);
        PlayerListS2CPacket packet = new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAME_MODE, (ServerPlayerEntity) (Object) this);
        interactionManager.changeGameMode(oldGameMode);
        networkHandler.sendPacket(packet);

        PlayerAbilities abilities = this.getAbilities();
        abilities.flying = phasing;

        this.noClip = gameMode == GameMode.SPECTATOR;
        this.sendAbilitiesUpdate();
    }

    @Unique
    public boolean origins$canPhase() {
        // Phasing logic
        boolean crouchingDown = this.isOnGround() && this.isSneaking();
        if (getHungerManager().getFoodLevel() <= 0) return false;
        return (!this.doesNotSuffocate(this.getBlockPos()) || !this.doesNotSuffocate(this.getBlockPos().add(0, 1, 0))) || crouchingDown;
    }

    @Override
    public boolean origins$isPhasing() {
        return origins$isPhasing;
    }

    @Override
    public void origins$setPhasing(boolean value) {
        this.origins$isPhasing = value;
    }

    @Unique
    public void origins$setAndSyncPhasing(boolean value) {
        this.origins$setPhasing(value);
        this.origins$syncPhaseState();
    }


    @Inject(method = "tick", at = @At("TAIL"))
    public void origins$phaseTick(CallbackInfo ci) {
        boolean phaseState = origins$isPhasing();
        if (phaseState) {
            if (!origins$canPhase()) {
                origins$setAndSyncPhasing(false);
                this.getAbilities().setFlySpeed(0.1f);
            } else {
                this.getAbilities().setFlySpeed(0.03f);
            }
            this.sendAbilitiesUpdate();
        }
    }
}
