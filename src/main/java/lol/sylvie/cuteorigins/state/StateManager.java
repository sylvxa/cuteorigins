package lol.sylvie.cuteorigins.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lol.sylvie.cuteorigins.CuteOrigins;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StateManager extends SavedData {
    public HashMap<UUID, PlayerData> players;

    public static final Codec<StateManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.unboundedMap(UUIDUtil.STRING_CODEC, PlayerData.CODEC).fieldOf("players").forGetter(StateManager::getPlayers))
            .apply(instance, StateManager::new));

    private static final SavedDataType<StateManager> TYPE = new SavedDataType<>(CuteOrigins.identifier("origins"),
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

    public static StateManager getServerState(MinecraftServer server) {
        SavedDataStorage savedDataStorage = server.overworld().getDataStorage();
        StateManager state = savedDataStorage.computeIfAbsent(TYPE);

        state.setDirty();

        return state;
    }

    public static PlayerData getPlayerState(LivingEntity player) {
        MinecraftServer server = player.level().getServer();
        if (server == null)
            throw new IllegalStateException("Tried to get the player state of a non-server entity.");
        if (!(player instanceof Player))
            throw new IllegalStateException("Non-player entities shouldn't have data!");
        StateManager serverState = getServerState(server);
        return serverState.players.computeIfAbsent(player.getUUID(), uuid -> new PlayerData());
    }
}
