package com.semenzhi.okt.mixin;

import com.semenzhi.okt.Connector;
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

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Predicate;


@Mixin(FallingBlockEntity.class)
abstract class FallingBlockEntityMixin extends Entity implements Connector {

    public FallingBlockEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    private int fallDamageMax;
    @Shadow
    private BlockState blockState;
    @Shadow
    private float fallDamagePerDistance;
    @Unique
    Predicate<Entity> onUrKneeEntity$predicate = EntitySelector.NO_CREATIVE_OR_SPECTATOR;
    @Unique
    DamageSource onUrKneeEntity$damagesource;
    @Unique
    private Level onUrKneeEntity$level;
    @Unique
    private final Set<Entity> onUrKneeEntity$harmedEntities = Collections.newSetFromMap(new WeakHashMap<>());

    @Override
    public void onUrKneeEntity$clearHarmedEntities() {
        onUrKneeEntity$harmedEntities.clear();
    }


    @Inject(method = "isAttackable", at = @At("HEAD"), cancellable = true)
    public void isAttackable(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/level/block/state/BlockState;)V", at = @At("TAIL"))
    public void init(Level level, double x, double y, double z, BlockState state, CallbackInfo ci) {
        fallDamageMax = Integer.MAX_VALUE;

        if (state == null || state.isAir()) {
            this.discard(); // 立即移除无效实体
            return;
        }

        onUrKneeEntity$level = level;

        if (state.getBlock() instanceof Fallable fallable) {
            onUrKneeEntity$damagesource = fallable.getFallDamageSource(this);
        } else {
            onUrKneeEntity$damagesource = this.damageSources().fallingBlock(this);
        }
    }

    @ModifyVariable(method = "setHurtsEntities", at = @At("HEAD"), index = 2, argsOnly = true)
    public int setHurtsEntities(int fallDamageMax) {
        return Integer.MAX_VALUE;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/FallingBlockEntity;onGround()Z", shift = At.Shift.AFTER))
    public void tick(CallbackInfo ci) {
        if (this.isRemoved()) return;
        if (this.blockState == null || this.blockState.isAir()) return;
        // 确保 damageSource 已初始化（兜底逻辑）
        if (onUrKneeEntity$damagesource == null) onUrKneeEntity$damagesource = this.damageSources().fallingBlock(this);

        onUrKneeEntity$level = ((EntityAccessors) this).getLevel();
        if (!onUrKneeEntity$level.isClientSide) {
            float f = (float) Math.min(Mth.floor(fallDistance * this.fallDamagePerDistance), this.fallDamageMax);

            onUrKneeEntity$level.getEntities(this, this.getBoundingBox(), onUrKneeEntity$predicate).forEach(entity -> {
                if (!onUrKneeEntity$harmedEntities.contains(entity)) {
                    entity.hurtServer((ServerLevel) onUrKneeEntity$level, onUrKneeEntity$damagesource, f);
                    onUrKneeEntity$harmedEntities.add(entity);
                }
            });
        }


    }


}