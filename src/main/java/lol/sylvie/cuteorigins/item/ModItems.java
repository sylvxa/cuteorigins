package lol.sylvie.cuteorigins.item;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.item.impl.KeybindItem;
import lol.sylvie.cuteorigins.item.impl.OrbOfOriginItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
public class ModItems {
    public static Item ORB_OF_ORIGIN = register(new OrbOfOriginItem(), OrbOfOriginItem.IDENTIFIER);
    public static Item KEYBIND_ITEM = register(new KeybindItem(), KeybindItem.IDENTIFIER);


    public static final CreativeModeTab ITEM_GROUP = PolymerItemGroupUtils.builder()
            .title(Component.translatable("itemGroup.cuteorigins.item_group"))
            .icon(Items.SLIME_BALL::getDefaultInstance).displayItems((context, entries) -> {
                entries.accept(ORB_OF_ORIGIN);
            }).build();

    public static Item register(Item item, Identifier identifier) {
        return Registry.register(BuiltInRegistries.ITEM, identifier, item);
    }

    public static void initialize() {
        PolymerItemGroupUtils.registerPolymerItemGroup(CuteOrigins.identifier("item_group"), ITEM_GROUP);
    }
}
