package lol.sylvie.cuteorigins.power.effect.impl;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.effect.Effect;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class ForceElytraEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("force_elytra");

    protected ForceElytraEffect() {
        super(IDENTIFIER, false);
    }

    @Override
    public void onTick(ServerPlayer player) {
        if (!player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
            ItemStack elytra = Items.ELYTRA.getDefaultInstance();
            Registry<Enchantment> enchantmentRegistry = player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            elytra.enchant(enchantmentRegistry.get(Enchantments.BINDING_CURSE.identifier()).orElseThrow(), 1);
            elytra.enchant(enchantmentRegistry.get(Enchantments.VANISHING_CURSE.identifier()).orElseThrow(), 1);
            elytra.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
            player.setItemSlot(EquipmentSlot.CHEST, elytra);
        }
    }

    @Override
    public void onRemoved(ServerPlayer player) {
        ItemStack slot = player.getItemBySlot(EquipmentSlot.CHEST);
        if (slot.is(Items.ELYTRA)) slot.setCount(0);
    }

    public static Effect fromJson(JsonObject object) {
        return new ForceElytraEffect();
    }
}
