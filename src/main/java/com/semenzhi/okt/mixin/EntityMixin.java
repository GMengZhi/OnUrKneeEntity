package com.semenzhi.okt.mixin;

import com.semenzhi.okt.Connector;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin implements Connector {

    @Shadow
    private Vec3 deltaMovement = Vec3.ZERO;

    @Shadow
    public Vec3 getDeltaMovement() {
        return this.deltaMovement;
    }

    @Shadow
    public void setDeltaMovement(Vec3 deltaMovement) {
        this.deltaMovement = deltaMovement;
    }

    @Shadow
    public void setDeltaMovement(double x, double y, double z) {
        this.setDeltaMovement(new Vec3(x, y, z));
    }

    @Inject(method = "isAttackable", at = @At("HEAD"), cancellable = true)
    public void isAttackable(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(method = "skipAttackInteraction", at = @At("HEAD"), cancellable = true)
    public void skipAttackInteraction(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "isRemoved", at = @At("HEAD"), cancellable = true)
    public void isRemoved(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }


    @Unique
    public void knockbackR(double strength, double x, double z) {
        if (!(strength <= 0.0)) {
            Vec3 vec3 = this.getDeltaMovement();

            while (x * x + z * z < 1.0E-5F) {
                x = (Math.random() - Math.random()) * 0.01;
                z = (Math.random() - Math.random()) * 0.01;
            }

            Vec3 vec31 = new Vec3(x, 0.0, z).normalize().scale(strength);
            this.setDeltaMovement(vec3.x / 2.0 - vec31.x, vec3.y, vec3.z / 2.0 - vec31.z);
        }
    }

}



















