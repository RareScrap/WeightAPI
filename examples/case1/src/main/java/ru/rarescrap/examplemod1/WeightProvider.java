package ru.rarescrap.examplemod1;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import ru.rarescrap.weightapi.IWeightProvider;
import ru.rarescrap.weightapi.WeightRegistry;
import ru.rarescrap.weightapi.event.*;

import static ru.rarescrap.examplemod1.ExampleMod1.NETWORK;

public class WeightProvider implements IWeightProvider {
    @Override
    public double getWeight(ItemStack itemStack, IInventory inventory, Entity owner) {
        double weight = itemStack == null ? 0 : itemStack.stackSize;

        CalculateStackWeightEvent event = new CalculateStackWeightEvent(itemStack, inventory, weight, owner);
        MinecraftForge.EVENT_BUS.post(event);
        return event.weight;
    }

    @Override
    public double getWeight(IInventory inventory, Entity owner) {
        double inventoryWeight = 0;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            inventoryWeight += getWeight(inventory.getStackInSlot(i), inventory, owner);
        }

        CalculateInventoryWeightEvent event = new CalculateInventoryWeightEvent(inventory, inventoryWeight, owner);
        MinecraftForge.EVENT_BUS.post(event);
        return event.weight;
    }

    @Override
    public boolean isOverloaded(IInventory inventory, Entity owner) {
        boolean isOverloaded = getWeight(inventory, owner) > getMaxWeight(inventory, owner);

        CalculateOverloadEvent event = new CalculateOverloadEvent(inventory, isOverloaded, owner);
        MinecraftForge.EVENT_BUS.post(event);
        return event.isOverload;
    }

    @Override
    public double getFreeSpace(IInventory inventory, Entity owner) {
        double freeSpace = getMaxWeight(inventory, owner) - getWeight(inventory, owner);

        CalculateFreeSpaceEvent event = new CalculateFreeSpaceEvent(inventory, freeSpace, owner);
        MinecraftForge.EVENT_BUS.post(event);
        return event.freeSpace;
    }

    @Override
    public double getMaxWeight(IInventory inventory, Entity owner) {
        double maxWeight;
        if (owner instanceof EntityPlayer) maxWeight = 20;
        else maxWeight = inventory.getSizeInventory() * 64;

        CalculateMaxWeightEvent event = new CalculateMaxWeightEvent(inventory, maxWeight, owner);
        MinecraftForge.EVENT_BUS.post(event);
        return event.maxWeight;
    }

    @Override
    public void sync(EntityPlayerMP player) {
        NETWORK.sendTo(new SyncMessage(), player);
    }

    public static class SyncMessage implements IMessage {

        public SyncMessage() {} // for reflection newInstance()

        @Override
        public void fromBytes(ByteBuf buf) {}

        @Override
        public void toBytes(ByteBuf buf) {}
    }

    public static class MessageHandler implements IMessageHandler<SyncMessage, IMessage> {

        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(WeightProvider.SyncMessage message, MessageContext ctx) {
//            if (WeightRegistry.getWeightProvider() == null ||
//                    WeightRegistry.getWeightProvider() != ConfigurableWeight.configurableWeightProvider) // Эта проверку нужна, когда игрок в сингле
            //WeightRegistry.registerWeightProvider("WeightProvider", new WeightProvider()); // Нет нужды переслать данные на клиент, так что мы просто зарегаем аналогичную систему веса, которая регистрировалась при старте игры на клиенте
            WeightRegistry.activateWeightProvider("WeightProvider", Minecraft.getMinecraft().theWorld);

            return null;
        }
    }

}
