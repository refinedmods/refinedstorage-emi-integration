package com.refinedmods.refinedstorage.emi.common;

import java.util.Optional;

import com.refinedmods.refinedstorage2.platform.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage2.platform.api.support.resource.RecipeModIngredientConverter;
import com.refinedmods.refinedstorage2.platform.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage2.platform.common.support.resource.ItemResource;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.FluidEmiStack;
import dev.emi.emi.api.stack.ItemEmiStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

class EmiRecipeModIngredientConverter implements RecipeModIngredientConverter {
    @Override
    public Optional<PlatformResourceKey> convertToResource(final Object ingredient) {
        if (ingredient instanceof FluidEmiStack fluid) {
            return Optional.of(new FluidResource((Fluid) fluid.getKey(), fluid.getNbt()));
        }
        if (ingredient instanceof ItemEmiStack item) {
            return Optional.of(new ItemResource((Item) item.getKey(), item.getNbt()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Object> convertToIngredient(final PlatformResourceKey resource) {
        if (resource instanceof ItemResource itemResource) {
            return Optional.of(EmiStack.of(itemResource.toItemStack()));
        }
        if (resource instanceof FluidResource fluidResource) {
            return Optional.of(EmiStack.of(fluidResource.fluid(), fluidResource.tag()));
        }
        return Optional.empty();
    }
}
