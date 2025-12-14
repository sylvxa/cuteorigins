package lol.sylvie.cuteorigins.mixin;

import lol.sylvie.cuteorigins.item.ModItems;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class SlotMixin {
    @Shadow @Final public Container container;

    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    public void origins$noKeybindInsertion(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(ModItems.KEYBIND_ITEM) && !(container instanceof Inventory)) cir.setReturnValue(false);
    }
}
