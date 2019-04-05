package ru.rarescrap.event;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.event.entity.EntityEvent;

public class WeightChangedEvent extends EntityEvent {
    public IInventory inventory;
    public int prevWeight;
    public int currentWeight;
    public boolean isOverloaded; // Это лучше, чем делать отдельный евент на перегрузку
    // TODO: Итемстак, который привел к изменению веса?
    // TODO: Трекер тут?

    public WeightChangedEvent(IInventory inventory, int prevWeight, int currentWeight, boolean isOverloaded, Entity owner) { //TODO: Зачем инвентарь если есть овнер который его содержит? Или может быть ситуация что овнер может не содержать инвентаря, которым же управляет?
        super(owner);
        this.inventory = inventory;
        this.prevWeight = prevWeight;
        this.currentWeight = currentWeight;
        this.isOverloaded = isOverloaded;
    }
}
