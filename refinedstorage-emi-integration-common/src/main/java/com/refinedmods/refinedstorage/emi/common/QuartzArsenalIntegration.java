package com.refinedmods.refinedstorage.emi.common;

import com.refinedmods.refinedstorage.quartzarsenal.common.Menus;

import dev.emi.emi.api.EmiRegistry;

public final class QuartzArsenalIntegration {
    private QuartzArsenalIntegration() {
    }

    public static boolean isLoaded() {
        try {
            Class.forName(
                "com.refinedmods.refinedstorage.quartzarsenal.common"
                    + ".Menus"
            );
            return true;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

    public static void load(final EmiRegistry registration) {
        registration.addRecipeHandler(
            Menus.INSTANCE.getWirelessCraftingGrid(),
            new CraftingGridEmiRecipeHandler<>()
        );
    }
}
