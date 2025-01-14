package com.semenzhi.okt.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.semenzhi.okt.Connector;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity{

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "attack",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;push(DDD)V", shift = At.Shift.AFTER))
    public void attack(Entity target, CallbackInfo ci, @Local(ordinal = 5) float f4) {

        ((Connector) (target)).knockbackR(
                f4 * 0.5F,
                Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)),
                -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0))
        );
    }

    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 5)
    public boolean flag4m(boolean flag) {
        return true;
    }

}
