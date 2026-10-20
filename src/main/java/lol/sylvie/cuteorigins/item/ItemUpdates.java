package lol.sylvie.cuteorigins.item;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

// i really hate this
public class ItemUpdates {
    private record Scheduled(ServerPlayer player, int timestamp) {}
    private static final ArrayList<Scheduled> UPDATES = new ArrayList<>();

    // this needs to update all keybind items because a player can have several
    // keybinds mapped to one action
    public static void update(ServerPlayer player) {
        Inventory inventory = player.getInventory();
        int i = 0;
        for (ItemStack comparing : inventory.getNonEquipmentItems()) {
            if (comparing.is(ModItems.KEYBIND_ITEM))
                player.connection.send(inventory.createInventoryUpdatePacket(i));


            i++;
        }
    }

    public static void tick(MinecraftServer server) {
        ArrayList<Scheduled> forRemoval = new ArrayList<>();
        UPDATES.forEach(scheduled -> {
            if (server.getTickCount() < scheduled.timestamp) return;

            update(scheduled.player);
            forRemoval.add(scheduled);
        });
        forRemoval.forEach(UPDATES::remove);
    }

    public static void schedule(ServerPlayer player, int timestamp) {
        UPDATES.add(new Scheduled(player, timestamp));
    }
}
