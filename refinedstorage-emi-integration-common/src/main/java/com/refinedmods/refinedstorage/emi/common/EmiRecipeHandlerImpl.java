package com.refinedmods.refinedstorage.emi.common;

import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage2.api.resource.list.ResourceList;
import com.refinedmods.refinedstorage2.platform.common.grid.CraftingGridContainerMenu;
import com.refinedmods.refinedstorage2.platform.common.support.resource.ItemResource;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.runtime.EmiDrawContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

class EmiRecipeHandlerImpl implements EmiRecipeHandler<CraftingGridContainerMenu> {
    @Override
    public EmiPlayerInventory getInventory(final AbstractContainerScreen<CraftingGridContainerMenu> screen) {
        final ResourceList available = screen.getMenu().getAvailableListForRecipeTransfer();
        return new EmiPlayerInventory(available.getAll().stream()
            .filter(resourceAmount -> resourceAmount.getResource() instanceof ItemResource)
            .map(resourceAmount -> EmiStack.of(
                ((ItemResource) resourceAmount.getResource()).item(),
                ((ItemResource) resourceAmount.getResource()).tag(),
                resourceAmount.getAmount()
            )).toList());
    }

    @Override
    public boolean supportsRecipe(final EmiRecipe recipe) {
        return recipe instanceof EmiCraftingRecipe;
    }

    @Override
    public boolean canCraft(final EmiRecipe recipe, final EmiCraftContext<CraftingGridContainerMenu> context) {
        final ResourceList available = context.getScreenHandler().getAvailableListForRecipeTransfer();
        return !hasMissingItems(recipe.getInputs(), available);
    }

    private boolean hasMissingItems(final List<EmiIngredient> inputs, final ResourceList available) {
        for (final EmiIngredient input : inputs) {
            if (input.isEmpty()) {
                continue;
            }
            if (!isAvailable(available, input)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAvailable(final ResourceList available, final EmiIngredient input) {
        final List<ItemResource> possibilities = getStacks(input);
        for (final ItemResource possibility : possibilities) {
            if (available.remove(possibility, 1).isPresent()) {
                return true;
            }
        }
        return false;
    }

    private List<ItemResource> getStacks(final EmiIngredient input) {
        return input.getEmiStacks()
            .stream()
            .map(EmiStack::getItemStack)
            .filter(stack -> !stack.isEmpty())
            .map(ItemResource::ofItemStack)
            .collect(Collectors.toList());
    }

    @Override
    public boolean craft(final EmiRecipe recipe, final EmiCraftContext<CraftingGridContainerMenu> context) {
        final List<List<ItemResource>> inputs = recipe.getInputs()
            .stream()
            .map(this::getStacks)
            .toList();
        context.getScreenHandler().transferRecipe(inputs);
        Minecraft.getInstance().setScreen(context.getScreen());
        return true;
    }

    @Override
    public void render(final EmiRecipe recipe,
                       final EmiCraftContext<CraftingGridContainerMenu> craftContext,
                       final List<Widget> widgets,
                       final GuiGraphics draw) {
        final EmiDrawContext context = EmiDrawContext.wrap(draw);
        RenderSystem.enableDepthTest();
        final ResourceList available = craftContext.getScreenHandler().getAvailableListForRecipeTransfer();
        for (final Widget widget : widgets) {
            if (!(widget instanceof SlotWidget slotWidget)) {
                continue;
            }
            final EmiIngredient stack = slotWidget.getStack();
            final Bounds bounds = slotWidget.getBounds();
            if (slotWidget.getRecipe() == null && !stack.isEmpty() && !isAvailable(available, stack)) {
                context.fill(bounds.x(), bounds.y(), bounds.width(), bounds.height(), 0x44FF0000);
            }
        }
    }
}
