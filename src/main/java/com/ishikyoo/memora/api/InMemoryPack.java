package com.ishikyoo.memora.api;

import com.ishikyoo.memora.api.asset.InMemoryAsset;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class InMemoryPack {

    private final @NotNull InMemoryPackMetadata metadata;

    private final @NotNull Map<ResourceLocation, InMemoryAsset> assets;

    private InMemoryPack(@NotNull InMemoryPackMetadata metadata, @NotNull Map<ResourceLocation, InMemoryAsset> assets) {
        this.metadata = metadata;
        this.assets = Collections.unmodifiableMap(assets);
    }

    public static InMemoryPack of(@NotNull InMemoryPackMetadata metadata) {
        return new InMemoryPack(metadata, new HashMap<>()).withAsset(metadata.asset());
    }

    public @NotNull InMemoryPackMetadata metadata() {
        return metadata;
    }

    public @NotNull InMemoryPack withAsset(@NotNull InMemoryAsset asset) {
        if (assets.containsKey(Objects.requireNonNull(asset.location())) && assets.containsValue(asset)) return this;
        Map<ResourceLocation, InMemoryAsset> assets = new HashMap<>(this.assets);
        assets.put(asset.location(), asset);
        return new InMemoryPack(metadata, assets);
    }

    public @NotNull InMemoryPack withoutAsset(@NotNull ResourceLocation location) {
        if (!assets.containsKey(Objects.requireNonNull(location))) return this;
        Map<ResourceLocation, InMemoryAsset> assets = new HashMap<>(this.assets);
        assets.remove(location);
        return new InMemoryPack(metadata, assets);
    }

    public @NotNull InMemoryPack withoutAsset(@NotNull InMemoryAsset asset) {
        return withoutAsset(asset.location());
    }

    public @NotNull <T extends InMemoryAsset> Optional<T> asset(@NotNull ResourceLocation location, @NotNull Class<T> type) {
        InMemoryAsset asset = assets.get(Objects.requireNonNull(location));
        if (asset == null) return Optional.empty();
        if (asset.getClass() != Objects.requireNonNull(type)) return Optional.empty();
        return Optional.of(type.cast(asset));
    }

    public @NotNull Optional<InMemoryAsset> asset(@NotNull ResourceLocation location) {
        return Optional.ofNullable(assets.get(Objects.requireNonNull(location)));
    }

    public @NotNull Collection<InMemoryAsset> assets() {
        return Collections.unmodifiableCollection(assets.values());
    }
}