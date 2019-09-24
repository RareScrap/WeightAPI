package ru.rarescrap.examplemod2;

import cpw.mods.fml.common.network.ByteBufUtils;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import ru.rarescrap.simpleweightsystem.ConfigurableWeightProvider;
import ru.rarescrap.weightapi.WeightRegistry;
import ru.rarescrap.weightapi.event.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static ru.rarescrap.examplemod2.ExampleMod2.NETWORK;

public class NewConfigurableWeightProvider extends ConfigurableWeightProvider {
    public NewConfigurableWeightProvider(Map<Item, Double> weightStorage, double defaultWeight) {
        super(weightStorage, defaultWeight);
    }

    public NewConfigurableWeightProvider(File configFile) {
        super(configFile);
    }

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
        boolean isOverloaded =  getWeight(inventory, owner) > getMaxWeight(inventory, owner);

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
        if (owner instanceof EntityPlayer) maxWeight = 10;
        else maxWeight = inventory.getSizeInventory() * 64;

        CalculateMaxWeightEvent event = new CalculateMaxWeightEvent(inventory, maxWeight, owner);
        MinecraftForge.EVENT_BUS.post(event);
        return event.maxWeight;
    }

    @Override
    public void sync(EntityPlayerMP player) {
        NETWORK.sendTo(new NewSyncMessage(this), player);
    }

    public static class NewSyncMessage extends SyncMessage {
        private NewConfigurableWeightProvider weightProvider;

        public NewSyncMessage() {
        }

        public NewSyncMessage(ConfigurableWeightProvider serverWeightProvider) {
            super(serverWeightProvider);
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            Map<Item, Double> weightStorage = new HashMap<Item, Double>();
            int size = ByteBufUtils.readVarInt(buf, 1);
            for (int i = 0; i < size; i++) {
                Item item = Item.getItemById(ByteBufUtils.readVarShort(buf));
                double weight = buf.readDouble();
                weightStorage.put(item, weight);
            }
            double defaultWeight = buf.readDouble();
            weightProvider = new NewConfigurableWeightProvider(weightStorage, defaultWeight);
        }
    }

    public static class MessageHandler implements IMessageHandler<NewSyncMessage, IMessage> {

        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(NewSyncMessage message, MessageContext ctx) {
            WeightRegistry.applyToClient(message.weightProvider);
            return null;
        }
    }
}
