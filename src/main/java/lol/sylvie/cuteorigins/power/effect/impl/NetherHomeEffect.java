package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.condition.Condition;
import lol.sylvie.cuteorigins.power.effect.Effect;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;

import java.util.Objects;
import java.util.Set;

public class NetherHomeEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("nether_home");

    protected NetherHomeEffect() {
        super(IDENTIFIER, false);
    }

    @Override
    public void onChosen(ServerPlayerEntity player) {
        World world = Objects.requireNonNull(player.getEntityWorld().getServer()).getWorld(World.NETHER);
        if (!(world instanceof ServerWorld serverWorld)) return;
        BlockPos pos = SpawnLocating.findServerSpawnPoint(serverWorld, serverWorld.getChunk(player.getBlockPos()).getPos());
        if (pos == null) pos = new BlockPos(0, 64, 0);

        if (!serverWorld.getBlockState(pos).isAir()) {
            serverWorld.setBlockState(pos, Blocks.AIR.getDefaultState());
            serverWorld.setBlockState(pos.add(0, 1, 0), Blocks.AIR.getDefaultState());
            player.giveItemStack(Items.WOODEN_PICKAXE.getDefaultStack());
        }

        player.teleport(serverWorld, pos.getX(), pos.getY(), pos.getZ(), Set.of(), 0f, 0f, false);
        player.setSpawnPoint(new ServerPlayerEntity.Respawn(new WorldProperties.SpawnPoint(GlobalPos.create(serverWorld.getRegistryKey(), pos), 0f, 0f), true), false);
    }

    @Override
    public void onRemoved(ServerPlayerEntity player) {
        ServerPlayerEntity.Respawn respawn = player.getRespawn();
        if (respawn != null && respawn.forced() && respawn.respawnData().getDimension().equals(World.NETHER)) {
            player.setSpawnPoint(null, false);
        }
    }

    public static Effect fromJson(JsonObject object) {
        return new NetherHomeEffect();
    }
}
