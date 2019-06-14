package ru.rarescrap.weightapi;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Интерфейс для объктов, способных расчитывать вес предметов и инвентарей
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

    /**
     * Вычисляет вес инвентаря
     * @param inventory Инвенарь, вес которого нужно вычислить
     * @param owner Владелец инвентаря
     * @return Общий вес предметов в инвентаре
     */
    double getWeight(IInventory inventory, Entity owner);

    /**
     * Определяет, перегружен ли инвентарь.
     * @param inventory Инвентарь, перегруз которого нужно выявить.
     * @param owner Владелец инвентаря
     * @return True, если инвентарь перегружен. Иначе - false.
     */
    boolean isOverloaded(IInventory inventory, Entity owner);

    /**
     * Вычисляет свободное место инвентаря
     * @param inventory Инвенарь, для которого нужно вычислить свободное место
     * @param owner Владелец инвентаря
     * @return Оставшееся место в инвентаре, при пересечении которого инвентарь будет перегружен
     */
    double getFreeSpace(IInventory inventory, Entity owner);

    /**
     * Вычисляет максимальную вместимость инвентаря
     * @param inventory Инвенарь, вместимость которого нужно вычислить
     * @param owner Владелец инвентаря
     * @return Максимальный вместимый вес инвентаря
     */
    double getMaxWeight(IInventory inventory, Entity owner);

    /**
     * Синхронизирует данную систему веса с клиентом игрока.
     */
    void sync(EntityPlayerMP player);
}
