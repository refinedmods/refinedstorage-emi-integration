package com.refinedmods.refinedstorage.emi.common;

import com.refinedmods.refinedstorage.platform.api.PlatformApi;
import com.refinedmods.refinedstorage.platform.api.grid.view.PlatformGridResource;
import com.refinedmods.refinedstorage.platform.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.platform.common.grid.screen.AbstractGridScreen;

import dev.emi.emi.api.EmiStackProvider;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStackInteraction;
import net.minecraft.client.gui.screens.Screen;

class GridEmiStackProvider implements EmiStackProvider<Screen> {
    @Override
    public EmiStackInteraction getStackAt(final Screen screen, final int x, final int y) {
        if (!(screen instanceof AbstractGridScreen<?> gridScreen)) {
            return EmiStackInteraction.EMPTY;
        }
        final PlatformGridResource resource = gridScreen.getCurrentGridResource();
        if (resource == null) {
            return EmiStackInteraction.EMPTY;
        }
        final PlatformResourceKey underlyingResource = resource.getUnderlyingResource();
        if (underlyingResource == null) {
            return EmiStackInteraction.EMPTY;
        }
        return PlatformApi.INSTANCE.getIngredientConverter().convertToIngredient(underlyingResource).map(
            ingredient -> new EmiStackInteraction((EmiIngredient) ingredient, null, false)
        ).orElse(EmiStackInteraction.EMPTY);
    }
}

