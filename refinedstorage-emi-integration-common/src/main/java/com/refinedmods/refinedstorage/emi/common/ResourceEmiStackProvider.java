package com.refinedmods.refinedstorage.emi.common;

import com.refinedmods.refinedstorage.platform.api.PlatformApi;
import com.refinedmods.refinedstorage.platform.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.platform.common.support.AbstractBaseScreen;

import dev.emi.emi.api.EmiStackProvider;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStackInteraction;
import net.minecraft.client.gui.screens.Screen;

class ResourceEmiStackProvider implements EmiStackProvider<Screen> {
    @Override
    public EmiStackInteraction getStackAt(final Screen screen, final int x, final int y) {
        if (!(screen instanceof AbstractBaseScreen<?> baseScreen)) {
            return EmiStackInteraction.EMPTY;
        }
        final PlatformResourceKey resource = baseScreen.getHoveredResource();
        if (resource == null) {
            return EmiStackInteraction.EMPTY;
        }
        return PlatformApi.INSTANCE.getIngredientConverter().convertToIngredient(resource).map(
            ingredient -> new EmiStackInteraction((EmiIngredient) ingredient, null, false)
        ).orElse(EmiStackInteraction.EMPTY);
    }
}

