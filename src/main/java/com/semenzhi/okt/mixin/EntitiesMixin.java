package com.semenzhi.okt.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;


@Mixin(FallingBlockEntity.class)
abstract class FallingBlockEntityMixin extends Entity {

    public FallingBlockEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow private int fallDamageMax;
    @Shadow private BlockState blockState;
    @Shadow private float fallDamagePerDistance;
    @Unique boolean onUrKneeEntity$entityHit = false;
    @Unique Predicate<Entity> onUrKneeEntity$predicate = EntitySelector.NO_CREATIVE_OR_SPECTATOR;
    @Unique DamageSource onUrKneeEntity$damagesource = blockState.getBlock() instanceof Fallable fallable
            ? fallable.getFallDamageSource(this)
            : this.damageSources().fallingBlock(this);
    @Unique private Level onUrKneeEntity$level;

    @Inject(method = "isAttackable", at = @At("HEAD"), cancellable = true)
    public void isAttackable(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/level/block/state/BlockState;)V" , at = @At("TAIL"))
    public void init(Level level, double x, double y, double z, BlockState state, CallbackInfo ci) {
        fallDamageMax = Integer.MAX_VALUE;
    }

    @ModifyVariable(method = "setHurtsEntities", at = @At("HEAD"), index = 2, argsOnly = true)
    public int setHurtsEntities(int fallDamageMax) {
        return Integer.MAX_VALUE;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/FallingBlockEntity;onGround()Z", shift = At.Shift.AFTER))
    public void tick(CallbackInfo ci) {
        onUrKneeEntity$level = ((EntityAccessors) this).getLevel();
        float f = (float)Math.min(Mth.floor(fallDistance * this.fallDamagePerDistance), this.fallDamageMax);
        if (!onUrKneeEntity$entityHit && !onUrKneeEntity$level.isClientSide) {
            onUrKneeEntity$level.getEntities(this, this.getBoundingBox(), onUrKneeEntity$predicate).forEach(entity -> entity.hurtServer((ServerLevel) onUrKneeEntity$level ,onUrKneeEntity$damagesource, f));
        }

    }

}