package ru.rarescrap.weightapi;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;

@Mod(modid = APIContainer.MODID, version = APIContainer.VERSION)
public class APIContainer  {
    public static final String MODID = "weightapi";
    public static final String VERSION = "0.2.0";

    @Mod.EventHandler
    public void onStop(FMLServerStoppedEvent event) {
        // т.к. при завершении мира в сингле провайдер все еще
        // сохраняется и вызовет краш при следующем заходе в игру
        WeightRegistry.clearProvider();
    }
}
