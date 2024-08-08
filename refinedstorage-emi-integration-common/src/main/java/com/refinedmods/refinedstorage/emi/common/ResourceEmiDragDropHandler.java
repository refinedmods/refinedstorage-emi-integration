package com.refinedmods.refinedstorage.emi.common;

import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.support.AbstractBaseScreen;
import com.refinedmods.refinedstorage.common.support.containermenu.AbstractResourceContainerMenu;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlot;
import com.refinedmods.refinedstorage.common.support.packet.c2s.C2SPackets;

import dev.emi.emi.api.EmiDragDropHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.runtime.EmiDrawContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

class ResourceEmiDragDropHandler implements EmiDragDropHandler<Screen> {
    @Override
    public boolean dropStack(final Screen screen, final EmiIngredient stack, final int x, final int y) {
        if (!(screen instanceof AbstractBaseScreen<?> baseScreen)) {
            return false;
        }
        if (!(baseScreen.getMenu() instanceof AbstractResourceContainerMenu menu)) {
            return false;
        }
        return RefinedStorageApi.INSTANCE.getIngredientConverter().convertToResource(stack)
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
        if (!isSlotValid(resource, slot)) {
            return false;
        }
        final int slotX = baseScreen.getLeftPos() + slot.x;
        final int slotY = baseScreen.getTopPos() + slot.y;
        if (x < slotX || y < slotY || x > slotX + 16 || y > slotY + 16) {
            return false;
        }
        C2SPackets.sendResourceFilterSlotChange(resource, slot.index);
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
        RefinedStorageApi.INSTANCE.getIngredientConverter().convertToResource(dragged).ifPresent(resource -> {
            final EmiDrawContext context = EmiDrawContext.wrap(draw);
            for (final ResourceSlot slot : menu.getResourceSlots()) {
                if (!isSlotValid(resource, slot)) {
                    continue;
                }
                context.fill(baseScreen.getLeftPos() + slot.x, baseScreen.getTopPos() + slot.y, 17, 17, 0x8822BB33);
            }
        });
    }

    private static boolean isSlotValid(final PlatformResourceKey resource, final ResourceSlot slot) {
        return slot.isFilter() && slot.isActive() && slot.isValid(resource);
    }
}

