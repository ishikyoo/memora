package com.ishikyoo.memora.api.asset;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class InMemoryRawTextAsset extends InMemoryTextAsset {

    private final @NotNull String text;

    private InMemoryRawTextAsset(@NotNull Key key, @NotNull String text) {
        super(key);
        this.text = text;
    }

    public static @NotNull InMemoryRawTextAsset of(@NotNull ResourceLocation location, @NotNull String text) {
        Key key = Key.of(Objects.requireNonNull(location));
        return new InMemoryRawTextAsset(key, Objects.requireNonNull(text));
    }

    public @NotNull InMemoryRawTextAsset withLocation(@NotNull ResourceLocation location) {
        if (Objects.requireNonNull(location).equals(location())) return this;
        Key key = Key.of(location);
        return new InMemoryRawTextAsset(key, text);
    }

    public @NotNull InMemoryRawTextAsset withText(@NotNull String text) {
        if (Objects.requireNonNull(text).equals(this.text)) return this;
        return new InMemoryRawTextAsset(key(), text);
    }

    @Override
    protected @NotNull String toText() {
        return text;
    }
}