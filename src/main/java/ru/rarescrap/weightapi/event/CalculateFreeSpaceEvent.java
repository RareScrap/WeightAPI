package ru.rarescrap.weightapi.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import ru.rarescrap.weightapi.IWeightProvider;

/**
 * CalculateFreeSpaceEvent выбрасывается, когда в текущем {@link IWeightProvider} вычисляется свободное
 * место в инвентаре.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #entity} владелец {@link #inventory}.<br>
 * {@link #inventory} инвентарь, принаджежащий {@link #entity}, и для которого расчитывается свободное место.<br>
 * {@link #freeSpace} свободно место, предварительно вычесленное текущим {@link IWeightProvider}'ом.<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class CalculateFreeSpaceEvent extends EntityEvent {
    public final IInventory inventory;
    public double freeSpace;

    public CalculateFreeSpaceEvent(IInventory inventory, double freeSpace, Entity entity) {
        super(entity);
        this.inventory = inventory;
        this.freeSpace = freeSpace;
    }
}
