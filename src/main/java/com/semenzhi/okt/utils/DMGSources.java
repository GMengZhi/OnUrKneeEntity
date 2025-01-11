package com.semenzhi.okt.utils;

import com.semenzhi.okt.OKT;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class DMGSources {

    public static final ResourceKey<DamageType> APPLE_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(OKT.MODID, "poisoned_by_apple"));

}
