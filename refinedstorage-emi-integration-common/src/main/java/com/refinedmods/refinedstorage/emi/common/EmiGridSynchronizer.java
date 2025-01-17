package com.refinedmods.refinedstorage.emi.common;

import com.refinedmods.refinedstorage.common.api.grid.GridSynchronizer;
import com.refinedmods.refinedstorage.common.grid.NoopGridSynchronizer;

import javax.annotation.Nullable;

import dev.emi.emi.api.EmiApi;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import static com.refinedmods.refinedstorage.emi.common.Common.MOD_ID;

class EmiGridSynchronizer implements GridSynchronizer {
    private static final MutableComponent TITLE = Component.translatable("gui.%s.grid.synchronizer".formatted(MOD_ID));
    private static final MutableComponent TITLE_TWO_WAY = Component.translatable(
        "gui.%s.grid.synchronizer.two_way".formatted(MOD_ID)
    );
    private static final Component HELP = Component.translatable("gui.%s.grid.synchronizer.help".formatted(MOD_ID));
    private static final Component HELP_TWO_WAY = Component.translatable(
        "gui.%s.grid.synchronizer.two_way.help".formatted(MOD_ID)
    );

    private final boolean twoWay;

    EmiGridSynchronizer(final boolean twoWay) {
        this.twoWay = twoWay;
    }

    @Override
    public MutableComponent getTitle() {
        return twoWay ? TITLE_TWO_WAY : TITLE;
    }

    @Override
    public Component getHelpText() {
        return twoWay ? HELP_TWO_WAY : HELP;
    }

    @Override
    public void synchronizeFromGrid(final String text) {
        EmiApi.setSearchText(text);
    }

    @Override
    @Nullable
    public String getTextToSynchronizeToGrid() {
        return twoWay ? EmiApi.getSearchText() : null;
    }

    @Override
    public ResourceLocation getSprite() {
        return twoWay ? NoopGridSynchronizer.ON_TWO_WAY : NoopGridSynchronizer.ON;
    }
}
