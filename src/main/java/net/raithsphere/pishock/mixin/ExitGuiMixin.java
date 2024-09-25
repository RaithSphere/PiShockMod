package net.raithsphere.pishock.mixin;

import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public class ExitGuiMixin extends Screen{

    protected ExitGuiMixin(Component p_96550_) {
        super(p_96550_);
    }

    @Inject(at = @At(value = "TAIL"), method = "createPauseMenu", cancellable = true)
    private void createPauseMenu(CallbackInfo i) {

    }

}
