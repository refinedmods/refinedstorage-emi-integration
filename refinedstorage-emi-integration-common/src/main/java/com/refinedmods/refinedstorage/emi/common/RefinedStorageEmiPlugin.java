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
        registry.addGenericDragDropHandler(new ResourceEmiDragDropHandler());
        registry.addGenericDragDropHandler(new FilterEmiDragDropHandler());
        registry.addRecipeHandler(Menus.INSTANCE.getCraftingGrid(), new CraftingGridEmiRecipeHandler<>());
        if (QuartzArsenalIntegration.isLoaded()) {
            QuartzArsenalIntegration.load(registry);
        }
        registry.addRecipeHandler(Menus.INSTANCE.getPatternGrid(), new PatternGridEmiRecipeHandler());
    }
}
