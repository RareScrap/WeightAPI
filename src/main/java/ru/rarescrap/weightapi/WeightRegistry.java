package ru.rarescrap.weightapi;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class WeightRegistry {

    private static IWeightProvider weightProvider;
    private static Map<IInventory, IWeightTracker> trackers = new HashMap<IInventory, IWeightTracker>();

    // Трекеры нужно регистрировать по большому счету только для того, чтобы иметь возможность получить их из других мест
    public static void register(IWeightTracker tracker) {
        trackers.put(tracker.getInventory(), tracker);
        // TODO: Event?
    }

    public static void registerWeightProvider(IWeightProvider provider) {
        weightProvider = provider;
        // TODO: Event?
    }

    public static IWeightTracker getTracker(IInventory inventory) {
        return trackers.get(inventory);
    }

    public static IWeightProvider getWeightProvider() {
        return weightProvider;
    }

    // TODO: Опасно пиздец. Стоит ли оставлять?
    public static IWeightTracker findTracker(ItemStack itemStack) {
        for (Map.Entry<IInventory, IWeightTracker> entry : trackers.entrySet()) {
            IInventory inventory = entry.getKey();
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack itemStack1 = inventory.getStackInSlot(i);
                if (itemStack1 == itemStack) return trackers.get(inventory);
            }
        }

        return null;
    }
}
