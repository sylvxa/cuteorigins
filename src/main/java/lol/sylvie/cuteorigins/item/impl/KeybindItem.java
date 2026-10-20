package lol.sylvie.cuteorigins.item.impl;

import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import it.unimi.dsi.fastutil.ints.IntList;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.item.ItemUpdates;
import lol.sylvie.cuteorigins.item.ModComponents;
import lol.sylvie.cuteorigins.item.ModItems;
import lol.sylvie.cuteorigins.origin.Origin;
import lol.sylvie.cuteorigins.power.Power;
import lol.sylvie.cuteorigins.state.StateManager;
import lol.sylvie.cuteorigins.util.OriginRegistries;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.UseCooldown;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class KeybindItem extends SimplePolymerItem {

    public static Identifier IDENTIFIER = CuteOrigins.identifier("keybind");

    public KeybindItem() {
        super(new Properties()
                        .setId(ResourceKey.create(Registries.ITEM, IDENTIFIER))
                        .stacksTo(1)
                        .rarity(Rarity.COMMON),
                Items.FIREWORK_STAR);
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

        ItemUpdates.update(player);
        if (power.isOnCooldown(player))
            ItemUpdates.schedule(player, server.getTickCount() + power.getCooldown());
        if (power.hasCooldown()) player.getCooldowns().addCooldown(power.getIdentifier(), power.getCooldown());

        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipFlag tooltipType, PacketContext context, HolderLookup.Provider lookup) {
        ItemStack out = PolymerItemUtils.createItemStack(itemStack, tooltipType, context, lookup);
        out.set(DataComponents.ITEM_MODEL, Identifier.withDefaultNamespace("firework_star"));
        out.set(DataComponents.ITEM_NAME, Component.translatable("item.cuteorigins.keybind", "None").withStyle(ChatFormatting.GRAY));

        Connection connection = context.orElseThrow(PacketContext.CONNECTION);
        if (!(connection.getPacketListener() instanceof ServerGamePacketListenerImpl listener)) return out;

        ServerPlayer player = listener.getPlayer();
        Power power = getPower(itemStack);
        if (power == null) return out;

        boolean customModelHandling = power.hasCustomKeybindModels();
        boolean onCooldown = power.isOnCooldown(player);

        out.set(DataComponents.ITEM_NAME, Component.translatable("item.cuteorigins.keybind", power.getName()).withStyle(onCooldown ? ChatFormatting.GRAY : ChatFormatting.GREEN));

        IntList color = IntList.of(0x00FF00);
        if (customModelHandling) {
            Identifier identifier = power.getIdentifier().withSuffix("_keybind");
            out.set(DataComponents.ITEM_MODEL, onCooldown ? identifier.withSuffix("_cooldown") : identifier);
        } else if (!onCooldown) {
            out.set(DataComponents.FIREWORK_EXPLOSION, new FireworkExplosion(FireworkExplosion.Shape.LARGE_BALL, color, IntList.of(), false, false));
        }

        return out;
    }

    public static ItemStack getKeybind(Identifier identifier) {
        ItemStack keybind = new ItemStack(ModItems.KEYBIND_ITEM);
        keybind.set(ModComponents.POWER_KEYBIND, identifier.toString());

        Power power = getPower(keybind);
        if (power != null && power.hasCooldown()) keybind.set(DataComponents.USE_COOLDOWN, new UseCooldown(power.getCooldown() / 20f, Optional.of(identifier)));
        return keybind;
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        if (!(owner instanceof ServerPlayer player) || !player.getItemInHand(InteractionHand.MAIN_HAND).equals(itemStack)) return;

        Power power = getPower(itemStack);
        if (power == null) return;

        boolean onCooldown = power.isOnCooldown(player);
        if (onCooldown) power.sendCooldownOverlay(player);
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }
}
