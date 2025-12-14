package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.condition.Condition;
import lol.sylvie.cuteorigins.power.effect.Effect;
import lol.sylvie.cuteorigins.util.JsonHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import java.util.HashMap;
import java.util.UUID;

public class DamageEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("damage");
    private final float damage;
    private final int speed;

    private final Identifier damageType;

    private final Condition condition;

    private final HashMap<UUID, Integer> timestamps = new HashMap<>();

    public DamageEffect(float damage, int speed, Identifier damageType, Condition condition) {
        super(IDENTIFIER, false);
        this.damage = damage;
        this.speed = speed;
        this.damageType = damageType;
        this.condition = condition;
    }

    @Override
    public void onTick(ServerPlayer player) {
        if (!condition.test(player)) return;
        ServerLevel world = player.level();
        MinecraftServer server = world.getServer();

        int now = server.getTickCount();
        int lastTickDamage = timestamps.computeIfAbsent(player.getUUID(), ignored -> now);
        if ((lastTickDamage + speed) < now) {
            ResourceKey<DamageType> damageTypeKey = ResourceKey.create(Registries.DAMAGE_TYPE, damageType);
            DamageSource source = world.damageSources().source(damageTypeKey);
            player.hurtServer(world, source, damage);
            timestamps.put(player.getUUID(), now);
        }
    }

    public static Effect fromJson(JsonObject object) {
        return new DamageEffect(object.get("damage").getAsFloat(), object.get("speed").getAsInt(), JsonHelper.jsonStringToIdentifier(object.get("damage_type")), Condition.fromJson(object.getAsJsonObject("condition")));
    }
}