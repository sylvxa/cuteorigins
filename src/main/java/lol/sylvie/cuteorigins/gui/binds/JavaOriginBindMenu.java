package lol.sylvie.cuteorigins.gui.binds;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.SimpleGuiElement;
import eu.pb4.sgui.api.gui.SimpleGui;
import lol.sylvie.cuteorigins.origin.Origin;
import lol.sylvie.cuteorigins.state.StateManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class JavaOriginBindMenu extends SimpleGui {
    public JavaOriginBindMenu(ServerPlayer player) {
        super(MenuType.GENERIC_9x3, player, false);
    }
    public static void open(ServerPlayer player) {
        JavaOriginBindMenu gui = new JavaOriginBindMenu(player);
        gui.open();
        gui.updateGui();
    }

    protected void updateGui() {
        this.setTitle(Component.translatable("menu.cuteorigins.binds"));

        Origin origin = StateManager.getPlayerState(this.player).getOrigin();
        if (origin == null) {
            this.close();
            return;
        }

        int i = 0;
        for (ItemStack item : origin.getKeybinds()) {
            GuiElement keybind = new SimpleGuiElement(item, (i1, clickType, slotActionType, slotGuiInterface) -> {
                player.addItem(item);
                this.updateGui();
            });
            this.setSlot(i, keybind);
            i++;
        }
    }
}
