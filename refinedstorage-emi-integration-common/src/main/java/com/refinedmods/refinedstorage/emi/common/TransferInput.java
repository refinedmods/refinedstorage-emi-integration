package com.refinedmods.refinedstorage.emi.common;

import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import javax.annotation.Nullable;

record TransferInput(TransferInputType type, @Nullable ItemResource autocraftableResource) {
}
