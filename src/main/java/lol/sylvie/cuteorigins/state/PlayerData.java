package lol.sylvie.cuteorigins.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lol.sylvie.cuteorigins.item.ModItems;
import lol.sylvie.cuteorigins.origin.Origin;
import lol.sylvie.cuteorigins.power.effect.impl.shulker.ShulkerInventory;
import lol.sylvie.cuteorigins.util.OriginRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PlayerData {
    public static final Codec<PlayerData> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    Identifier.CODEC.fieldOf("origin").forGetter(PlayerData::getSelectedOrigin),
                    ShulkerInventory.CODEC.fieldOf("shulker_inventory").forGetter(PlayerData::getShulkerInventory)
            )
            .apply(instance, PlayerData::new));

    public Identifier selectedOrigin = null;
    public ShulkerInventory shulkerInventory = new ShulkerInventory();

    // Constructors
    public PlayerData() {}

    public PlayerData(Identifier identifier, ShulkerInventory inventory) {
        this.selectedOrigin = identifier;
        this.shulkerInventory = inventory;
    }

    // Getters
    public Identifier getSelectedOrigin() {
        return selectedOrigin;
    }

    public @Nullable Origin getOrigin() {
        return OriginRegistries.ORIGIN_REGISTRY.getOrigin(this.getSelectedOrigin());
    }

    public ShulkerInventory getShulkerInventory() {
        return shulkerInventory;
    }

    public void setOrigin(ServerPlayer player, Origin origin) {
        Origin oldOrigin = getOrigin();
        if (oldOrigin != null) oldOrigin.onRemoved(player);
        this.selectedOrigin = origin.identifier();
        origin.onChosen(player);

        player.getInventory().clearOrCountMatchingItems(i -> i.is(ModItems.KEYBIND_ITEM), -1, player.inventoryMenu.getCraftSlots());
        for (ItemStack item : origin.getKeybinds()) {
            player.addItem(item);
        }
    }

    public void resetOrigin(ServerPlayer player) {
        Origin oldOrigin = getOrigin();
        if (oldOrigin != null) oldOrigin.onRemoved(player);
        this.selectedOrigin = null;
    }
}
