package ru.rarescrap.weightapi;

public class WeightRegistry {

    private static IWeightProvider weightProvider;

    /**
     * Регистрирует {@link IWeightProvider}, к которому будут обращаться все инвентари игры
     * (в том числе и из других модов) для вычисления веса, свободного места и т.д.
     * Если {@link IWeightProvider}, уже установлен, то повторная регистрация вызовет краш.
     * @param provider
     */
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

    /**
     * Возвращает текущий объект, отвечающий за вычисления веса, свободного места и т.д.
     * @return текущий {@link IWeightProvider}
     */
    public static IWeightProvider getWeightProvider() {
        return weightProvider;
    }
}
