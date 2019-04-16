package ru.rarescrap.weightapi.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import ru.rarescrap.weightapi.IWeightProvider;

/**
 * CalculateMaxWeightEvent выбрасывается, когда в текущем {@link IWeightProvider} вычисляется
 * общая вместимость инвентаря.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #entity} владелец {@link #inventory}.<br>
 * {@link #inventory} инвентарь, принаджежащий {@link #entity}, и для которого расчитывается
 * максимальный вмесимый вес.<br>
 * {@link #maxWeight} максимальная вместимость инвентаря, предварительно вычесленная
 * текущим {@link IWeightProvider}'ом.<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class CalculateMaxWeightEvent extends EntityEvent {
    public final IInventory inventory;
    public double maxWeight;

    public CalculateMaxWeightEvent(IInventory inventory, double maxWeight, Entity entity) {
        super(entity);
        this.maxWeight = maxWeight;
        this.inventory = inventory;
    }
}
