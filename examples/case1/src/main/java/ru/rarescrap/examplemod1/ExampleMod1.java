package ru.rarescrap.examplemod1;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import ru.rarescrap.weightapi.WeightAPI;
import ru.rarescrap.weightapi.WeightRegistry;

import java.io.File;

/**
 * Кейс №1 - независимая система веса, никак не затрагивающая другие системы.
 * Полностью автономная система со своим эффектом перегруза и своим собственным
 * независимым рендеров веса. Может быть расширена классами наследниками.
 */
@Mod(modid = ExampleMod1.MODID, version = ExampleMod1.VERSION, dependencies = "required-after:weightapi@0.3.0")
public class ExampleMod1
{
    public static final String MODID = WeightAPI.MODID + "_example1";
    public static final String VERSION = WeightAPI.VERSION;

    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID.toLowerCase());

    @SideOnly(Side.CLIENT)
    public static RenderGameOverlayEvent.ElementType WEIGHT = EnumHelper.addEnum(RenderGameOverlayEvent.ElementType.class, "WEIGHT");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        EventHandler eventHandler1 = new EventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler1);
        FMLCommonHandler.instance().bus().register(eventHandler1);
        NETWORK.registerMessage(WeightProvider.MessageHandler.class,
                WeightProvider.SyncMessage.class,0, Side.CLIENT);
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerAboutToStartEvent event) {
        WeightRegistry.registerWeightProvider("WeightProvider", new WeightProvider());
    }
}
