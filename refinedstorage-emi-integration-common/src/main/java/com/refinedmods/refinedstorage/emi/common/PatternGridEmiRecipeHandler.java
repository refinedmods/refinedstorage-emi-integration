package com.refinedmods.refinedstorage.emi.common;

import com.refinedmods.refinedstorage.api.grid.view.GridView;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridContainerMenu;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.runtime.EmiDrawContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

import static com.refinedmods.refinedstorage.emi.common.Common.MOD_ID;

class PatternGridEmiRecipeHandler extends AbstractEmiRecipeHandler<PatternGridContainerMenu> {
    private static final List<ClientTooltipComponent> ALL_AUTOCRAFTABLE_TOOLTIP = List.of(
        createAutocraftableHint(Component.translatable("gui.%s.transfer.all_autocraftable".formatted(MOD_ID)))
    );
    private static final List<ClientTooltipComponent> SOME_AUTOCRAFTABLE_TOOLTIP = List.of(
        createAutocraftableHint(Component.translatable("gui.%s.transfer.some_autocraftable".formatted(MOD_ID)))
    );

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
            .map(PatternGridEmiRecipeHandler::getResourceAmounts)
            .toList();
        final List<List<ResourceAmount>> outputs = recipe.getOutputs()
            .stream()
            .map(PatternGridEmiRecipeHandler::getResourceAmounts)
            .toList();
        context.getScreenHandler().transferProcessingRecipe(inputs, outputs);
    }

    @Override
    public void render(final EmiRecipe recipe,
                       final EmiCraftContext<PatternGridContainerMenu> craftContext,
                       final List<Widget> widgets,
                       final GuiGraphics draw) {
        final EmiDrawContext context = EmiDrawContext.wrap(draw);
        RenderSystem.enableDepthTest();
        final GridView view = craftContext.getScreenHandler().getView();
        for (final Widget widget : widgets) {
            if (!(widget instanceof SlotWidget slotWidget)) {
                continue;
            }
            final EmiIngredient stack = slotWidget.getStack();
            final Bounds bounds = slotWidget.getBounds();
            if (slotWidget.getRecipe() == null && !stack.isEmpty()) {
                final boolean autocraftable = getResourceAmounts(stack)
                    .stream()
                    .anyMatch(resourceAmount -> view.isAutocraftable(resourceAmount.resource()));
                if (autocraftable) {
                    context.fill(bounds.x(), bounds.y(), bounds.width(), bounds.height(), AUTOCRAFTABLE_COLOR);
                }
            }
        }
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(final EmiRecipe recipe,
                                                   final EmiCraftContext<PatternGridContainerMenu> context) {
        final GridView view = context.getScreenHandler().getView();
        final List<List<ResourceKey>> inputs = recipe.getInputs()
            .stream()
            .filter(input -> !input.isEmpty())
            .map(PatternGridEmiRecipeHandler::getResources)
            .toList();
        final boolean allAutocraftable = inputs.stream()
            .allMatch(possibilities -> possibilities.stream().anyMatch(view::isAutocraftable));
        if (allAutocraftable) {
            return ALL_AUTOCRAFTABLE_TOOLTIP;
        }
        final boolean someAutocraftable = inputs.stream()
            .anyMatch(possibilities -> possibilities.stream().anyMatch(view::isAutocraftable));
        if (someAutocraftable) {
            return SOME_AUTOCRAFTABLE_TOOLTIP;
        }
        return Collections.emptyList();
    }

    private static List<ResourceAmount> getResourceAmounts(final EmiIngredient ingredient) {
        return ingredient.getEmiStacks()
            .stream()
            .flatMap(emiStack -> RefinedStorageApi.INSTANCE.getIngredientConverter().convertToResourceAmount(emiStack)
                .stream())
            .collect(Collectors.toList());
    }

    private static List<ResourceKey> getResources(final EmiIngredient ingredient) {
        return ingredient.getEmiStacks()
            .stream()
            .flatMap(emiStack -> RefinedStorageApi.INSTANCE.getIngredientConverter().convertToResourceAmount(emiStack)
                .stream())
            .map(ResourceAmount::resource)
            .collect(Collectors.toList());
    }
}
