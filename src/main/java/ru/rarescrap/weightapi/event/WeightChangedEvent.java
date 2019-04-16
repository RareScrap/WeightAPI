package ru.rarescrap.weightapi.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * WeightChangedEvent выбрасывается, когда вес инвентаря изменился.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #entity} владелец {@link #inventory}.<br>
 * {@link #inventory} инвентарь, принаджежащий {@link #entity}, в котором произошло изменение веса.<br>
 * {@link #prevWeight} вес инвентаря до изменений.<br>
 * {@link #currentWeight} текущий вес инвентаря.<br>
 * {@link #isOverloaded} перегружен ли инвентарь.<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class WeightChangedEvent extends EntityEvent {
    public IInventory inventory;
    public double prevWeight;
    public double currentWeight;
    public boolean isOverloaded; // Это лучше, чем делать отдельный евент на перегрузку
    // TODO: Итемстак, который привел к изменению веса?

    public WeightChangedEvent(IInventory inventory, double prevWeight, double currentWeight, boolean isOverloaded, Entity owner) { //TODO: Зачем инвентарь если есть овнер который его содержит? Или может быть ситуация что овнер может не содержать инвентаря, которым же управляет?
        super(owner);
        this.inventory = inventory;
        this.prevWeight = prevWeight;
        this.currentWeight = currentWeight;
        this.isOverloaded = isOverloaded;
    }
}
