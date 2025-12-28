package lol.sylvie.cuteorigins.origin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.item.impl.KeybindItem;
import lol.sylvie.cuteorigins.power.Power;
import lol.sylvie.cuteorigins.power.effect.Effect;
import lol.sylvie.cuteorigins.util.JsonHelper;
import lol.sylvie.cuteorigins.util.OriginRegistries;
import lol.sylvie.cuteorigins.util.TextUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public record Origin(Identifier identifier, Item icon, boolean hidden, List<Power> powers) {
    public Component getName() {
        return TextUtil.getIdentifierText(this.identifier, "origin", "name");
    }

    public Component getDescription() {
        return TextUtil.getIdentifierText(this.identifier, "origin", "description");
    }

    public List<Power> getDisplayPowers() {
        return powers.stream().filter(Power::isVisible).sorted(Comparator.comparing(Power::isNegative)).toList();
    }

    public boolean hasPower(Power power) {
        return this.powers.contains(power);
    }

    public List<ItemStack> getKeybinds() {
        ArrayList<ItemStack> items = new ArrayList<>();
        for (Power power : this.powers()) {
            if (power.getEffect().hasAction()) {
                items.add(KeybindItem.getKeybind(power.getIdentifier()));
            }
        }
        return items;
    }

    public Effect getFirstEffect(Class<? extends Effect> clazz) {
        List<Effect> effects = getEffectsOfType(clazz);
        if (effects.isEmpty()) return null;
        return effects.getFirst();
    }

    public List<Effect> getEffectsOfType(Class<? extends Effect> clazz) {
        return this.powers().stream()
                .map(Power::getEffect)
                .filter(effect -> effect.getClass().equals(clazz))
                .toList();
    }

    public static Origin fromJson(Identifier identifier, JsonObject object) {
        Identifier itemId = JsonHelper.jsonStringToIdentifier(object.get("icon"));
        Item item = BuiltInRegistries.ITEM.getValue(itemId).asItem();
        boolean hidden = object.has("hidden") && object.get("hidden").getAsBoolean();

        List<JsonElement> powerNames = object.getAsJsonArray("powers").asList();
        List<Power> powerList = powerNames.stream()
                .map(JsonHelper::jsonStringToIdentifier)
                .filter(power -> {
                    if (!OriginRegistries.POWER_REGISTRY.hasPower(power)) {
                        CuteOrigins.LOGGER.warn("Origin {} tried to use power {} doesn't exist.", identifier, power);
                        return false;
                    }
                    return true;
                })
                .map(OriginRegistries.POWER_REGISTRY::getPower).toList();

        return new Origin(identifier, item, hidden, powerList);
    }

    private void forEachEffect(Consumer<Effect> runnable) {
        this.powers.forEach(power -> runnable.accept(power.getEffect()));
    }

    public void onRespawn(ServerPlayer player) {
        forEachEffect(effect -> effect.onRespawn(player));
    }

    public void onTick(ServerPlayer player) {
        forEachEffect(effect -> effect.onTick(player));
    }

    public InteractionResult onAttack(Player player, Entity target) {
        for (Power power : this.powers) {
            InteractionResult thisResult = power.getEffect().onAttack(player, target);
            if (thisResult != InteractionResult.PASS) {
                return thisResult;
            }
        }
        return InteractionResult.PASS;
    }

    public void onChosen(ServerPlayer player) {
        forEachEffect(effect -> effect.onChosen(player));
    }

    public void onRemoved(ServerPlayer player) {
        forEachEffect(effect -> effect.onRemoved(player));
    }
}
