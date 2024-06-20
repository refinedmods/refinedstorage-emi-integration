package com.refinedmods.refinedstorage.emi.common;

import com.refinedmods.refinedstorage2.platform.api.PlatformApi;
import com.refinedmods.refinedstorage2.platform.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage2.platform.common.Platform;
import com.refinedmods.refinedstorage2.platform.common.support.AbstractBaseScreen;
import com.refinedmods.refinedstorage2.platform.common.support.containermenu.AbstractResourceContainerMenu;
import com.refinedmods.refinedstorage2.platform.common.support.containermenu.ResourceSlot;
import dev.emi.emi.api.EmiDragDropHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.runtime.EmiDrawContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

class EmiDragDropHandlerImpl implements EmiDragDropHandler<Screen> {
    @Override
    public boolean dropStack(final Screen screen, final EmiIngredient stack, final int x, final int y) {
        if (!(screen instanceof AbstractBaseScreen<?> baseScreen)) {
            return false;
        }
        if (!(baseScreen.getMenu() instanceof AbstractResourceContainerMenu menu)) {
            return false;
        }
        return PlatformApi.INSTANCE.getIngredientConverter().convertToResource(stack)
            .map(resource -> dropStack(menu, baseScreen, resource, x, y))
            .orElse(false);
    }

    private boolean dropStack(final AbstractResourceContainerMenu menu,
                              final AbstractBaseScreen<?> baseScreen,
                              final PlatformResourceKey resource,
                              final int x,
                              final int y) {
        for (final ResourceSlot slot : menu.getResourceSlots()) {
            if (dropStack(resource, slot, x, y, baseScreen)) {
                return true;
            }
        }
        return false;
    }

    private boolean dropStack(final PlatformResourceKey resource,
                              final ResourceSlot slot,
                              final int x,
                              final int y,
                              final AbstractBaseScreen<?> baseScreen) {
        if (!slot.isFilter() || !slot.isValid(resource)) {
            return false;
        }
        final int slotX = baseScreen.getLeftPos() + slot.x;
        final int slotY = baseScreen.getTopPos() + slot.y;
        if (x < slotX || y < slotY || x > slotX + 16 || y > slotY + 16) {
            return false;
        }
        Platform.INSTANCE.getClientToServerCommunications().sendResourceFilterSlotChange(
            resource,
            slot.index
        );
        return true;
    }

    @Override
    public void render(final Screen screen,
                       final EmiIngredient dragged,
                       final GuiGraphics draw,
                       final int mouseX,
                       final int mouseY,
                       final float delta) {
        if (!(screen instanceof AbstractBaseScreen<?> baseScreen)) {
            return;
        }
        if (!(baseScreen.getMenu() instanceof AbstractResourceContainerMenu menu)) {
            return;
        }
        PlatformApi.INSTANCE.getIngredientConverter().convertToResource(dragged).ifPresent(resource -> {
            final EmiDrawContext context = EmiDrawContext.wrap(draw);
            for (final ResourceSlot slot : menu.getResourceSlots()) {
                if (!slot.isFilter() || !slot.isValid(resource)) {
                    continue;
                }
                context.fill(baseScreen.getLeftPos() + slot.x, baseScreen.getTopPos() + slot.y, 17, 17, 0x8822BB33);
            }
        });
    }
}

