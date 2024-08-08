package com.refinedmods.refinedstorage.emi.common;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.autocrafting.PatternGridContainerMenu;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import java.util.List;
import java.util.stream.Collectors;

import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

class PatternGridEmiRecipeHandler implements EmiRecipeHandler<PatternGridContainerMenu> {
    @Override
    public EmiPlayerInventory getInventory(final AbstractContainerScreen<PatternGridContainerMenu> screen) {
        return new EmiPlayerInventory(List.of());
    }

    @Override
    public boolean supportsRecipe(final EmiRecipe recipe) {
        return true;
    }

    @Override
    public boolean canCraft(final EmiRecipe recipe, final EmiCraftContext<PatternGridContainerMenu> context) {
        return true;
    }

    @Override
    public boolean craft(final EmiRecipe recipe, final EmiCraftContext<PatternGridContainerMenu> context) {
        if (recipe.getCategory() == VanillaEmiRecipeCategories.CRAFTING) {
            transferCraftingRecipe(recipe, context);
        } else if (recipe.getCategory() == VanillaEmiRecipeCategories.STONECUTTING) {
            transferStonecutterRecipe(recipe, context);
        } else if (recipe.getCategory() == VanillaEmiRecipeCategories.SMITHING) {
            transferSmithingTableRecipe(recipe, context);
        } else {
            transferProcessingRecipe(recipe, context);
        }
        return true;
    }

    private void transferCraftingRecipe(final EmiRecipe recipe,
                                        final EmiCraftContext<PatternGridContainerMenu> context) {
        final List<List<ItemResource>> inputs = recipe.getInputs()
            .stream()
            .map(this::getItems)
            .toList();
        context.getScreenHandler().transferCraftingRecipe(inputs);
    }

    private void transferStonecutterRecipe(final EmiRecipe recipe,
                                           final EmiCraftContext<PatternGridContainerMenu> context) {
        final List<List<ItemResource>> inputs = recipe.getInputs()
            .stream()
            .map(this::getItems)
            .toList();
        final List<List<ItemResource>> outputs = recipe.getOutputs()
            .stream()
            .map(this::getItems)
            .toList();
        if (!inputs.isEmpty() && !outputs.isEmpty() && !inputs.getFirst().isEmpty() && !outputs.getFirst().isEmpty()) {
            context.getScreenHandler().transferStonecutterRecipe(
                inputs.getFirst().getFirst(),
                outputs.getFirst().getFirst()
            );
        }
    }

    private void transferSmithingTableRecipe(final EmiRecipe recipe,
                                             final EmiCraftContext<PatternGridContainerMenu> context) {
        final List<List<ItemResource>> inputs = recipe.getInputs()
            .stream()
            .map(this::getItems)
            .toList();
        if (inputs.size() == 3) {
            context.getScreenHandler().transferSmithingTableRecipe(
                inputs.getFirst(),
                inputs.get(1),
                inputs.get(2)
            );
        }
    }

    private List<ItemResource> getItems(final EmiIngredient ingredient) {
        return ingredient.getEmiStacks()
            .stream()
            .map(EmiStack::getItemStack)
            .filter(stack -> !stack.isEmpty())
            .map(ItemResource::ofItemStack)
            .collect(Collectors.toList());
    }

    private void transferProcessingRecipe(final EmiRecipe recipe,
                                          final EmiCraftContext<PatternGridContainerMenu> context) {
        final List<List<ResourceAmount>> inputs = recipe.getInputs()
            .stream()
            .map(this::getResources)
            .toList();
        final List<List<ResourceAmount>> outputs = recipe.getOutputs()
            .stream()
            .map(this::getResources)
            .toList();
        context.getScreenHandler().transferProcessingRecipe(inputs, outputs);
    }


    private List<ResourceAmount> getResources(final EmiIngredient ingredient) {
        return ingredient.getEmiStacks()
            .stream()
            .flatMap(emiStack -> RefinedStorageApi.INSTANCE.getIngredientConverter().convertToResourceAmount(emiStack)
                .stream())
            .collect(Collectors.toList());
    }
}
