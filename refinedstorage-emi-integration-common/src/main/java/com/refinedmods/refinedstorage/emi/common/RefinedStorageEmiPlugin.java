package com.refinedmods.refinedstorage.emi.common;

import com.refinedmods.refinedstorage.common.content.Menus;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

@EmiEntrypoint
public class RefinedStorageEmiPlugin implements EmiPlugin {
    @Override
    public void register(final EmiRegistry registry) {
        registry.addGenericExclusionArea(new EmiExclusionAreaImpl());
        registry.addGenericStackProvider(new GridEmiStackProvider());
        registry.addGenericStackProvider(new ResourceEmiStackProvider());
        registry.addGenericDragDropHandler(new EmiDragDropHandlerImpl());
        registry.addRecipeHandler(Menus.INSTANCE.getCraftingGrid(), new EmiRecipeHandlerImpl());
    }
}
