package com.ishikyoo.memora.api.asset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

public abstract class InMemoryJSONAsset extends InMemoryTextAsset {

    protected static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    protected InMemoryJSONAsset(@NotNull Key key) {
        super(key);
    }

    protected abstract @NotNull JsonElement toJson();

    @Override
    protected @NotNull String toText() {
        try {
            JsonElement json = toJson();
            return GSON.toJson(json);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to serialize JSON asset: " + key(), e
            );
        }
    }
}
