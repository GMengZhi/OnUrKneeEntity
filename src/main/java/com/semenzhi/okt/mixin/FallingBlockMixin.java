package com.semenzhi.okt.mixin;

import net.minecraft.world.entity.item.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(FallingBlockEntity.class)
class FallingBlockEntityMixin {

    @Inject(method = "isAttackable", at = @At("HEAD"), cancellable = true)
    public void isAttackable(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

}