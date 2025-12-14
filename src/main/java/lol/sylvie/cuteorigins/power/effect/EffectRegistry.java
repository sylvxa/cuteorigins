package lol.sylvie.cuteorigins.power.effect;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.power.effect.impl.*;
import lol.sylvie.cuteorigins.power.effect.impl.shulker.ShulkerInventoryEffect;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.Identifier;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class EffectRegistry {
    public HashMap<Identifier, Class<? extends Effect>> effects = new HashMap<>();

    private void putEffect(Class<? extends Effect> clazz) {
        try {
            Field field = clazz.getField("IDENTIFIER");
            field.setAccessible(true);
            Identifier identifier = (Identifier) field.get(null);

            effects.put(identifier, clazz);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public EffectRegistry() {
        // Effects are hard-coded.
        putEffect(AttributeEffect.class);
        putEffect(CannotSeeEffect.class);
        putEffect(DamageEffect.class);
        putEffect(DebugEffect.class);
        putEffect(EnchantmentDiscountEffect.class);
        putEffect(EnderPearlEffect.class);
        putEffect(DamageBonusEffect.class);
        putEffect(DamageMultiplierEffect.class);

        putEffect(ActionVelocityEffect.class);
        putEffect(ForceElytraEffect.class);
        putEffect(PotionEffect.class);

        putEffect(CannotUseEffect.class);
        putEffect(CobwebAttackEffect.class);
        putEffect(ClimbAnywhereEffect.class);

        putEffect(WaterBreathingEffect.class);
        putEffect(PotionImmunityEffect.class);
        putEffect(DamageImmunityEffect.class);
        putEffect(NetherHomeEffect.class);

        putEffect(ShulkerInventoryEffect.class);
        putEffect(ExhaustionEffect.class);

        putEffect(ModifyHarvestEffect.class);
        putEffect(SleepingConditionEffect.class);

        putEffect(ScareCreeperEffect.class);
        putEffect(FireEffect.class);

        putEffect(TogglePhasingEffect.class);
        putEffect(InvisibleEffect.class);

        putEffect(VillagerGossipEffect.class);
    }

    public Effect getEffect(Identifier identifier, JsonObject parameters) {
        // I am 200% sure there is a way to do this without reflection, but I am ever so lazy and this works.
        Class<? extends Effect> clazz = effects.get(identifier);
        try {
            Method method = clazz.getMethod("fromJson", JsonObject.class);
            return (Effect) method.invoke(null, parameters);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("fromJson doesn't exist on this method!");
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
