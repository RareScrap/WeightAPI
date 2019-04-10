package ru.rarescrap.weightapi;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Интерфейс для объктов, способных расчитывать вес предметов
 */
public interface IWeightProvider {

    /**
     * Вычисляет вес стака
     * @param itemStack Стак, вес которого нужно вычислить
     * @param inventory Инвентарь, где в данный момент находится стак
     * @param owner Владелец инентаря
     * @return Вес стака
     */
    /*
     * Тут нет простых методов вроде getWeight(Item), т.к. расчет веса может основываться и на оссобенностях
     * игрока (вдруг у него есть способность на компенсацию веса?), и на оссобенностях инвентаря (вдруг руда в
     * этом инвентаре не имее веса?), в котором находится стак... Именно поэтому этот метод имеет так много
     * аргументов - ради гибкости использования.
     */
    double getWeight(ItemStack itemStack, IInventory inventory, Entity owner);


}
