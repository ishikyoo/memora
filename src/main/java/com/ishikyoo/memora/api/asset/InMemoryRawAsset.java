package com.ishikyoo.memora.api.asset;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class InMemoryRawAsset extends InMemoryAsset {

    private InMemoryRawAsset(@NotNull Key key, byte @Nullable [] data) {
        super(key, data);
    }

    public @NotNull InMemoryRawAsset of(@NotNull ResourceLocation location, byte @NotNull [] data) {
        Key key = Key.of(Objects.requireNonNull(location));
        return new InMemoryRawAsset(key, Objects.requireNonNull(data));
    }

    public @NotNull InMemoryRawAsset withLocation(@NotNull ResourceLocation location) {
        if (Objects.requireNonNull(location).equals(location())) return this;
        Key key = Key.of(location);
        return new InMemoryRawAsset(key, bytes);
    }

    public @NotNull InMemoryRawAsset withData(byte @NotNull [] data) {
        if (Objects.requireNonNull(data) == bytes()) return this;
        return new InMemoryRawAsset(key(), data);
    }

    @Override
    protected byte @NotNull [] toBytes() {
        assert bytes != null;
        return bytes;
    }
}
