package com.refinedmods.refinedstorage.emi.common;

import com.refinedmods.refinedstorage.api.grid.view.GridView;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.list.MutableResourceList;
import com.refinedmods.refinedstorage.api.resource.list.MutableResourceListImpl;
import com.refinedmods.refinedstorage.api.resource.list.ResourceList;
import com.refinedmods.refinedstorage.common.api.RefinedStorageClientApi;
import com.refinedmods.refinedstorage.common.grid.AbstractCraftingGridContainerMenu;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.common.support.tooltip.HelpClientTooltipComponent;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.runtime.EmiDrawContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

import static com.refinedmods.refinedstorage.emi.common.Common.MOD_ID;
import static java.util.Comparator.comparingLong;

class CraftingGridEmiRecipeHandler<T extends AbstractCraftingGridContainerMenu> extends AbstractEmiRecipeHandler<T> {
    private static final Component CTRL_CLICK_TO_AUTOCRAFT = Component.translatable(
        "gui.%s.transfer.ctrl_click_to_autocraft".formatted(MOD_ID)
    );

    @Override
    public EmiPlayerInventory getInventory(final AbstractContainerScreen<T> screen) {
        final ResourceList available = screen.getMenu().getAvailableListForRecipeTransfer();
        return new EmiPlayerInventory(available.copyState().stream()
            .filter(resourceAmount -> resourceAmount.resource() instanceof ItemResource)
            .map(resourceAmount -> EmiStack.of(
                ((ItemResource) resourceAmount.resource()).item(),
                ((ItemResource) resourceAmount.resource()).components(),
                resourceAmount.amount()
            )).toList());
    }

    @Override
    public boolean supportsRecipe(final EmiRecipe recipe) {
        return recipe instanceof EmiCraftingRecipe;
    }

    @Override
    public boolean canCraft(final EmiRecipe recipe, final EmiCraftContext<T> context) {
        return true;
    }

    private static TransferInput toTransferInput(final GridView view,
                                                 final MutableResourceList available,
                                                 final EmiIngredient input) {
        final List<ItemResource> possibilities = getItems(input);
        for (final ItemResource possibility : possibilities) {
            if (available.remove(possibility, 1).isPresent()) {
                return new TransferInput(TransferInputType.AVAILABLE, null);
            }
        }
        final List<ItemResource> autocraftingPossibilities = possibilities
            .stream()
            .filter(view::isAutocraftable)
            .sorted(comparingLong(view::getAmount))
            .toList();
        if (!autocraftingPossibilities.isEmpty()) {
            return new TransferInput(TransferInputType.AUTOCRAFTABLE, autocraftingPossibilities.getFirst());
        }
        return new TransferInput(TransferInputType.MISSING, null);
    }

    private static List<ItemResource> getItems(final EmiIngredient ingredient) {
        return ingredient.getEmiStacks()
            .stream()
            .map(EmiStack::getItemStack)
            .filter(stack -> !stack.isEmpty())
            .map(ItemResource::ofItemStack)
            .collect(Collectors.toList());
    }

    @Override
    public boolean craft(final EmiRecipe recipe, final EmiCraftContext<T> context) {
        final MutableResourceList available = context.getScreenHandler().getAvailableListForRecipeTransfer();
        final List<TransferInput> transferInputs = recipe.getInputs()
            .stream()
            .filter(input -> !input.isEmpty())
            .map(input -> toTransferInput(context.getScreenHandler().getView(), available, input))
            .toList();
        final TransferType transferType = getTransferType(transferInputs);
        if (transferType.canOpenAutocraftingPreview() && Screen.hasControlDown()) {
            final List<ResourceAmount> craftingRequests = createCraftingRequests(transferInputs);
            RefinedStorageClientApi.INSTANCE.openAutocraftingPreview(craftingRequests, context.getScreen());
            return false;
        }
        final List<List<ItemResource>> inputs = recipe.getInputs()
            .stream()
            .map(CraftingGridEmiRecipeHandler::getItems)
            .toList();
        context.getScreenHandler().transferRecipe(inputs);
        return true;
    }

