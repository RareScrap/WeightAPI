package ru.rarescrap.weightapi.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import ru.rarescrap.weightapi.IWeightProvider;

/**
 * CalculateStackWeightEvent выбрасывается, когда в текущем {@link IWeightProvider} вычисляется веc стака.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #entity} владелец {@link #inventory}.<br>
 * {@link #inventory} инвентарь, принаджежащий {@link #entity}, и в котором нахоится {@link #itemStack}.<br>
 * {@link #itemStack} стак, для которого расчитывается вес.<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class CalculateStackWeightEvent extends EntityEvent {
    public final IInventory inventory;
    public ItemStack itemStack;
    public double weight;

    public CalculateStackWeightEvent(ItemStack itemStack, IInventory inventory, double weight, Entity entity) {
        super(entity);
        this.inventory = inventory;
        this.itemStack = itemStack;
        this.weight = weight;
    }
}
