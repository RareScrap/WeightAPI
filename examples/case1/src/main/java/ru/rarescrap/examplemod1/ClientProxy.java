package ru.rarescrap.examplemod1;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.lwjgl.opengl.GL11;
import ru.rarescrap.weightapi.WeightRegistry;
import ru.rarescrap.weightapi.event.WeightProviderChangedEvent;

import static net.minecraftforge.client.GuiIngameForge.right_height;
import static ru.rarescrap.examplemod1.Utils.drawCenteredStringWithoutShadow;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    public static ResourceLocation WEIGHT_ICONS = new ResourceLocation(ExampleMod1.MODID, "textures/weight_overlay.png");
    public static RenderGameOverlayEvent.ElementType WEIGHT = EnumHelper.addEnum(RenderGameOverlayEvent.ElementType.class, "WEIGHT");
    public static FreezeMovementInput freezeMovementInput;

    // Инициализируем обработчик движения игрока на клиенте
    @SubscribeEvent
    public void onSetupPlayer(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityPlayerSP) {
            freezeMovementInput = new FreezeMovementInput((EntityPlayerSP) event.entity, Minecraft.getMinecraft().gameSettings);
        }
    }

    @SubscribeEvent
    public void onOverload(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().thePlayer != null // Игрок МОЖЕТ быть не инициализирован во время клиентского тика
                && Minecraft.getMinecraft().thePlayer.movementInput != freezeMovementInput // TODO: instanceof?
                && WeightRegistry.getActiveWeightProvider() instanceof WeightProvider) { // Поддержка маштабируемости провайдера за счет наслеников
            Minecraft.getMinecraft().thePlayer.movementInput = freezeMovementInput;
        }
    }

    // Восстанавливаем клиентский обработчик движения, если наш провайдер дективирован
    // TODO: Сохрать предыдущий обработчик вместо инициализации нового?
    @SubscribeEvent
    public void onProviderChanged(WeightProviderChangedEvent.Pre event) {
        if (/*event.world.isRemote
                &&*/ event.deactivatedProvider != null // Чтобы не срабатыва на клиенте, при входе в мир в котором из WorldData достали не WeightProvider
                && Minecraft.getMinecraft().thePlayer != null
                && !(event.currentProvider instanceof WeightProvider)) {
            Minecraft.getMinecraft().thePlayer.movementInput = new MovementInputFromOptions(Minecraft.getMinecraft().gameSettings);
        }
    }

    // Рендер инфы о весе в инвентаре (обычном и креативном)
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
    @SubscribeEvent
    public void renderOverloadMessage(RenderGameOverlayEvent.Text event) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        if (WeightRegistry.getActiveWeightProvider() instanceof WeightProvider
                && WeightRegistry.getActiveWeightProvider().isOverloaded(player.inventory, player)) {
            Minecraft.getMinecraft().fontRenderer.drawString("Вы перегружены и не можете двигаться!",
                    10, 10, 0xDB1818);
        }
    }

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
        if (pre(WEIGHT, eventParent)) return;
        Minecraft mc = Minecraft.getMinecraft();
        mc.mcProfiler.startSection("weight");

        GL11.glEnable(GL11.GL_BLEND);
        int left = width / 2 + 91 - 9;
        int top = height - right_height;
        right_height += 10;

        int level = (int) (WeightRegistry.getActiveWeightProvider().getWeight(Minecraft.getMinecraft().thePlayer.inventory, Minecraft.getMinecraft().thePlayer) / 1);
        bind(WEIGHT_ICONS, mc);

        for (int i = 1; level > 0 && i < 20; i += 2)
        {
            if (WeightRegistry.getActiveWeightProvider().isOverloaded(Minecraft.getMinecraft().thePlayer.inventory, Minecraft.getMinecraft().thePlayer))  {
                // Рендерим без скейла
                Gui.func_146110_a(left, top, 27, 0, 9, 9, 36, 9);
            } else if (i < level) {
                Gui.func_146110_a(left, top, 18, 0, 9, 9, 36, 9);
            }
            else if (i == level) {
                Gui.func_146110_a(left, top, 9, 0, 9, 9, 36, 9);
            }
            else if (i > level)
            {
                Gui.func_146110_a(left, top, 0, 0, 9, 9, 36, 9);
            }
            left -= 8;
        }

        GL11.glDisable(GL11.GL_BLEND);
        mc.mcProfiler.endSection();
        post(WEIGHT, eventParent);
    }

    //Helper macros from GuiIngameForge
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
}
