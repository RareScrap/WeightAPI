package ru.rarescrap.examplemod1;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import ru.rarescrap.weightapi.WeightRegistry;

public class Utils {
    public static int calculateAllowingStackSize(ItemStack itemStack, IInventory inventory, Entity owner, double limit) {
        ItemStack copy = itemStack.copy();
        for (; copy.stackSize > 0; copy.stackSize--) {
            double weight = WeightRegistry.getActiveWeightProvider().getWeight(copy, inventory, owner);
            if (weight <= limit) return copy.stackSize;
        }
        return 0;
    }

    public static void drawCenteredStringWithoutShadow(FontRenderer fontRenderer, String str, int x, int y, int color){
        GL11.glDisable(GL11.GL_LIGHTING); // TODO: Вообще хз что я сделал
        fontRenderer.drawString(str, x - fontRenderer.getStringWidth(str) / 2, y, color, false);
    }
}
