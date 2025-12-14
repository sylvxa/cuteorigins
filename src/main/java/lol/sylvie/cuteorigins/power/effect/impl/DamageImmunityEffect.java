package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.condition.Condition;
import lol.sylvie.cuteorigins.power.effect.Effect;
import lol.sylvie.cuteorigins.util.JsonHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import java.util.List;

public class DamageImmunityEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("damage_immunity");
    private final List<Identifier> damageTypes;
    private final Condition condition;

    public DamageImmunityEffect(List<Identifier> damageTypes, Condition condition) {
        super(IDENTIFIER, false);
        this.damageTypes = damageTypes;
        this.condition = condition;
    }

    public DamageType getDamageType(Player player, Identifier identifier) {
        Registry<DamageType> damageTypeRegistry = player.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE);
        return damageTypeRegistry.getValue(identifier);
    }

    public boolean isImmuneTo(Player player, DamageType type) {
        return damageTypes.stream().anyMatch(identifier -> getDamageType(player, identifier).equals(type)) && this.condition.test(player);
    }

    public static Effect fromJson(JsonObject object) {
        return new DamageImmunityEffect(object.getAsJsonArray("damage_types").asList().stream().map(JsonHelper::jsonStringToIdentifier).toList(), Condition.fromJson(object.getAsJsonObject("condition")));
    }
}