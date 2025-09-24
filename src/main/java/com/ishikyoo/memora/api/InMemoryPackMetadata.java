package com.ishikyoo.memora.api;

import com.ishikyoo.memora.api.asset.InMemoryPackMetadataAsset;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.util.ResourceLocationPattern;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class InMemoryPackMetadata {

    private final @NotNull String id;
    private final @NotNull Component title;
    private final @NotNull PackSource source;
    private final @NotNull PackSelectionConfig config;
    private final @NotNull InMemoryPackMetadataAsset asset;

    private InMemoryPackMetadata(@NotNull String id, @NotNull Component title, @NotNull PackSource source,
                                 @NotNull PackSelectionConfig config, @NotNull InMemoryPackMetadataAsset asset) {
        this.id = id;
        this.title = title;
        this.source = source;
        this.config = config;
        this.asset = asset;
    }

    public static @NotNull InMemoryPackMetadata of(@NotNull String id, @NotNull PackType type,
                                                   @NotNull PackSource source, @NotNull PackSelectionConfig config,
                                                   @NotNull Component title, @NotNull Component description) {
        InMemoryPackMetadataAsset internal = InMemoryPackMetadataAsset.of(
                Objects.requireNonNull(type),
                Objects.requireNonNull(description)
        );
        return new InMemoryPackMetadata(
                Objects.requireNonNull(id),
                Objects.requireNonNull(title),
                Objects.requireNonNull(source),
                Objects.requireNonNull(config),
                internal
        );
    }

    public @NotNull String id() {
        return id;
    }

    public @NotNull PackType type() {
        return asset.type();
    }

    public @NotNull PackSource source() {
        return source;
    }

    public @NotNull PackSelectionConfig config() {
        return config;
    }

    public @NotNull Component title() {
        return title;
    }

    public @NotNull Component description() {
        return asset.description();
    }

    public @NotNull InMemoryPackMetadataAsset asset() {
        return asset;
    }

    public @NotNull Collection<ResourceLocationPattern> filters() {
        return asset.filters();
    }

    public @NotNull Collection<LanguageInfo> languages() {
        return asset.languages();
    }

    public InMemoryPackMetadata withId(@NotNull String id) {
        return new InMemoryPackMetadata(Objects.requireNonNull(id), title, source, config, asset);
    }

    public InMemoryPackMetadata withTitle(@NotNull Component title) {
        return new InMemoryPackMetadata(id, Objects.requireNonNull(title), source, config, asset);
    }

    public @NotNull InMemoryPackMetadata withType(@NotNull PackType type) {
        return new InMemoryPackMetadata(id, title, source, config, asset.withType(Objects.requireNonNull(type)));
    }

    public @NotNull InMemoryPackMetadata withSource(@NotNull PackSource source) {
        return new InMemoryPackMetadata(id, title, Objects.requireNonNull(source), config, asset);
    }

    public @NotNull InMemoryPackMetadata withConfig(@NotNull PackSelectionConfig config) {
        return new InMemoryPackMetadata(id, title, source, Objects.requireNonNull(config), asset);
    }

    public @NotNull InMemoryPackMetadata withDescription(Component description) {
        return new InMemoryPackMetadata(id, title, source, config, asset.withDescription(Objects.requireNonNull(description)));
    }

    public @NotNull InMemoryPackMetadata withFeature(@NotNull ResourceLocation feature) {
        return new InMemoryPackMetadata(id, title, source, config, asset.withFeature(Objects.requireNonNull(feature)));
    }

    public @NotNull InMemoryPackMetadata withFilter(@NotNull ResourceLocationPattern pattern) {
        return new InMemoryPackMetadata(id, title, source, config, asset.withFilter(Objects.requireNonNull(pattern)));
    }

    public @NotNull InMemoryPackMetadata withLanguage(@NotNull String code, @NotNull LanguageInfo info) {
        return new InMemoryPackMetadata(id, title, source, config, asset.withLanguage(Objects.requireNonNull(code), Objects.requireNonNull(info)));
    }
}