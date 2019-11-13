package ru.rarescrap.examplemod1;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import ru.rarescrap.weightapi.WeightAPI;
import ru.rarescrap.weightapi.WeightRegistry;

/**
 * Кейс №1 - независимая система веса, никак не затрагивающая другие системы.
 * Полностью автономная система со своим эффектом перегруза и своим собственным
 * независимым рендеров веса. Может быть расширена классами наследниками.
 */
@Mod(modid = ExampleMod1.MODID, version = ExampleMod1.VERSION, dependencies = "required-after:weightapi@[0.4.0]")
public class ExampleMod1
{
    public static final String MODID = WeightAPI.MODID + "_example1";
    public static final String VERSION = WeightAPI.VERSION;

    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID.toLowerCase());

    @SidedProxy(serverSide = "ru.rarescrap.examplemod1.CommonProxy", clientSide = "ru.rarescrap.examplemod1.ClientProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        NETWORK.registerMessage(WeightProvider.MessageHandler.class,
                WeightProvider.SyncMessage.class,0, Side.CLIENT);
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerAboutToStartEvent event) {
        WeightRegistry.registerWeightProvider("WeightProvider", new WeightProvider());
    }
}
