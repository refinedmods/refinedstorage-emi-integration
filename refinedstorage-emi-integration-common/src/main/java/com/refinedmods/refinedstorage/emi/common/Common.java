package com.refinedmods.refinedstorage.emi.common;

import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;

import net.minecraft.resources.ResourceLocation;

public final class Common {
    public static final String MOD_ID = "refinedstorage_emi_integration";

    private Common() {
    }

    public static void init(final RefinedStorageApi api) {
        api.addIngredientConverter(new EmiRecipeModIngredientConverter());
        api.getGridSynchronizerRegistry().register(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "emi"),
            new EmiGridSynchronizer(false)
        );
        api.getGridSynchronizerRegistry().register(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "emi_two_way"),
            new EmiGridSynchronizer(true)
        );
    }
}
