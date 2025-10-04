package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.effect.Effect;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

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
    public ActionResult onAttack(PlayerEntity player, Entity target) {
        MinecraftServer server = player.getEntityWorld().getServer();
        if (server == null) return super.onAttack(player, target);
        int now = server.getTicks();
        int lastCobweb = timestamps.computeIfAbsent(player.getUuid(), ignored -> now - COOLDOWN);

        if (target.getBlockStateAtPos().isReplaceable() && player.getAttackCooldownProgress(0) == 1f && (lastCobweb + COOLDOWN) <= now) {
            target.getEntityWorld().setBlockState(target.getBlockPos(), Blocks.COBWEB.getDefaultState());
            timestamps.put(player.getUuid(), now);
        }
        return super.onAttack(player, target);
    }

    public static Effect fromJson(JsonObject object) {
        return new CobwebAttackEffect();
    }
}
