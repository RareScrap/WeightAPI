package ru.rarescrap;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;

/**
 * Интерфейс для отслеживания изменения веса в инвентарях
 */
public interface IWeightTracker {
    /**
     * @return Инвентарь, который отслеживает данный {@link IWeightTracker}
     */
    IInventory getInventory();

    /**
     * @return Максимальная вместимость инвентаря
     */
    int getMaxWeight();

    /**
     * @return Текущий вес инвентаря
     */
    int getCurrentWeight();

    /**
     * @return True, если инвентарь перегружен (обычно, когда вес инвентаря
     *         больше максимально допустимого). Иначе - false.
     */
    boolean isOverloaded();

    // TODO: Нужно ли?
    Entity getOwner();

    // TODO: Нужно ли?
//    void onCurrentWeightChanged();
}
