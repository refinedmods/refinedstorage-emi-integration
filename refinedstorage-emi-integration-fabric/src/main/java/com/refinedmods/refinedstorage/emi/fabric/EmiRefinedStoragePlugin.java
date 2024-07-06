package com.refinedmods.refinedstorage.emi.fabric;

import com.refinedmods.refinedstorage.platform.api.PlatformApi;
import com.refinedmods.refinedstorage.platform.api.RefinedStoragePlugin;

import static com.refinedmods.refinedstorage.emi.common.Common.init;

public class EmiRefinedStoragePlugin implements RefinedStoragePlugin {
    @Override
    public void onPlatformApiAvailable(final PlatformApi platformApi) {
        init(platformApi);
    }
}
