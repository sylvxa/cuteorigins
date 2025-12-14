package lol.sylvie.cuteorigins.item;

import com.mojang.serialization.Codec;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import lol.sylvie.cuteorigins.CuteOrigins;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModComponents {
    public static final DataComponentType<String> POWER_KEYBIND = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            CuteOrigins.identifier("power_keybind"),
            DataComponentType.<String>builder().persistent(Codec.STRING).build()
    );

    public static final DataComponentType<Boolean> ON_COOLDOWN = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            CuteOrigins.identifier("power_on_cooldown"),
            DataComponentType.<Boolean>builder().persistent(Codec.BOOL).build()
    );

    public static void initialize() {
        CuteOrigins.LOGGER.info("Registering {} components", CuteOrigins.MOD_ID);

        PolymerComponent.registerDataComponent(POWER_KEYBIND);
        PolymerComponent.registerDataComponent(ON_COOLDOWN);
    }
}
