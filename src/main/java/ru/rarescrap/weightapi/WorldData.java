package ru.rarescrap.weightapi;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

import java.util.Arrays;

import static ru.rarescrap.weightapi.APIContainer.LOGGER;
import static ru.rarescrap.weightapi.APIContainer.MODID;

/**
 * Постоянное хранилище данных, необходимых для работы WeightAPI.
 * Не предназначен для использования за пределами WeightAPI.
 */
@Deprecated
@SuppressWarnings("DeprecatedIsStillUsed")
public class WorldData extends WorldSavedData { // reflection needed modifier
    /** Название системы веса, действовавшая на сервере до отключения  */
    private String lastWeightProvider;

    public WorldData(String p_i2141_1_) { // reflection needed modifier
        super(p_i2141_1_);
    }

    static WorldData get(World world) {
        MapStorage storage = world.perWorldStorage;
        WorldData worldData = (WorldData) storage.loadData(WorldData.class, MODID);

        if (worldData == null) {
            worldData = new WorldData(MODID);
            storage.setData(MODID, worldData);
        }

        return (WorldData) storage.loadData(WorldData.class, MODID);
    }

    void restoreLastWeightProvider() {
        if (lastWeightProvider != null && !WeightRegistry.activateWeightProvider(lastWeightProvider)) {
            String logMsg = StatCollector.translateToLocalFormatted("log.weightprovider.restore.warning",
                    lastWeightProvider, Arrays.asList(WeightRegistry.getProvidersNames()));
            LOGGER.warn(logMsg);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        lastWeightProvider = compound.getString("last_weightprovider");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        if (WeightRegistry.getActiveWeightProvider() != null)
            compound.setString("last_weightprovider", WeightRegistry.getActiveProviderName());
    }
}