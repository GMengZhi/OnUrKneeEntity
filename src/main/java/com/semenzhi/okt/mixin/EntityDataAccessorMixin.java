package com.semenzhi.okt.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.data.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/*
 * 让原版指令 /data 可以修改玩家数据
 */
@Mixin(EntityDataAccessor.class)
public class EntityDataAccessorMixin {

    @Unique
    private static final SimpleCommandExceptionType PLAYERS_ERROR = new SimpleCommandExceptionType(Component.translatable("commands.data.player.modify.error"));

    @Final
    @Shadow
    private Entity entity;

    @Inject(method = "setData", at = @At("HEAD"), cancellable = true)
    public void setData(CompoundTag other, CallbackInfo ci) throws CommandSyntaxException {
        if (this.entity instanceof Player) {
            try {
                UUID uuid = this.entity.getUUID();
                this.entity.load(other);
                this.entity.setUUID(uuid);
            } catch (Exception e) {
                throw PLAYERS_ERROR.create();
            }
            ci.cancel();
        }
    }

}
