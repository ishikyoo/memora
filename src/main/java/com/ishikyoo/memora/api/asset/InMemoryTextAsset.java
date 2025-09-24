package com.ishikyoo.memora.api.asset;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public abstract class InMemoryTextAsset extends InMemoryAsset {

    protected InMemoryTextAsset(@NotNull Key key) {
        super(key, null);
    }

    @Override
    protected byte @NotNull [] toBytes() {
        try {
            String text = text();
            return text.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to convert text to bytes: " + key(), e
            );
        }
    }

    public @NotNull String text() {
        try {
            return toText();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to convert asset to text: " + key(), e
            );
        }
    }

    protected abstract @NotNull String toText();
}