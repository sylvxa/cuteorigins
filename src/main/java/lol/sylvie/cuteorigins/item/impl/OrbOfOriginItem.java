package lol.sylvie.cuteorigins.item.impl;

import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.gui.OriginGui;
import lol.sylvie.cuteorigins.state.StateManager;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class OrbOfOriginItem extends SimplePolymerItem {
    public static Identifier IDENTIFIER = CuteOrigins.identifier("orb_of_origin");
    private static final Item ITEM = Items.SLIME_BALL;

    public OrbOfOriginItem() {
        super(new Properties()
                .setId(ResourceKey.create(Registries.ITEM, IDENTIFIER))
                .stacksTo(1)
                .rarity(Rarity.EPIC),
                ITEM);
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipFlag tooltipType, PacketContext context, HolderLookup.Provider lookup) {
        ItemStack out = PolymerItemUtils.createItemStack(itemStack, tooltipType, context, lookup);
        out.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        out.set(DataComponents.ITEM_MODEL, Identifier.withDefaultNamespace("slime_ball"));
        return out;
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        itemStack.consume(1, user);
        if (user instanceof ServerPlayer player) {
            StateManager.getPlayerState(player).resetOrigin(player);
            OriginGui.openPicker(player);
        }
        return InteractionResult.SUCCESS;
    }
}
