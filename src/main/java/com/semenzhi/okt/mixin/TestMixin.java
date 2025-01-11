package com.semenzhi.okt.mixin;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.semenzhi.okt.utils.DMGSources.APPLE_DAMAGE;

@Mixin(Item.class)
public class TestMixin {

    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    public void finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir){

        Player player = (Player) livingEntity;

        DamageSource damageSource = new DamageSource(
                level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(APPLE_DAMAGE), null, player, null
        );

        if(player.getMainHandItem().getItem() == Items.APPLE) {
            if(!level.isClientSide()) {

                player.hurtServer((ServerLevel) level,damageSource,Float.MAX_VALUE);
                //player.kill((ServerLevel) level);
                //player.die(damageSource);
                player.displayClientMessage(Component.literal("You have poisoned by An Apple. You asshole"),false);

            }
        }
    }
}
