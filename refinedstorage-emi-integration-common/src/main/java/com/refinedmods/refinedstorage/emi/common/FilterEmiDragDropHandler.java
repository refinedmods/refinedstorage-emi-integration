package com.refinedmods.refinedstorage.emi.common;

import com.refinedmods.refinedstorage.common.support.AbstractBaseContainerMenu;
import com.refinedmods.refinedstorage.common.support.AbstractBaseScreen;
import com.refinedmods.refinedstorage.common.support.containermenu.FilterSlot;
import com.refinedmods.refinedstorage.common.support.packet.c2s.C2SPackets;

import dev.emi.emi.api.EmiDragDropHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.ItemEmiStack;
import dev.emi.emi.runtime.EmiDrawContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

class FilterEmiDragDropHandler implements EmiDragDropHandler<Screen> {
    @Override
    public boolean dropStack(final Screen screen, final EmiIngredient stack, final int x, final int y) {
        if (!(screen instanceof AbstractBaseScreen<?> baseScreen)) {
            return false;
        }
        if (!(baseScreen.getMenu() instanceof AbstractBaseContainerMenu menu)) {
            return false;
        }
        if (!(stack instanceof ItemEmiStack emiStack)) {
            return false;
        }
        return dropStack(menu, baseScreen, emiStack.getItemStack(), x, y);
    }

    private boolean dropStack(final AbstractBaseContainerMenu menu,
                              final AbstractBaseScreen<?> baseScreen,
                              final ItemStack stack,
                              final int x,
                              final int y) {
        for (final Slot slot : menu.slots) {
            if (slot instanceof FilterSlot filterSlot && dropStack(stack, filterSlot, x, y, baseScreen)) {
                return true;
            }
        }
        return false;
    }

    private boolean dropStack(final ItemStack stack,
                              final FilterSlot slot,
                              final int x,
                              final int y,
                              final AbstractBaseScreen<?> baseScreen) {
        if (!isSlotValid(stack, slot)) {
            return false;
        }
        final int slotX = baseScreen.getLeftPos() + slot.x;
        final int slotY = baseScreen.getTopPos() + slot.y;
        if (x < slotX || y < slotY || x > slotX + 16 || y > slotY + 16) {
            return false;
        }
        C2SPackets.sendFilterSlotChange(stack, slot.index);
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
        if (!(baseScreen.getMenu() instanceof AbstractBaseContainerMenu menu)) {
            return;
        }
        if (dragged.isEmpty() || !(dragged.getEmiStacks().getFirst() instanceof ItemEmiStack emiStack)) {
            return;
        }
        final ItemStack stack = emiStack.getItemStack();
        final EmiDrawContext context = EmiDrawContext.wrap(draw);
        for (final Slot slot : menu.slots) {
            if (!(slot instanceof FilterSlot filterSlot) || !isSlotValid(stack, filterSlot)) {
                continue;
            }
            context.fill(baseScreen.getLeftPos() + slot.x, baseScreen.getTopPos() + slot.y, 17, 17, 0x8822BB33);
        }
    }

    private static boolean isSlotValid(final ItemStack stack, final FilterSlot slot) {
        return slot.isActive() && slot.mayPlace(stack);
    }
}

