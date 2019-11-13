package ru.rarescrap.examplemod1;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInputFromOptions;
import ru.rarescrap.weightapi.WeightRegistry;

/**
 * Клиентский обработчик ввода, отключающий движение при перегрузе
 */
public class FreezeMovementInput extends MovementInputFromOptions {

    private EntityPlayerSP player;

    public FreezeMovementInput(EntityPlayerSP entityPlayerSP, GameSettings profile) {
        super(profile);
        this.player = entityPlayerSP;
    }

    @Override
    public void updatePlayerMoveState() {
        if (!WeightRegistry.getActiveWeightProvider().isOverloaded(player.inventory, player)) {
            super.updatePlayerMoveState();
        } else {
            this.moveStrafe = 0.0F; // Обнуляем, дабы игрок не двигалсяпо инерации после перегруза
            this.moveForward = 0.0F;
        }
    }
}
