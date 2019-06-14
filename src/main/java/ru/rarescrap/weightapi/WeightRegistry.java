package ru.rarescrap.weightapi;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import ru.rarescrap.weightapi.event.WeightProviderChangedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Главный механизм управления системами веса.
 */
public class WeightRegistry {
    /** Система веса, используемая сервером в настоящий момент */
    private static IWeightProvider activeWeightProvider;

    private static HashMap<String, IWeightProvider> providers = new HashMap<String, IWeightProvider>();

    /**
     * Устанавливает действующий {@link IWeightProvider}, к которому будут обращаться все инвентари игры
     * (в том числе и из других модов) для вычисления веса, свободного места и т.д. Провайдер дожен быть
     * предварительно зарегистрировать при помощи {@link #registerWeightProvider(String, IWeightProvider)}
     * <br>
     * <strong>ВНИМАНИЕ!</strong> Главным способ задания текущего {@link IWeightProvider}'а является
     * команда {@link ru.rarescrap.weightapi.command.SetWeightProvider}. При перезапуске сервера предыдущий
     * IWeightProvider будет восстановлен. Поэтому НЕ ИСПОЛЬЗУЙТЕ этот метод для обхода вышеописанного подхода.
     * Используйте его только если понимаете что делаете и просчитали все возможные конфликты с другими модами.
     * Исключение - активация {@link IWeightProvider}'а на клиенте, если тот был активирован на сервере.
     * @param providerName Название провайдера, который требуется применить
     * @param world Мир, в котором произошла активация системы веса. Несмотря на это
     * @return True, если провайдер был обновен. Иначе - false.
     */
    @Deprecated
    public static boolean activateWeightProvider(String providerName, World world) {
        IWeightProvider newProvider = providers.get(providerName);
        if (newProvider == null) return false;

        MinecraftForge.EVENT_BUS.post(new WeightProviderChangedEvent.Pre(activeWeightProvider, newProvider, world));
        activeWeightProvider = newProvider;
        if (!world.isRemote && shouldSyncProvider()) syncWithAllPlayers();
        MinecraftForge.EVENT_BUS.post(new WeightProviderChangedEvent.Post(activeWeightProvider, newProvider, world));
        return true;
    }

    static boolean shouldSyncProvider() {
        return MinecraftServer.getServer().isDedicatedServer() || ((IntegratedServer) MinecraftServer.getServer()).getPublic();
    }

    private static void syncWithAllPlayers() {
        for (WorldServer worldServer : MinecraftServer.getServer().worldServers) {
            for (EntityPlayerMP player : (List<EntityPlayerMP>) worldServer.playerEntities) {
                activeWeightProvider.sync(player);
            }
        }
    }

    /**
     * Регистрирует {@link IWeightProvider}
     */
    public static void registerWeightProvider(String providerName, IWeightProvider provider) {
        providers.put(providerName, provider); // TODO: Тут нужен эвент? Кому он может пригодиться?
        // TODO: евент при удалении системы веса
    }

    // не для использования за пределами апи
    static void clearProvider() {
        activeWeightProvider = null;
    }

    /**
     * @return Имена доступных систем веса
     */
    public static String[] getProvidersNames() {
        return providers.keySet().toArray(new String[0]);
    }

    /**
     * @return Имя активной системы веса.
     */
    public static String getActiveProviderName() {
        for (Map.Entry<String, IWeightProvider> entry : providers.entrySet()) {
            if (entry.getValue() == activeWeightProvider) return entry.getKey();
        }
        throw new IllegalStateException("Active provider not set. Use getWeightProvider to check it.");
    }

    /**
     * Возвращает текущий объект, отвечающий за вычисления веса, свободного места и т.д.
     * @return текущий {@link IWeightProvider}
     */
    public static IWeightProvider getActiveWeightProvider() {
        return activeWeightProvider;
    }

    /**
     * @param providerName Имя системы выса
     * @return Система веса с данным именем
     */
    public static IWeightProvider getWeightProvider(String providerName) {
        return providers.get(providerName);
    }

    // TODO: Зачем?
    public static boolean isProviderActive(IWeightProvider weightProvider) {
        return weightProvider == getActiveWeightProvider(); // TODO: Будет ли такое сравнение норм? Или сравнивать на принадлежность классу? Хз пока.
    }
}
