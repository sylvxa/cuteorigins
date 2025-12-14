package lol.sylvie.cuteorigins.power.effect.impl.shulker;

import com.google.gson.JsonObject;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.power.effect.Effect;
import lol.sylvie.cuteorigins.power.effect.impl.DebugEffect;
import lol.sylvie.cuteorigins.state.StateManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class ShulkerInventoryEffect extends Effect {
    public static final Identifier IDENTIFIER = CuteOrigins.identifier("shulker_inventory");
    protected ShulkerInventoryEffect() {
        super(IDENTIFIER, true);
    }

    @Override
    public void onAction(ServerPlayer player) {
        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("menu.cuteorigins.shulker_inventory");
            }

            @Override
            public @Nullable AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
                return new ChestMenu(MenuType.GENERIC_3x3, syncId, playerInventory, StateManager.getPlayerState(player).getShulkerInventory(), 1);
            }
        });
    }

    public static Effect fromJson(JsonObject object) {
        return new ShulkerInventoryEffect();
    }
}
