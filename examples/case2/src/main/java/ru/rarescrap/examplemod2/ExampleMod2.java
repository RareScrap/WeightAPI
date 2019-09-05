package ru.rarescrap.examplemod2;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import ru.rarescrap.test.case2.NewConfigurableWeightProvider;
import ru.rarescrap.weightapi.WeightAPI;
import ru.rarescrap.weightapi.WeightRegistry;

import java.io.File;

/**
 * Кейс №2 - система веса, расширяющая уже существующую систему {@link ru.rarescrap.simpleweightsystem.ConfigurableWeightProvider}.
 * Представленная в этом кейсе система будет использовать тот рендер и поведение при перегрузе, которое указано для
 * ConfigurableWeightProvider.
 */
@Mod(modid = ExampleMod2.MODID, version = ExampleMod2.VERSION, dependencies = "required-after:configurableweight")
public class ExampleMod2 {
    public static final String MODID = WeightAPI.MODID + "_example2";
    public static final String VERSION = WeightAPI.VERSION;

    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID.toLowerCase());

    // TODO: Является ли хорошим подходом хранить ссылку на систему веса?
    //NewConfigurableWeightProvider newConfigurableWeightProvider;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        NETWORK.registerMessage(NewConfigurableWeightProvider.MessageHandler.class,
                NewConfigurableWeightProvider.NewSyncMessage.class,0, Side.CLIENT);
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerAboutToStartEvent event) {
        File configFile = new File(Loader.instance().getConfigDir(),"dummy_configurableweight_for_case2.cfg");
        if (configFile.exists()) {
            WeightRegistry.registerWeightProvider("NewConfigurableWeightProvider", new NewConfigurableWeightProvider(configFile));
        } else throw new RuntimeException("[NewConfigurableWeight] Can't find config file. Weights not loaded!");
    }
}
