package com.semenzhi.okt.mixin;

import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(FallingBlockEntity.class)
class FallingBlockEntityMixin {

    @Shadow
    private int fallDamageMax;

    @Inject(method = "isAttackable", at = @At("HEAD"), cancellable = true)
    public void isAttackable(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/level/block/state/BlockState;)V" , at = @At("TAIL"))
    public void init(Level level, double x, double y, double z, BlockState state, CallbackInfo ci) {
        fallDamageMax = Integer.MAX_VALUE;
    }

    @ModifyVariable(method = "setHurtsEntities", at = @At("HEAD"), ordinal = 1)
    public int setHurtsEntities(int fallDamageMax) {
        return Integer.MAX_VALUE;
    }


}