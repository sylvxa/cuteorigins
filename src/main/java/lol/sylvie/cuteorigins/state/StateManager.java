package lol.sylvie.cuteorigins.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lol.sylvie.cuteorigins.CuteOrigins;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Uuids;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StateManager extends PersistentState {
    public HashMap<UUID, PlayerData> players;

    public static final Codec<StateManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.unboundedMap(Uuids.CODEC, PlayerData.CODEC).fieldOf("players").forGetter(StateManager::getPlayers))
            .apply(instance, StateManager::new));

    private static final PersistentStateType<StateManager> TYPE = new PersistentStateType<>(CuteOrigins.MOD_ID,
            StateManager::new, CODEC, null);

    // Constructors
    public StateManager() {
        this.players = new HashMap<>();
    }

    public StateManager(Map<UUID, PlayerData> players) {
        this.players = new HashMap<>(players);
    }

    // Getters
    public Map<UUID, PlayerData> getPlayers() {
        return players;
    }
    /*
    public static StateManager createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        StateManager state = new StateManager();

        NbtCompound playersNbt = tag.getCompound("players");
        playersNbt.getKeys().forEach(key -> {
            PlayerData playerData = new PlayerData();
            NbtCompound playerNbt = playersNbt.getCompound(key);

            NbtElement originElement = playerNbt.get("origin");
            if (originElement != null) playerData.selectedOrigin = Identifier.of(originElement.asString());

            NbtList shulkerElement = playerNbt.getList("shulker_inventory", NbtList.COMPOUND_TYPE);
            if (shulkerElement != null) playerData.shulkerInventory.readNbtList(shulkerElement, registryLookup);

            UUID uuid = UUID.fromString(key);
            state.players.put(uuid, playerData);
        });

        return state;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound playersNbt = new NbtCompound();

        players.forEach((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();

            if (playerData.selectedOrigin != null) playerNbt.putString("origin", playerData.selectedOrigin.toString());
            playerNbt.put("shulker_inventory", playerData.shulkerInventory.toNbtList(registryLookup));

            playersNbt.put(uuid.toString(), playerNbt);
        });

        nbt.put("players", playersNbt);
        return nbt;
    }*/

    public static StateManager getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getOverworld().getPersistentStateManager();
        StateManager state = persistentStateManager.getOrCreate(TYPE);

        state.markDirty();

        return state;
    }

    public static PlayerData getPlayerState(LivingEntity player) {
        MinecraftServer server = player.getEntityWorld().getServer();
        if (server == null)
            throw new IllegalStateException("Tried to get the player state of a non-server entity.");
        if (!(player instanceof PlayerEntity))
            throw new IllegalStateException("Non-player entities shouldn't have data!");
        StateManager serverState = getServerState(server);
        return serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerData());
    }
}
