package com.semenzhi.okt.mixin;

import com.semenzhi.okt.Connector;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.SweepAttackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity{

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "attack",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;push(DDD)V", shift = At.Shift.AFTER), locals = LocalCapture.PRINT)
    public void attack(Entity target, CallbackInfo ci, float f, ItemStack itemstack, DamageSource damagesource, float f1, float f2, boolean flag3, boolean flag, boolean flag1, CriticalHitEvent critEvent, float f3, boolean flag2, boolean critBlocksSweep, SweepAttackEvent sweepEvent, float f6, Vec3 vec3, boolean flag4, float f4, LivingEntity livingentity1) {

        ((Connector) (target)).knockbackR(
                (double)(f4 * 0.5F),
                (double) Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)),
                (double)(-Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)))
        );
    }

    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 5)
    public boolean flag4m(boolean flag) {
        return true;
    }

}
