package ru.rarescrap.examplemod1;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import ru.rarescrap.weightapi.WeightRegistry;

import static ru.rarescrap.examplemod1.Utils.calculateAllowingStackSize;


// Прокси выступает еще и как евент хандлер чтобы не плодит лишние классы
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    // После достижения предела веса игрок не может подбирать предметы
    @SubscribeEvent
    public void onPickupItem(EntityItemPickupEvent event) {
        if (WeightRegistry.getActiveWeightProvider() instanceof WeightProvider) {
            EntityPlayer player = event.entityPlayer;
            boolean isOverloaded = WeightRegistry.getActiveWeightProvider().isOverloaded(player.inventory, player);

            if (isOverloaded) event.setCanceled(true);
            else {
                ItemStack itemStack = event.item.getEntityItem();
                double freeSpace = WeightRegistry.getActiveWeightProvider().getFreeSpace(player.inventory, player);
                int takenItems = calculateAllowingStackSize(itemStack, player.inventory, player, freeSpace);
                if (takenItems > 0) {
                    itemStack.stackSize -= takenItems;
                    player.inventory.addItemStackToInventory(
                            new ItemStack(itemStack.getItem(), takenItems, itemStack.getItemDamage()));
                }
                event.setCanceled(true);
            }
        }
    }


//    // Высылаем клиенту таблицу весов, если тот подключился
//    @SubscribeEvent
//    public void onClientConectToServer(PlayerEvent.PlayerLoggedInEvent event) {
//        if (!event.player.worldObj.isRemote && WeightRegistry.getActiveWeightProvider() instanceof WeightProvider)
//            // Ничего страшного, что в сингле пошлется пакет, который по факту ничего не изменит.
//            // Стоимоть пакета в сингле почти нулевая.
//            NETWORK.sendTo(new WeightProvider.SyncMessage(), (EntityPlayerMP) event.player);
//    }

    // Для безопасности, т.к. клиент может хаками сменить MovementInput
    @SubscribeEvent
    public void onServerPlayerOverload(TickEvent.PlayerTickEvent event) {
        if (!event.player.worldObj.isRemote
                && WeightRegistry.getActiveWeightProvider() instanceof WeightProvider
                && WeightRegistry.getActiveWeightProvider().isOverloaded(event.player.inventory, event.player)) { // Это тест. Так что пофиг на производительнось
            ((EntityPlayerMP) event.player).setEntityActionState(0, 0, false, false);
        }
    }
}
