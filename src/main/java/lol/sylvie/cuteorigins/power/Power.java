package lol.sylvie.cuteorigins.power;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.power.effect.Effect;
import lol.sylvie.cuteorigins.util.OriginRegistries;
import lol.sylvie.cuteorigins.util.TextUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

public class Power {
    private final Identifier identifier;
    private final boolean visible;
    private final boolean negative;
    private final Integer cooldown;
    private final HashMap<UUID, Integer> cooldownMap = new HashMap<>();

    private final Effect effect;

    private static final DecimalFormat SECOND_FORMAT = new DecimalFormat("0.0");

    protected Power(Identifier identifier, boolean visible, boolean negative, Effect effect, Integer cooldown) {
        this.identifier = identifier;
        this.visible = visible;
        this.negative = negative;
        this.effect = effect;
        this.cooldown = cooldown;
    }

    public static Power fromJson(Identifier identifier, JsonObject object) {
        boolean visible = object.get("visible").getAsBoolean();
        boolean negative = object.get("negative").getAsBoolean();
        Identifier effect = Identifier.parse(object.get("effect").getAsString());

        Integer cooldown = null;
        if (object.has("cooldown")) {
            cooldown = object.get("cooldown").getAsInt();
        }

        return new Power(identifier, visible, negative, OriginRegistries.EFFECT_REGISTRY.getEffect(effect, object), cooldown);
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isNegative() {
        return negative;
    }

    public boolean isPositive() {
        return !negative;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public boolean hasCooldown() {
        return this.cooldown != null;
    }

    public int getCooldownRemaining(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        return (cooldownMap.getOrDefault(player.getUUID(), 0) + cooldown) - server.getTickCount();
    }

    public boolean isOnCooldown(ServerPlayer player) {
        if (cooldown == null) return false;
        return getCooldownRemaining(player) > 0;
    }

    public boolean attemptAction(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();

        if (!effect.hasAction()) return false; // This shouldn't happen unless someone does some weird item component stuff!
        if (isOnCooldown(player)) {
            float remaining = getCooldownRemaining(player) / 20f;
            player.sendOverlayMessage(Component.translatable("message.cuteorigins.cooldown", SECOND_FORMAT.format(remaining)));
            return false;
        }

        this.getEffect().onAction(player);
        cooldownMap.put(player.getUUID(), server.getTickCount());
        return true;
    }

    public Component getName() {
        return TextUtil.getIdentifierText(this.identifier, "power", "name");
    }

    public Component getDescription() {
        return TextUtil.getIdentifierText(this.identifier, "power", "description");
    }

    public Effect getEffect() {
        return effect;
    }
}
