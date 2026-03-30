package lol.sylvie.cuteorigins.power.condition;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.mixininterfaces.Phasable;
import lol.sylvie.cuteorigins.util.JsonHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Random;
import java.util.function.Predicate;

public class Condition {
    protected final Predicate<ConditionContext> predicate;
    private final boolean inverted;
    private static final Random random = new Random();

    // Mob.class
    private boolean isAffectedByDaylight(LivingEntity entity, boolean ignoreWater) {
        if (entity.level().isBrightOutside() && !entity.level().isClientSide()) {
            float f = entity.getLightLevelDependentMagicValue();
            BlockPos blockPos = BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ());
            boolean bl = !ignoreWater && (entity.isInWaterOrRain() || entity.isInPowderSnow || entity.wasInPowderSnow);
            return f > 0.5F && !bl && entity.level().canSeeSky(blockPos);
        }

        return false;
    }

    public Condition(CheckType checkType, JsonObject params, boolean inverted) {
        switch (checkType) {
            case ENTITY_TYPE -> predicate = ctx -> ctx.target.getType().equals(EntityType.byString(params.get("type").getAsString()).orElseThrow());
            case EQUIPMENT -> {
                EquipmentSlot slot = EquipmentSlot.byName(params.get("slot").getAsString().toLowerCase(Locale.ROOT));
                Identifier identifier = JsonHelper.jsonStringToIdentifier(params.get("item"));
                predicate = ctx -> {
                    if (!(ctx.target instanceof LivingEntity living)) return false;
                    return living.getItemBySlot(slot).typeHolder().is(identifier);
                };
            }
            case WATER -> {
                boolean submerged = params.has("submerged") && params.get("submerged").getAsBoolean();
                boolean rain = params.has("rain") && params.get("rain").getAsBoolean();
                predicate = ctx -> {
                    if (rain && ctx.target.isInWaterOrRain()) return true;
                    if (submerged) {
                        return ctx.target.isUnderWater();
                    }

                    return ctx.target.isInWater();
                };
            }
            case FIRE -> predicate = ctx -> ctx.target.isOnFire();
            case GLIDING -> predicate = ctx -> {
                if (!(ctx.target instanceof ServerPlayer player)) return false;
                return player.isFallFlying();
            };
            case LOW_CEILING -> predicate = ctx -> {
                BlockPos ceilingPos = ctx.target.blockPosition().offset(0, 2, 0);
                return ctx.target.level().getBlockState(ceilingPos).isSolidRender();
            };
            case PHASING -> predicate = ctx -> ctx.target instanceof Phasable phasable && phasable.origins$isPhasing();
            case SNEAKING -> predicate = ctx -> ctx.target.isShiftKeyDown();
            case SPRINTING -> predicate = ctx -> ctx.target.isSprinting();
            case ELEVATION -> {
                int height = params.get("height").getAsInt();
                predicate = ctx -> ctx.target.getY() >= height;
            }
            case DAYLIGHT -> {
                boolean ignoreWater = params.has("ignore_water") && params.get("ignore_water").getAsBoolean();
                predicate = ctx -> ctx.target instanceof ServerPlayer living && isAffectedByDaylight(living, ignoreWater);
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
