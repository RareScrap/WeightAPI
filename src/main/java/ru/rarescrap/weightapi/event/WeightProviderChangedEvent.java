package ru.rarescrap.weightapi.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import ru.rarescrap.weightapi.IWeightProvider;
import ru.rarescrap.weightapi.WeightRegistry;

// TODO: отменяемость это евента все еще на этапе обсуждения
/**
 * WeightProviderChangedEvent выбрасывается, когда изменяется текущая система веса.<br>
 * Если метод принимает этот {@link Event} как параметр, метод будет
 * срабатывать на каждого наследника этого класса.<br>
 * <br>
 * {@link #deactivatedProvider} содержит отключенную систему веса.<br>
 * {@link #currentProvider} содержит активную систему веса.<br>
 * {@link #world} содержит мир, из которого послался данный евент.<br>
 * <br>
 * Все наследники этого эвента выбрасываются в {@link MinecraftForge#EVENT_BUS}.
 **/
public class WeightProviderChangedEvent extends Event {

    public final IWeightProvider deactivatedProvider;
    public final IWeightProvider currentProvider;
    public final World world;

    public WeightProviderChangedEvent(IWeightProvider deactivatedProvider, IWeightProvider currentProvider, World world) {
        this.deactivatedProvider = deactivatedProvider;
        this.currentProvider = currentProvider;
        this.world = world;
    }

    /**
     * WeightProviderChangedEvent.Pre выбрасывается до того, как новая система веса вступила в силу.<br>
     * <br>
     * Этот евенты выбрасывается в {@link WeightRegistry#activateWeightProvider(String, World)}.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * Этот евент не имеет результата. {@link HasResult}<br>
     * <br>
     * Этот евент выбрасывается в {@link MinecraftForge#EVENT_BUS}.
     **/
    public static class Pre extends WeightProviderChangedEvent {
        public Pre(IWeightProvider deactivatedProvider, IWeightProvider currentProvider, World world) {
            super(deactivatedProvider, currentProvider, world);
        }
    }

    /**
     * WeightProviderChangedEvent.Pre выбрасывается после того, как новая система веса вступила в силу.<br>
     * <br>
     * Этот евенты выбрасывается в {@link WeightRegistry#activateWeightProvider(String, World)}.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * Этот евент не имеет результата. {@link HasResult}<br>
     * <br>
     * Этот евент выбрасывается в {@link MinecraftForge#EVENT_BUS}.
     **/
    public static class Post extends WeightProviderChangedEvent {
        public Post(IWeightProvider deactivatedProvider, IWeightProvider currentProvider, World world) {
            super(deactivatedProvider, currentProvider, world);
        }
    }
}