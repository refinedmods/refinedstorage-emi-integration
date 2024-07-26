package com.refinedmods.refinedstorage.emi.fabric;

import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.fabric.api.RefinedStoragePlugin;

import static com.refinedmods.refinedstorage.emi.common.Common.init;

public class EmiRefinedStoragePlugin implements RefinedStoragePlugin {
    @Override
    public void onApiAvailable(final RefinedStorageApi api) {
        init(api);
    }
}
