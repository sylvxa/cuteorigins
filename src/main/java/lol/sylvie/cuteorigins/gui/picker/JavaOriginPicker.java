package lol.sylvie.cuteorigins.gui.picker;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import lol.sylvie.cuteorigins.CuteOrigins;
import lol.sylvie.cuteorigins.gui.Icons;
import lol.sylvie.cuteorigins.origin.Origin;
import lol.sylvie.cuteorigins.power.Power;
import lol.sylvie.cuteorigins.state.StateManager;
import lol.sylvie.cuteorigins.util.OriginRegistries;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;

import java.util.List;
import java.util.stream.IntStream;

public class JavaOriginPicker extends SimpleGui {
    private static final int SLOT_COUNT = 9 * 6;
    private final List<Origin> origins;
    private int index = 0;

    public JavaOriginPicker(ServerPlayerEntity player) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);
        this.origins = OriginRegistries.ORIGIN_REGISTRY.getOriginsAlphabetically();
        // Start on the "human" page
        index = IntStream.range(0, origins.size())
                .filter(i -> origins.get(i).identifier().equals(CuteOrigins.identifier("human")))
                .findFirst()
                .orElse(0);
    }

    @Override
    public boolean canPlayerClose() {
        return false;
    }

    public static void open(ServerPlayerEntity player) {
        JavaOriginPicker gui = new JavaOriginPicker(player);
        gui.open();
        gui.updateGui();
    }

    protected void updateGui() {
        this.setTitle(Text.translatable("menu.cuteorigins.picker"));

        // Setup
        for (int i = 0; i < SLOT_COUNT; i++) {
            this.clearSlot(i);
        }

        // Border
        ItemStack borderStack = Items.MAGENTA_STAINED_GLASS_PANE.getDefaultStack();
        for (int i = 0; i < 9; i++) {
            this.setSlot(i, borderStack);
            this.setSlot(i + (9 * 5), borderStack);
        }

        for (int i = 8; i <= 45; i += 9) {
            this.setSlot(i - 8, borderStack);
            this.setSlot(i, borderStack);
        }

        Origin origin = origins.get(index);
        GuiElement originIcon = new GuiElementBuilder(origin.icon())
                .setName(origin.getName().copy().formatted(Formatting.BOLD, Formatting.WHITE))
                .setLore(List.of(origin.getDescription().copy().formatted(Formatting.GRAY)))
                .build();

        this.setSlot(13, originIcon);

        // Pagination
        this.setSlot(45, new GuiElementBuilder(Items.PLAYER_HEAD)
                .setSkullOwner(Icons.ARROW_LEFT)
                .setName(Text.translatable("menu.cuteorigins.back").copy().formatted(Formatting.BOLD))
                .setRarity(Rarity.COMMON)
                .setCallback((i, clickType, slotActionType) -> {
                    index--;
                    if (index < 0) index = origins.size() - 1;
                    updateGui();
                }));

        this.setSlot(53, new GuiElementBuilder(Items.PLAYER_HEAD)
                .setSkullOwner(Icons.ARROW_RIGHT)
                .setName(Text.translatable("menu.cuteorigins.next").copy().formatted(Formatting.BOLD))
                .setRarity(Rarity.COMMON)
                .setCallback((i, clickType, slotActionType) -> {
                    index++;
                    if (index >= origins.size()) index = 0;
                    updateGui();
                }));

        this.setSlot(49, new GuiElementBuilder(Items.PLAYER_HEAD)
                .setSkullOwner(Icons.CHECKMARK)
                .setName(Text.translatable("menu.cuteorigins.choose").copy().formatted(Formatting.BOLD))
                .setRarity(Rarity.COMMON)
                .setLore(List.of(Text.translatable("menu.cuteorigins.beware").copy().formatted(Formatting.RED)))
                .setCallback((i, clickType, slotActionType) -> {
                    this.close();
                    StateManager.getPlayerState(player)
                            .setOrigin(player, origin);
                    player.sendMessage(Text.translatable("menu.cuteorigins.success", origin.getName()), true);
                }));

        int i = 19;
        for (Power power : origin.getDisplayPowers()) {
            GuiElement powerIcon = new GuiElementBuilder(Items.PAPER)
                    .setName(power.isNegative() ? power.getName().copy().formatted(Formatting.RED) : power.getName())
                    .setLore(List.of(power.getDescription().copy().formatted(Formatting.GRAY)))
                    .build();
            this.setSlot(i, powerIcon);

            i++;
            if (i == 26) i = 28; // Hacky wrapping
            if (i == 35) i = 37; // Hacky wrapping
            if (i > 43) break; // If we get that far, I really don't care anymore
        }
    }
}
