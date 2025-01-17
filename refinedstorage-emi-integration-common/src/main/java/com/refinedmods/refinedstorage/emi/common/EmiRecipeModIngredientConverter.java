package com.refinedmods.refinedstorage.emi.common;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.RecipeModIngredientConverter;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import java.util.Optional;

import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.FluidEmiStack;
import dev.emi.emi.api.stack.ItemEmiStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

class EmiRecipeModIngredientConverter implements RecipeModIngredientConverter {
    @SuppressWarnings("UnstableApiUsage")
    @Override
    public Optional<PlatformResourceKey> convertToResource(final Object ingredient) {
        if (ingredient instanceof FluidEmiStack fluid) {
            return Optional.of(new FluidResource((Fluid) fluid.getKey(), fluid.getComponentChanges()));
        }
        if (ingredient instanceof ItemEmiStack item) {
            return Optional.of(new ItemResource((Item) item.getKey(), item.getComponentChanges()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<ResourceAmount> convertToResourceAmount(final Object ingredient) {
        if (ingredient instanceof FluidEmiStack fluid) {
            return Optional.of(new ResourceAmount(
                new FluidResource((Fluid) fluid.getKey(), fluid.getComponentChanges()),
                fluid.getAmount()
            ));
        }
        if (ingredient instanceof ItemEmiStack item) {
            return Optional.of(new ResourceAmount(
                new ItemResource((Item) item.getKey(), item.getComponentChanges()),
                item.getAmount()
            ));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Object> convertToIngredient(final PlatformResourceKey resource) {
        if (resource instanceof ItemResource itemResource) {
            return Optional.of(EmiStack.of(itemResource.toItemStack()));
        }
        if (resource instanceof FluidResource fluidResource) {
            return Optional.of(EmiStack.of(fluidResource.fluid(), fluidResource.components()));
        }
        return Optional.empty();
    }
}
