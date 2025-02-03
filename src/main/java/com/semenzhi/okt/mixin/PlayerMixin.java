package com.semenzhi.okt.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.semenzhi.okt.Connector;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    @Shadow
    public abstract void displayClientMessage(Component chatComponent, boolean actionBar);

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @ModifyVariable(method = "attack", at = @At("STORE"), index = 17)
    public boolean flag4m(boolean flag) {
        return true;
    }

    @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getKnockback(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;)F"))
    public float attack$hands(float original) {
        return (original == 0 ? original + 0.5F : original);

    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;push(DDD)V", shift = At.Shift.AFTER))
    public void attack$knockback(Entity target, CallbackInfo ci,
                                 @Local(index = 18) float knockBack,
                                 @Local(index = 9) boolean isCritical,
                                 @Local(index = 7) boolean isStrong,
                                 @Local(index = 12) boolean isSweep,
                                 @Local(index = 11) float calculatedDamage) {


        float calculatedFallingDistance = target.fallDistance;

        calculatedFallingDistance = (float) Math.abs(Math.sqrt(calculatedFallingDistance * calculatedFallingDistance + knockBack * knockBack / 4));

        target.fallDistance = calculatedFallingDistance;

        float knockbackMultiplier = isCritical ? 4.0F : (isStrong ? 2.0F : 0.5F);

        ((Connector) (target)).knockbackR(
                knockBack == 0F ? knockbackMultiplier * 0.5F : (knockBack * knockbackMultiplier * 0.5F),
                Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)),
                Mth.sin(this.getXRot() * (float) (Math.PI / 180.0)),
                -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0))
        );

        displayClientMessage(Component.literal(String.valueOf(calculatedFallingDistance)), true);

    }

}
