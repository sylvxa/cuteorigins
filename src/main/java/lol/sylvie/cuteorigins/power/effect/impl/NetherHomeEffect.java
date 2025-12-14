package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.condition.Condition;
import lol.sylvie.cuteorigins.power.effect.Effect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.PlayerSpawnFinder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.LevelData;
import java.util.Objects;
import java.util.Set;

public class NetherHomeEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("nether_home");

    protected NetherHomeEffect() {
        super(IDENTIFIER, false);
    }

    @Override
    public void onChosen(ServerPlayer player) {
        Level world = Objects.requireNonNull(player.level().getServer()).getLevel(Level.NETHER);
        if (!(world instanceof ServerLevel serverWorld)) return;
        BlockPos pos = PlayerSpawnFinder.getSpawnPosInChunk(serverWorld, serverWorld.getChunk(player.blockPosition()).getPos());
        if (pos == null) pos = new BlockPos(0, 64, 0);

        if (!serverWorld.getBlockState(pos).isAir()) {
            serverWorld.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            serverWorld.setBlockAndUpdate(pos.offset(0, 1, 0), Blocks.AIR.defaultBlockState());
            player.addItem(Items.WOODEN_PICKAXE.getDefaultInstance());
        }

        player.teleportTo(serverWorld, pos.getX(), pos.getY(), pos.getZ(), Set.of(), 0f, 0f, false);
        player.setRespawnPosition(new ServerPlayer.RespawnConfig(new LevelData.RespawnData(GlobalPos.of(serverWorld.dimension(), pos), 0f, 0f), true), false);
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        ServerPlayer.RespawnConfig respawn = player.getRespawnConfig();
        if (respawn != null && respawn.forced() && respawn.respawnData().dimension().equals(Level.NETHER)) {
            player.setRespawnPosition(null, false);
        }
    }

    public static Effect fromJson(JsonObject object) {
        return new NetherHomeEffect();
    }
}
