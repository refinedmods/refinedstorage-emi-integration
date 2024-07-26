package com.refinedmods.refinedstorage.emi.common;

import com.refinedmods.refinedstorage.common.support.AbstractBaseScreen;

import java.util.List;
import java.util.function.Consumer;

import dev.emi.emi.api.EmiExclusionArea;
import dev.emi.emi.api.widget.Bounds;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;

class EmiExclusionAreaImpl implements EmiExclusionArea<Screen> {
    @Override
    public void addExclusionArea(final Screen screen, final Consumer<Bounds> consumer) {
        if (!(screen instanceof AbstractBaseScreen<?> baseScreen)) {
            return;
        }
        final List<Rect2i> exclusionZones = baseScreen.getExclusionZones();
        exclusionZones.forEach(zone -> consumer.accept(new Bounds(
            zone.getX(), zone.getY(), zone.getWidth(), zone.getHeight()
        )));
    }
}
