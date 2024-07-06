package com.refinedmods.refinedstorage.emi.common;

import com.refinedmods.refinedstorage.platform.api.PlatformApi;

import net.minecraft.resources.ResourceLocation;

public final class Common {
    public static final String MOD_ID = "refinedstorage_emi_integration";

    private Common() {
    }

    public static void init(final PlatformApi platformApi) {
        platformApi.addIngredientConverter(new EmiRecipeModIngredientConverter());
        platformApi.getGridSynchronizerRegistry().register(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "emi"),
            new EmiGridSynchronizer(false)
        );
        platformApi.getGridSynchronizerRegistry().register(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "emi_two_way"),
            new EmiGridSynchronizer(true)
        );
    }
}
