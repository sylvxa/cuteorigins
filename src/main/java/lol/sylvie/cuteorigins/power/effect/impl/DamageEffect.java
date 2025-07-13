package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.condition.Condition;
import lol.sylvie.cuteorigins.power.effect.Effect;
import lol.sylvie.cuteorigins.util.JsonHelper;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKey;

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
    public void onTick(ServerPlayerEntity player) {
        if (!condition.test(player)) return;
        assert player.getServer() != null;
    
        int now = player.getServer().getTicks();
        int lastTickDamage = timestamps.computeIfAbsent(player.getUuid(), ignored -> now);
        if ((lastTickDamage + speed) < now) {
            RegistryKey<DamageType> damageTypeKey = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, damageType);
            DamageSource source = player.getWorld().getDamageSources().create(damageTypeKey);
            player.damage(player.getWorld(), source, damage);
            timestamps.put(player.getUuid(), now);
        }
    }



    public static Effect fromJson(JsonObject object) {
        return new DamageEffect(object.get("damage").getAsFloat(), object.get("speed").getAsInt(), JsonHelper.jsonStringToIdentifier(object.get("damage_type")), Condition.fromJson(object.getAsJsonObject("condition")));
    }
}
