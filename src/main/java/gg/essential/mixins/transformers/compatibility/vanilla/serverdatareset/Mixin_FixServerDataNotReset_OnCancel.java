/*
 * Copyright (c) 2024 ModCore Inc. All rights reserved.
 *
 * This code is part of ModCore Inc.'s Essential Mod repository and is protected
 * under copyright registration # TX0009138511. For the full license, see:
 * https://github.com/EssentialGG/Essential/blob/main/LICENSE
 *
 * You may not use, copy, reproduce, modify, sell, license, distribute,
 * commercialize, or otherwise exploit, or create derivative works based
 * upon, this file or any other in this repository, all of which is reserved by Essential.
 */
package gg.essential.mixins.transformers.compatibility.vanilla.serverdatareset;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.GuiConnecting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @see gg.essential.mixins.transformers.compatibility.vanilla.serverdatareset
 */
@Mixin(GuiConnecting.class)
public abstract class Mixin_FixServerDataNotReset_OnCancel {

    //#if MC==11202
    @SuppressWarnings("ConstantConditions")  // Method inappropriately marked as non-null by Forge
    //#endif
    @Inject(
        //#if FABRIC
        //$$ method = "method_19800",
        //#elseif MC>=11400
        //$$ method = {"func_212999_a", "m_95704_"},
        //#else
        method = "actionPerformed",
        //#endif
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"))
    private void resetServerData(CallbackInfo ci) {
        Minecraft.getMinecraft().setServerData(null);
    }

}