    @Override
    public void render(final EmiRecipe recipe,
                       final EmiCraftContext<T> craftContext,
                       final List<Widget> widgets,
                       final GuiGraphics draw) {
        final EmiDrawContext context = EmiDrawContext.wrap(draw);
        RenderSystem.enableDepthTest();
        final MutableResourceList available = craftContext.getScreenHandler().getAvailableListForRecipeTransfer();
        final GridView view = craftContext.getScreenHandler().getView();
        for (final Widget widget : widgets) {
            if (!(widget instanceof SlotWidget slotWidget)) {
                continue;
            }
            final EmiIngredient stack = slotWidget.getStack();
            final Bounds bounds = slotWidget.getBounds();
            if (slotWidget.getRecipe() == null && !stack.isEmpty()) {
                final TransferInput transferInput = toTransferInput(view, available, stack);
                if (transferInput.type() == TransferInputType.MISSING) {
                    context.fill(bounds.x(), bounds.y(), bounds.width(), bounds.height(), 0x44FF0000);
                } else if (transferInput.type() == TransferInputType.AUTOCRAFTABLE) {
                    context.fill(bounds.x(), bounds.y(), bounds.width(), bounds.height(), AUTOCRAFTABLE_COLOR);
                }
            }
        }
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(final EmiRecipe recipe,
                                                   final EmiCraftContext<T> context) {
        final MutableResourceList available = context.getScreenHandler().getAvailableListForRecipeTransfer();
        final List<TransferInput> transferInputs = recipe.getInputs()
            .stream()
            .filter(input -> !input.isEmpty())
            .map(input -> toTransferInput(context.getScreenHandler().getView(), available, input))
            .toList();
        final TransferType transferType = getTransferType(transferInputs);
        return calculateTooltip(transferType);
    }

    private List<ClientTooltipComponent> calculateTooltip(final TransferType type) {
        return switch (type) {
            case MISSING -> List.of(ClientTooltipComponent.create(EmiPort.ordered(NOT_ENOUGH_INGREDIENTS)));
            case MISSING_BUT_ALL_AUTOCRAFTABLE -> List.of(
                createAutocraftableHint(
                    Component.translatable("gui.%s.transfer.missing_but_all_autocraftable".formatted(MOD_ID))
                ),
                HelpClientTooltipComponent.createAlwaysDisplayed(CTRL_CLICK_TO_AUTOCRAFT)
            );
            case MISSING_BUT_SOME_AUTOCRAFTABLE -> List.of(
                createAutocraftableHint(
                    Component.translatable("gui.%s.transfer.missing_but_some_autocraftable".formatted(MOD_ID))
                ),
                HelpClientTooltipComponent.createAlwaysDisplayed(CTRL_CLICK_TO_AUTOCRAFT)
            );
            default -> Collections.emptyList();
        };
    }

    private TransferType getTransferType(final List<TransferInput> transferInputs) {
        if (transferInputs.stream().allMatch(input -> input.type() == TransferInputType.AVAILABLE)) {
            return TransferType.AVAILABLE;
        }
        final boolean hasMissing = transferInputs.stream().anyMatch(input -> input.type() == TransferInputType.MISSING);
        final boolean hasAutocraftable = transferInputs.stream()
            .anyMatch(input -> input.type() == TransferInputType.AUTOCRAFTABLE);
        if (hasMissing && hasAutocraftable) {
            return TransferType.MISSING_BUT_SOME_AUTOCRAFTABLE;
        } else if (hasAutocraftable) {
            return TransferType.MISSING_BUT_ALL_AUTOCRAFTABLE;
        }
        return TransferType.MISSING;
    }

    private static List<ResourceAmount> createCraftingRequests(final List<TransferInput> transferInputs) {
        final MutableResourceList requests = MutableResourceListImpl.orderPreserving();
        for (final TransferInput transferInput : transferInputs) {
            if (transferInput.type() == TransferInputType.AUTOCRAFTABLE
                && transferInput.autocraftableResource() != null) {
                requests.add(transferInput.autocraftableResource(), 1);
            }
        }
        return requests.copyState().stream().toList();
    }
}
