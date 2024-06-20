package com.refinedmods.refinedstorage.emi.forge;

import com.refinedmods.refinedstorage.emi.common.Common;

import com.refinedmods.refinedstorage2.platform.api.PlatformApi;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import static com.refinedmods.refinedstorage.emi.common.Common.init;

@Mod(Common.MOD_ID)
public class ModInitializer {
    public ModInitializer(final IEventBus eventBus) {
        eventBus.addListener(ModInitializer::onCommonSetup);
    }

    @SubscribeEvent
    public static void onCommonSetup(final FMLCommonSetupEvent e) {
        init(PlatformApi.INSTANCE);
    }
}
