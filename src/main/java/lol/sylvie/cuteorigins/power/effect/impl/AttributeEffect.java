package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.effect.Effect;
import lol.sylvie.cuteorigins.util.JsonHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AttributeEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("attribute_modifier");
    private final Map<Holder<Attribute>, AttributeModifier> modifiers;

    protected AttributeEffect(Map<Holder<Attribute>, AttributeModifier> modifiers) {
        super(IDENTIFIER, false);
        this.modifiers = modifiers;
    }


    protected AttributeInstance getEntityAttributeInstance(ServerPlayer player, Holder<Attribute> attribute) {
        AttributeInstance instance = player.getAttributes().getInstance(attribute);
        if (instance == null) throw new IllegalStateException("Attribute from " + this.getClass().getSimpleName() + " does not exist!");
        return instance;
    }

    @Override
    public void onRespawn(ServerPlayer player) {
        for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : modifiers.entrySet()) {
            Holder<Attribute> attribute = entry.getKey();
            AttributeModifier modifier = entry.getValue();
            AttributeInstance instance = getEntityAttributeInstance(player, attribute);
            if (!instance.hasModifier(modifier.id())) {
                instance.addPermanentModifier(entry.getValue());
            }
        }
    }

    @Override
    public void onChosen(ServerPlayer player) {
        this.onRespawn(player);
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : modifiers.entrySet()) {
            Holder<Attribute> attribute = entry.getKey();
            AttributeModifier modifier = entry.getValue();
            AttributeInstance instance = getEntityAttributeInstance(player, attribute);

            instance.removeModifier(modifier);
        }
    }

    public static Effect fromJson(JsonObject object) {
        List<JsonElement> modifiers = object.getAsJsonArray("modifiers").asList();
        HashMap<Holder<Attribute>, AttributeModifier> modifierMap = new HashMap<>();
        for (JsonElement element : modifiers) {
            if (!(element instanceof JsonObject modifier)) throw new IllegalArgumentException("Modifier is not a JSON object");

            Identifier modifierId = JsonHelper.jsonStringToIdentifier(modifier.get("id"));

            Identifier attributeId = JsonHelper.jsonStringToIdentifier(modifier.get("attribute"));
            if (!BuiltInRegistries.ATTRIBUTE.containsKey(attributeId)) {
                throw new RuntimeException("Attribute " + attributeId + " does not exist!");
            }
            Holder<Attribute> attribute = BuiltInRegistries.ATTRIBUTE.get(attributeId).orElseThrow();

            String operation = modifier.get("operation").getAsString();
            double value = modifier.get("value").getAsDouble();

            AttributeModifier attributeModifier = new AttributeModifier(modifierId, value, operationFromString(operation));

            modifierMap.put(attribute, attributeModifier);
        }

        return new AttributeEffect(modifierMap);
    }

    private static AttributeModifier.Operation operationFromString(String value) {
        return AttributeModifier.Operation.valueOf(value.toUpperCase(Locale.ROOT));
    }
}
