package ru.rarescrap.weightapi;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import net.minecraftforge.event.world.WorldEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.rarescrap.weightapi.command.GetActiveWeightProvider;
import ru.rarescrap.weightapi.command.GetWeightProviders;
import ru.rarescrap.weightapi.command.SetWeightProvider;

@Mod(modid = WeightAPI.MODID, version = WeightAPI.VERSION)
public class WeightAPI {
    public static final String MODID = "weightapi";
    public static final String VERSION = "0.4.0";

    static final Logger LOGGER = LogManager.getLogger("WeightAPI");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new GetActiveWeightProvider());
        event.registerServerCommand(new GetWeightProviders());
        event.registerServerCommand(new SetWeightProvider());
        // TODO: Команда отключения активного провайдера
    }

    @Mod.EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        // т.к. при завершении мира в сингле провайдер все еще
        // сохраняется и вызовет краш при следующем заходе в игру
        WeightRegistry.clearProvider();
    }

    // Синхронизирует систему веса на сервере с клиентом при подключении игрока
    @SubscribeEvent(priority = EventPriority.LOW) // TODO: ХЗ качем приоритет. Но жопой чую что он должен работать после других эвентов
    public void onClientConnect(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.player.worldObj.isRemote && WeightRegistry.getActiveWeightProvider() != null && WeightRegistry.shouldSyncProvider())
            WeightRegistry.getActiveWeightProvider().sync((EntityPlayerMP) event.player);
    }

    // Восстанавливаем предыдущую систему веса, если таковая имеется
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) { //TODO: FMLServerStartedEvent?
        if (!event.world.isRemote) WorldData.get(event.world).restoreLastWeightProvider(event.world); // TODO: Срабатывает для каждого димешна, что намекает на использования своей системы веса в каждой димешне. Текущее поведение хоть и работает норм, но не является верным. Возможно что использовать WorldSavedData было неудачным решением, т.к. сам этот механизм сохранет инфу в каждом из миров, коих много
    }

    // Сохраняем текущую систему веса, чтобы при следующем запуске сервера восстановить ее
    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event) { //TODO: FMLServerStoppedEvent?
        if (!event.world.isRemote) WorldData.get(event.world).markDirty();
    }

    // Присоединяем игрокам трекер инвентаря
    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayerMP && PlayerWeightTracker.get((EntityPlayerMP) event.entity) == null)
            PlayerWeightTracker.register((EntityPlayerMP) event.entity);
    }

    // И присоединяем его к открытым контейнерам
    @SubscribeEvent
    public void onPlayerOpenContainer(PlayerOpenContainerEvent e) {
        PlayerWeightTracker tracker = PlayerWeightTracker.get((EntityPlayerMP) e.entityPlayer);
        /* Довольно узкое место. Дело в том, что PlayerOpenContainerEvent не совсем соотстветвует своему
         * описанию. Это скорее "CanInteractWithContainerEvent". Это из-за того, что этот евент по сути
         * выбрасывается каждый тик. А открываться контейнер каждый тик не может по логике.
         * Однако и этот "ущербный" эвент можно использовать в нужном ключе (в данном случае, для присоединения
         * слушателя изменения инвентаря (ICrafting). Только нужно позаботиться, чтобы он не добавлялся дважды. */
        if (!e.entityPlayer.openContainer.crafters.contains(tracker))
            tracker.attachListener(); // TODO: а если canInteractWith == false?
    }
}
