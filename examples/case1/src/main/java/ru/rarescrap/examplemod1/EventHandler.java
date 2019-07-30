package ru.rarescrap.examplemod1;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import ru.rarescrap.weightapi.WeightRegistry;
import ru.rarescrap.weightapi.event.WeightProviderChangedEvent;

import static ru.rarescrap.examplemod1.Utils.calculateAllowingStackSize;
import static ru.rarescrap.examplemod1.Utils.drawCenteredStringWithoutShadow;


public class EventHandler {

    @SideOnly(Side.CLIENT)
    public static ResourceLocation WEIGHT_ICONS = new ResourceLocation(ExampleMod1.MODID, "textures/weight_overlay.png");

    @SideOnly(Side.CLIENT)
    public static FreezeMovementInput freezeMovementInput;

    // Инициализируем обработчик движения игрока на клиенте
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onSetupPlayer(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityPlayerSP) {
            freezeMovementInput = new FreezeMovementInput((EntityPlayerSP) event.entity, Minecraft.getMinecraft().gameSettings);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onOverload(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().thePlayer != null // Игрок МОЖЕТ быть не инициализирован во время клиентского тика
                && Minecraft.getMinecraft().thePlayer.movementInput != freezeMovementInput // TODO: instanceof?
                && WeightRegistry.getActiveWeightProvider() instanceof WeightProvider) { // Поддержка маштабируемость провайдера за счет наслеников
            Minecraft.getMinecraft().thePlayer.movementInput = freezeMovementInput;
        }
    }

    // Восстанавливаем клиентский обработчик движения, если наш провайдер дективирован
    // TODO: Сохрать предыдущий обработчик вместо инициализации нового?
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onProviderChanged(WeightProviderChangedEvent.Pre event) {
        if (/*event.world.isRemote
                &&*/ event.deactivatedProvider != null // Чтобы не срабатыва на клиенте, при входе в мир в котором из WorldData достали не WeightProvider
                && Minecraft.getMinecraft().thePlayer != null
                && !(event.currentProvider instanceof WeightProvider)) {
            Minecraft.getMinecraft().thePlayer.movementInput = new MovementInputFromOptions(Minecraft.getMinecraft().gameSettings);
        }
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

    // Рендер инфы о весе в инвентаре (обычном и креативном)
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void renderWeight(GuiScreenEvent.DrawScreenEvent event) {
        if (!(WeightRegistry.getActiveWeightProvider() instanceof WeightProvider) // TODO: Отключаемый в конфиге рендер. Раз другие моды могут предоставлять собственный рендер веса, то каждый следует делать отключаемым. Боюсь, это самый лучший выход.
                || !(event.gui instanceof InventoryEffectRenderer)) return;

        InventoryEffectRenderer guiInventory = (InventoryEffectRenderer) event.gui;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        double currentWeight = WeightRegistry.getActiveWeightProvider().getWeight(player.inventory, player);
        double maxWeight = WeightRegistry.getActiveWeightProvider().getMaxWeight(player.inventory, player);
        boolean isOverloaded = WeightRegistry.getActiveWeightProvider().isOverloaded(player.inventory, player);

        String str = StatCollector.translateToLocalFormatted("gui.inventory_weight", currentWeight, maxWeight);
        int color = isOverloaded ? 0xDB1818 : 4210752;

        // Рендерим строку веса в инвентаре
        if (guiInventory instanceof GuiInventory)
            drawCenteredStringWithoutShadow(Minecraft.getMinecraft().fontRenderer, str, guiInventory.guiLeft + 125, guiInventory.guiTop + 70, color);
            // В креативе вес показывается только на вкладке с инвентарем (11-ая вкладка)
        else if (guiInventory instanceof GuiContainerCreative && ((GuiContainerCreative) guiInventory).func_147056_g() == 11) {
            drawCenteredStringWithoutShadow(Minecraft.getMinecraft().fontRenderer, str, guiInventory.guiLeft + 125, guiInventory.guiTop + 40, color);
        }
    }

    // Вывод сообщения о перегрузе в левом верхнем углу (как в TES)
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void renderOverloadMessage(RenderGameOverlayEvent.Text event) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        if (WeightRegistry.getActiveWeightProvider() instanceof WeightProvider
                && WeightRegistry.getActiveWeightProvider().isOverloaded(player.inventory, player)) {
            Minecraft.getMinecraft().fontRenderer.drawString("Вы перегружены и не можете двигаться!",
                    10, 10, 0xDB1818);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void renderWeightOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        if (WeightRegistry.getActiveWeightProvider() instanceof WeightProvider) {
            ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            renderWeight(res.getScaledWidth(), res.getScaledHeight(), event);
        }
    }

    protected void renderWeight(int width, int height, RenderGameOverlayEvent eventParent)
    {
        //if (pre(DebugMod.WEIGHT, eventParent)) return;
        // my code start
        Minecraft mc = Minecraft.getMinecraft();
        //bind(DebugMod.WEIGHT_ICONS, mc);
        // my code end

        //mc.mcProfiler.startSection("weight");

        //GL11.glEnable(GL11.GL_BLEND);
        int left = 100;//width / 2 - 91;
        int top = 100;//height - left_height;

        //int level = ForgeHooks.getTotalArmorValue(mc.thePlayer);
        bind(WEIGHT_ICONS, mc);
        //bind(icons, mc);

        //mc.ingameGUI.drawTexturedModalRect(left, top, 34, 9, 9, 9);
        mc.ingameGUI.drawTexturedModalRect(left, top, 0, 0, 9, 9);
//        for (int i = 1; level > 0 && i < 20; i += 2)
//        {
//            if (i < level)
//            {
//                mc.ingameGUI.drawTexturedModalRect(left, top, 0/*34*/, 0, 9, 9);
//            }
//            else if (i == level)
//            {
//                mc.ingameGUI.drawTexturedModalRect(left, top, 9/*25*/, 0, 9, 9);
//            }
//            else if (i > level)
//            {
//                mc.ingameGUI.drawTexturedModalRect(left, top, 18/*16*/, 0, 9, 9);
//            }
//            left += 20;//8;
//        }
        //left_height += 10;

        //GL11.glDisable(GL11.GL_BLEND);
        //mc.mcProfiler.endSection();
        //post(DebugMod.WEIGHT, eventParent);
    }

    //Helper macros
    private boolean pre(RenderGameOverlayEvent.ElementType type, RenderGameOverlayEvent eventParent)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(eventParent, type));
    }
    private void post(RenderGameOverlayEvent.ElementType type, RenderGameOverlayEvent eventParent)
    {
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(eventParent, type));
    }
    private void bind(ResourceLocation res, Minecraft mc)
    {
        mc.getTextureManager().bindTexture(res);
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
