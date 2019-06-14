package ru.rarescrap.weightapi;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = APIContainer.MODID, version = APIContainer.VERSION)
public class APIContainer  {
    public static final String MODID = "weightapi";
    public static final String VERSION = "0.2.0";

    static final Logger LOGGER = LogManager.getLogger("WeightAPI");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void onStop(FMLServerStoppedEvent event) {
        // т.к. при завершении мира в сингле провайдер все еще
        // сохраняется и вызовет краш при следующем заходе в игру
        WeightRegistry.clearProvider();
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) { //TODO: FMLServerStartedEvent?
        if (!event.world.isRemote) WorldData.get(event.world).restoreLastWeightProvider(event.world); // TODO: Срабатывает для каждого димешна, что намекает на использования своей системы веса в каждой димешне. Текущее поведение хоть и работает норм, но не является верным. Возможно что использовать WorldSavedData было неудачным решением, т.к. сам этот механизм сохранет инфу в каждом из миров, коих много
    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event) { //TODO: FMLServerStoppedEvent?
        if (!event.world.isRemote) WorldData.get(event.world).markDirty();
    }
}
