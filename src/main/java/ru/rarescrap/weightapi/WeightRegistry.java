package ru.rarescrap.weightapi;

public class WeightRegistry {

    private static IWeightProvider weightProvider;

    // Трекеры нужно регистрировать по большому счету только для того, чтобы иметь возможность получить их из других мест
    public static void register(IWeightTracker tracker) {
        trackers.put(tracker.getInventory(), tracker);
        // TODO: Event?
    }

    public static void registerWeightProvider(IWeightProvider provider) {
        if (weightProvider == null) weightProvider = provider;
        /* Не вижу смысла регистрировать новый провайдер если в системе уже есть зареганый.
         * Тогда придется парить с реализацией замеяемости, что не нужно, имхо. И какая вообще
         * логика в установке двух систем веса? */
        else throw new RuntimeException("Attempt to change registered weight provider: "
                + weightProvider.getClass().getName() + " to " + provider.getClass().getName());
    }

    // не для использования за пределами апи
    static void clearProvider() {
        weightProvider = null;
    }

    public static IWeightProvider getWeightProvider() {
        return weightProvider;
    }
}
