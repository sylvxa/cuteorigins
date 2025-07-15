package lol.sylvie.cuteorigins.power.effect.impl.shulker;

import com.mojang.serialization.Codec;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ShulkerInventory extends SimpleInventory {
    private static final int SIZE = 9;

    // TODO: items lose their position in the inventory upon restart
    // :(
    public static final Codec<ShulkerInventory> CODEC = ItemStack.CODEC.sizeLimitedListOf(SIZE)
            .xmap(
                    list -> new ShulkerInventory(
                            list.stream()
                                    .map(stack -> stack == null ? ItemStack.EMPTY : stack)
                                    .map(stack -> stack.isEmpty() || stack.getItem() == net.minecraft.item.Items.AIR || stack.getCount() <= 0 || stack.getCount() > 99
                                            ? ItemStack.EMPTY
                                            : stack)
                                    .toList()
                    ),
                    inventory -> inventory.heldStacks.stream()
                            .filter(stack -> !stack.isEmpty() && stack.getCount() > 0 && stack.getCount() <= 99 && stack.getItem() != net.minecraft.item.Items.AIR)
                            .toList()
            );

    private static ItemStack[] defaultedItems(List<ItemStack> items) {
        ItemStack[] array = new ItemStack[SIZE];
        Arrays.fill(array, ItemStack.EMPTY);
        for (int i = 0; i < Math.min(items.size(), SIZE); i++) {
            array[i] = items.get(i);
        }
        return array;
    }

    public ShulkerInventory() {
        super(SIZE);
    }

    public ShulkerInventory(List<ItemStack> items) {
        super(defaultedItems(
                items.stream()
                        .map(stack -> stack == null ? ItemStack.EMPTY : stack)
                        .map(stack -> stack.isEmpty() || stack.getItem() == net.minecraft.item.Items.AIR || stack.getCount() <= 0 || stack.getCount() > 99
                                ? ItemStack.EMPTY
                                : stack)
                        .toList()
        ));
    }
}