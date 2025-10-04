package lol.sylvie.cuteorigins.power.condition;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.mixininterfaces.Phasable;
import lol.sylvie.cuteorigins.util.JsonHelper;
import net.minecraft.entity.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Locale;
import java.util.Random;
import java.util.function.Predicate;

public class Condition {
    protected final Predicate<ConditionContext> predicate;
    private final boolean inverted;
    private static final Random random = new Random();

    // MobEntity.class
    private boolean isAffectedByDaylight(LivingEntity entity, boolean ignoreWater) {
        if (entity.getEntityWorld().isDay() && !entity.getEntityWorld().isClient()) {
            float f = entity.getBrightnessAtEyes();
            BlockPos blockPos = BlockPos.ofFloored(entity.getX(), entity.getEyeY(), entity.getZ());
            boolean bl = !ignoreWater && (entity.isTouchingWaterOrRain() || entity.inPowderSnow || entity.wasInPowderSnow);
            return f > 0.5F && !bl && entity.getEntityWorld().isSkyVisible(blockPos);
        }

        return false;
    }

    public Condition(CheckType checkType, JsonObject params, boolean inverted) {
        switch (checkType) {
            case ENTITY_TYPE -> predicate = ctx -> ctx.target.getType().equals(EntityType.get(params.get("type").getAsString()).orElseThrow());
            case EQUIPMENT -> {
                EquipmentSlot slot = EquipmentSlot.byName(params.get("slot").getAsString().toLowerCase(Locale.ROOT));
                Identifier identifier = JsonHelper.jsonStringToIdentifier(params.get("item"));
                predicate = ctx -> {
                    if (!(ctx.target instanceof LivingEntity living)) return false;
                    return living.getEquippedStack(slot).getRegistryEntry().matchesId(identifier);
                };
            }
            case WATER -> {
                boolean submerged = params.has("submerged") && params.get("submerged").getAsBoolean();
                boolean rain = params.has("rain") && params.get("rain").getAsBoolean();
                predicate = ctx -> {
                    if (rain && ctx.target.isTouchingWaterOrRain()) return true;
                    if (submerged) {
                        return ctx.target.isSubmergedInWater();
                    }

                    return ctx.target.isTouchingWater();
                };
            }
            case FIRE -> predicate = ctx -> ctx.target.isOnFire();
            case GLIDING -> predicate = ctx -> {
                if (!(ctx.target instanceof ServerPlayerEntity player)) return false;
                return player.isGliding();
            };
            case LOW_CEILING -> predicate = ctx -> {
                BlockPos ceilingPos = ctx.target.getBlockPos().add(0, 2, 0);
                return ctx.target.getEntityWorld().getBlockState(ceilingPos).isOpaqueFullCube();
            };
            case PHASING -> predicate = ctx -> ctx.target instanceof Phasable phasable && phasable.origins$isPhasing();
            case SNEAKING -> predicate = ctx -> ctx.target.isSneaking();
            case SPRINTING -> predicate = ctx -> ctx.target.isSprinting();
            case ELEVATION -> {
                int height = params.get("height").getAsInt();
                predicate = ctx -> ctx.target.getY() >= height;
            }
            case DAYLIGHT -> {
                boolean ignoreWater = params.has("ignore_water") && params.get("ignore_water").getAsBoolean();
                predicate = ctx -> ctx.target instanceof ServerPlayerEntity living && isAffectedByDaylight(living, ignoreWater);
            }
            case ALWAYS -> predicate = ctx -> true;
            default -> throw new NotImplementedException("CheckType " + checkType + " is not implemented");
        }
        this.inverted = inverted;
    }

    public boolean test(Entity target) {
        return inverted ^ this.predicate.test(new ConditionContext(target));
    }

    public static Condition fromJson(JsonObject object) {
        boolean inverted = false;
        JsonObject parameters = new JsonObject();
        if (object.has("inverted")) inverted = object.get("inverted").getAsBoolean();
        if (object.has("parameters")) parameters = object.get("parameters").getAsJsonObject();

        return new Condition(
                CheckType.valueOf(object.get("check_type").getAsString().toUpperCase(Locale.ROOT)),
                parameters,
                inverted);
    }

    public record ConditionContext(Entity target) {}

    public enum CheckType {
        ENTITY_TYPE,
        EQUIPMENT,
        WATER,
        FIRE,
        GLIDING,
        LOW_CEILING,
        PHASING,
        SNEAKING,
        SPRINTING,
        ELEVATION,
        DAYLIGHT,
        ALWAYS
    }
}
