package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.effect.Effect;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import java.util.HashMap;
import java.util.UUID;

public class CobwebAttackEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("cobweb_attack");
    private static final int COOLDOWN = 200;

    private final HashMap<UUID, Integer> timestamps = new HashMap<>();

    public CobwebAttackEffect() {
        super(IDENTIFIER, false);
    }

    @Override
    public InteractionResult onAttack(Player player, Entity target) {
        MinecraftServer server = player.level().getServer();
        if (server == null) return super.onAttack(player, target);
        int now = server.getTickCount();
        int lastCobweb = timestamps.computeIfAbsent(player.getUUID(), ignored -> now - COOLDOWN);

        if (target.getInBlockState().canBeReplaced() && player.getAttackStrengthScale(0) == 1f && (lastCobweb + COOLDOWN) <= now) {
            target.level().setBlockAndUpdate(target.blockPosition(), Blocks.COBWEB.defaultBlockState());
            timestamps.put(player.getUUID(), now);
        }
        return super.onAttack(player, target);
    }

    public static Effect fromJson(JsonObject object) {
        return new CobwebAttackEffect();
    }
}
