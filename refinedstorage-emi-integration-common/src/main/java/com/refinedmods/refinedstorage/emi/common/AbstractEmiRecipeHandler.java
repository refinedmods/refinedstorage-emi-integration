package com.refinedmods.refinedstorage.emi.common;

import com.refinedmods.refinedstorage.common.grid.AutocraftableResourceHint;

import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

abstract class AbstractEmiRecipeHandler<T extends AbstractContainerMenu> implements EmiRecipeHandler<T> {
    protected static final int AUTOCRAFTABLE_COLOR = AutocraftableResourceHint.AUTOCRAFTABLE.getColor();

    protected static ClientTooltipComponent createAutocraftableHint(final Component component) {
        return ClientTooltipComponent.create(component.copy().withColor(AUTOCRAFTABLE_COLOR).getVisualOrderText());
    }
}
