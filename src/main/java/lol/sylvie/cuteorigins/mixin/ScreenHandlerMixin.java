package lol.sylvie.cuteorigins.mixin;

import lol.sylvie.cuteorigins.item.ModItems;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerMenu.class)
public class ScreenHandlerMixin {
    @Inject(method = "canItemQuickReplace", at = @At("HEAD"), cancellable = true)
    private static void origins$noKeybindInsertion(Slot slot, ItemStack stack, boolean allowOverflow, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(ModItems.KEYBIND_ITEM) && !(slot.container instanceof Inventory)) cir.setReturnValue(false);
    }
}
