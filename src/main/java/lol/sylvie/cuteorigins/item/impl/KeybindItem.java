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
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.HashMap;

public class KeybindItem extends SimplePolymerItem {
    public static HashMap<ItemStack, Integer> UPDATE_MAP = new HashMap<>();
    public static Identifier IDENTIFIER = CuteOrigins.identifier("keybind");

    public KeybindItem() {
        super(new Properties()
                        .setId(ResourceKey.create(Registries.ITEM, IDENTIFIER))
                        .stacksTo(1)
                        .rarity(Rarity.COMMON),
                Items.LIME_DYE);
    }

    public static Power getPower(ItemStack stack) {
        String powerComponent = stack.get(ModComponents.POWER_KEYBIND);
        if (powerComponent == null) return null;

        Identifier powerId = Identifier.parse(powerComponent);
        return OriginRegistries.POWER_REGISTRY.getPower(powerId);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        if (!(user instanceof ServerPlayer player)) return super.use(world, user, hand);
        MinecraftServer server = player.level().getServer();

        ItemStack stack = player.getItemInHand(hand);
        Power power = getPower(stack);
        Origin origin = StateManager.getPlayerState(player).getOrigin();
        if (power == null || origin == null || !origin.hasPower(power)) return super.use(world, user, hand);

        // Polymer won't sync the item change unless I do this
        boolean didAction = power.attemptAction(player);
        if (!didAction) return InteractionResult.FAIL;
        if (power.hasCooldown()) {
            stack.set(ModComponents.ON_COOLDOWN, true);
            UPDATE_MAP.put(stack, server.getTickCount() + power.getCooldown());
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipFlag tooltipType, PacketContext context) {
        ItemStack out = PolymerItemUtils.createItemStack(itemStack, tooltipType, context);
        out.set(DataComponents.ITEM_MODEL, Identifier.withDefaultNamespace("lime_dye"));
        out.set(DataComponents.ITEM_NAME, Component.translatable("item.cuteorigins.keybind", "None").withStyle(ChatFormatting.GRAY));

        ServerPlayer player = context.getPlayer();
        if (player == null) return out;
        Power power = getPower(itemStack);
        if (power == null) return out;

        out.set(DataComponents.ITEM_NAME, Component.translatable("item.cuteorigins.keybind", power.getName()).withStyle(ChatFormatting.GRAY));
        if (power.isOnCooldown(player)) out.set(DataComponents.ITEM_MODEL, Identifier.withDefaultNamespace("gray_dye"));

        return out;
    }

    public static ItemStack getKeybind(Identifier identifier) {
        ItemStack keybind = new ItemStack(ModItems.KEYBIND_ITEM);
        keybind.set(ModComponents.POWER_KEYBIND, identifier.toString());
        return keybind;
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }
}
