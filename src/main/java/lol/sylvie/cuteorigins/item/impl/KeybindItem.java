package lol.sylvie.cuteorigins.item.impl;

import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.item.ModComponents;
import lol.sylvie.cuteorigins.item.ModItems;
import lol.sylvie.cuteorigins.origin.Origin;
import lol.sylvie.cuteorigins.power.Power;
import lol.sylvie.cuteorigins.state.StateManager;
import lol.sylvie.cuteorigins.util.OriginRegistries;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.HashMap;

public class KeybindItem extends SimplePolymerItem {
    public static HashMap<ItemStack, Integer> UPDATE_MAP = new HashMap<>();
    public static Identifier IDENTIFIER = CuteOrigins.identifier("keybind");

    public KeybindItem() {
        super(new Settings()
                        .registryKey(RegistryKey.of(RegistryKeys.ITEM, IDENTIFIER))
                        .maxCount(1)
                        .rarity(Rarity.COMMON),
                Items.LIME_DYE);
    }

    public static Power getPower(ItemStack stack) {
        String powerComponent = stack.get(ModComponents.POWER_KEYBIND);
        if (powerComponent == null) return null;

        Identifier powerId = Identifier.of(powerComponent);
        return OriginRegistries.POWER_REGISTRY.getPower(powerId);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!(user instanceof ServerPlayerEntity player)) return super.use(world, user, hand);
        MinecraftServer server = player.getEntityWorld().getServer();

        ItemStack stack = player.getStackInHand(hand);
        Power power = getPower(stack);
        Origin origin = StateManager.getPlayerState(player).getOrigin();
        if (power == null || origin == null || !origin.hasPower(power)) return super.use(world, user, hand);

        // Polymer won't sync the item change unless I do this
        boolean didAction = power.attemptAction(player);
        if (!didAction) return ActionResult.FAIL;
        if (power.hasCooldown()) {
            stack.set(ModComponents.ON_COOLDOWN, true);
            UPDATE_MAP.put(stack, server.getTicks() + power.getCooldown());
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipType tooltipType, PacketContext context) {
        ItemStack out = PolymerItemUtils.createItemStack(itemStack, tooltipType, context);
        out.set(DataComponentTypes.ITEM_MODEL, Identifier.ofVanilla("lime_dye"));
        out.set(DataComponentTypes.ITEM_NAME, Text.translatable("item.cuteorigins.keybind", "None").formatted(Formatting.GRAY));

        ServerPlayerEntity player = context.getPlayer();
        if (player == null) return out;
        Power power = getPower(itemStack);
        if (power == null) return out;

        out.set(DataComponentTypes.ITEM_NAME, Text.translatable("item.cuteorigins.keybind", power.getName()).formatted(Formatting.GRAY));
        if (power.isOnCooldown(player)) out.set(DataComponentTypes.ITEM_MODEL, Identifier.ofVanilla("gray_dye"));

        return out;
    }

    public static ItemStack getKeybind(Identifier identifier) {
        ItemStack keybind = new ItemStack(ModItems.KEYBIND_ITEM);
        keybind.set(ModComponents.POWER_KEYBIND, identifier.toString());
        return keybind;
    }

    @Override
    public boolean canBeNested() {
        return false;
    }
}
