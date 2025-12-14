package lol.sylvie.cuteorigins.mixin;

import com.mojang.authlib.GameProfile;
import lol.sylvie.cuteorigins.mixininterfaces.Phasable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements Phasable {
    @Shadow public ServerGamePacketListenerImpl connection;
    @Shadow @Final public ServerPlayerGameMode gameMode; // this was interactionManager in yarn, rip

    @Shadow public abstract void onUpdateAbilities();

    @Unique
    private boolean origins$isPhasing;

    public ServerPlayerMixin(Level world, BlockPos ignoredPos, float ignoredYaw, GameProfile gameProfile) {super(world, gameProfile);}

    @Unique
    public void origins$syncPhaseState() {
        boolean phasing = origins$isPhasing();
        GameType oldGameType = gameMode.getGameModeForPlayer();
        if (oldGameType == GameType.SPECTATOR) return;
        GameType gameType = phasing ? GameType.SPECTATOR : oldGameType;

        gameMode.changeGameModeForPlayer(gameType);
        ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE, (ServerPlayer) (Object) this);
        gameMode.changeGameModeForPlayer(oldGameType);
        connection.send(packet);

        Abilities abilities = this.getAbilities();
        abilities.flying = phasing;

        this.noPhysics = gameType == GameType.SPECTATOR;
        this.onUpdateAbilities();
    }

    @Unique
    public boolean origins$canPhase() {
        // Phasing logic
        boolean crouchingDown = this.onGround() && this.isShiftKeyDown();
        if (getFoodData().getFoodLevel() <= 0) return false;
        return (!this.freeAt(this.blockPosition()) || !this.freeAt(this.blockPosition().offset(0, 1, 0))) || crouchingDown;
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
                this.getAbilities().setFlyingSpeed(0.1f);
            } else {
                this.getAbilities().setFlyingSpeed(0.03f);
            }
            this.onUpdateAbilities();
        }
    }
}