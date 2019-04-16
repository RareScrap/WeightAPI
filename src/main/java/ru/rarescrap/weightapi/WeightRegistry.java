package ru.rarescrap.weightapi;

public class WeightRegistry {

    private static IWeightProvider weightProvider;

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
}
