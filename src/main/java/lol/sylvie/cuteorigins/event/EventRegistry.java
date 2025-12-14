package lol.sylvie.cuteorigins.event;

import lol.sylvie.cuteorigins.command.OriginCommand;
import lol.sylvie.cuteorigins.gui.OriginGui;
import lol.sylvie.cuteorigins.item.ModComponents;
import lol.sylvie.cuteorigins.item.ModItems;
import lol.sylvie.cuteorigins.item.impl.KeybindItem;
import lol.sylvie.cuteorigins.origin.Origin;
import lol.sylvie.cuteorigins.power.effect.Effect;
import lol.sylvie.cuteorigins.power.effect.impl.CannotUseEffect;
import lol.sylvie.cuteorigins.power.effect.impl.SleepingConditionEffect;
import lol.sylvie.cuteorigins.state.StateManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;

public class EventRegistry {
    public static boolean cannotUse(Item item, Player user) {
        if (!(user instanceof ServerPlayer)) return false;
        Origin origin = StateManager.getPlayerState(user).getOrigin();
        if (origin == null) return false;
        for (Effect effect : origin.getEffectsOfType(CannotUseEffect.class)) {
            CannotUseEffect cannotUseEffect = (CannotUseEffect) effect;
            if (!cannotUseEffect.isAllowedToUse(item)) {
                return true;
            }
        }
        return false;
    }

    public static Component canSleep(Player player) {
        Origin origin = StateManager.getPlayerState(player).getOrigin();
        if (origin == null) return null;
        for (Effect effect : origin.getEffectsOfType(SleepingConditionEffect.class)) {
            SleepingConditionEffect sleepingConditionEffect = (SleepingConditionEffect) effect;
            if (!sleepingConditionEffect.getCondition().test(player)) {
                return sleepingConditionEffect.getMessage();
            }
        }
        return null;
    }

    public static void register() {
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            Origin origin = StateManager.getPlayerState(newPlayer).getOrigin();
            if (origin != null) origin.onRespawn(newPlayer);
        });

        ServerTickEvents.START_WORLD_TICK.register(world -> {
            world.players().forEach((player) -> {
                Origin origin = StateManager.getPlayerState(player).getOrigin();
                if (origin != null) origin.onTick(player);
            });

            MinecraftServer server = world.getServer();
            ArrayList<ItemStack> buffer = new ArrayList<>();
            KeybindItem.UPDATE_MAP.forEach((stack, timestamp) -> {
                if (server.getTickCount() >= timestamp) {
                    stack.set(ModComponents.ON_COOLDOWN, false);
                    buffer.add(stack);
                }
            });
            buffer.forEach(stack -> KeybindItem.UPDATE_MAP.remove(stack));
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayer player) {
                Origin origin = StateManager.getPlayerState(player).getOrigin();
                if (origin == null) {
                    OriginGui.openPicker(player);
                }
            }

            if (entity instanceof ItemEntity item && item.getItem().is(ModItems.KEYBIND_ITEM)) {
                entity.kill(world);
            }
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!(player instanceof ServerPlayer)) return 
InteractionResult.PASS;
            Origin origin = StateManager.getPlayerState(player).getOrigin();
            if (origin != null) return origin.onAttack(player, entity);
            return InteractionResult.PASS;
        });

        EntitySleepEvents.ALLOW_SLEEPING.register((playerEntity, blockPos) -> {
            Component message = canSleep(playerEntity);
            if (message != null) {
                playerEntity.displayClientMessage(message, true);
                return Player.BedSleepingProblem.OTHER_PROBLEM;
            }
            return null;
        });

        UseItemCallback.EVENT.register((playerEntity, world, hand) -> cannotUse(playerEntity.getItemInHand(hand).getItem(), playerEntity) ? InteractionResult.CONSUME : InteractionResult.PASS);
        UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) -> cannotUse(playerEntity.getItemInHand(hand).getItem(), playerEntity) ? InteractionResult.CONSUME : InteractionResult.PASS);
        UseEntityCallback.EVENT.register((playerEntity, world, hand, entity, entityHitResult) -> cannotUse(playerEntity.getItemInHand(hand).getItem(), playerEntity) ? InteractionResult.CONSUME : InteractionResult.PASS);

        CommandRegistrationCallback.EVENT.register(OriginCommand::register);
    }
}
