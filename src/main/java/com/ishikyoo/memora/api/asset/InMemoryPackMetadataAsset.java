package com.ishikyoo.memora.api.asset;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ishikyoo.memora.internal.InMemoryPackResources;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.minecraft.SharedConstants;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.OverlayMetadataSection;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.ResourceFilterSection;
import net.minecraft.util.ResourceLocationPattern;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class InMemoryPackMetadataAsset extends InMemoryJSONAsset {

    private final @NotNull PackMetadataSection metadata;
    private final @NotNull PackType type;
    private final @NotNull Set<ResourceLocation> features;
    private final @NotNull Set<ResourceLocationPattern> filters;
    private final @NotNull Set<OverlayMetadataSection.OverlayEntry> overlays;
    private final @NotNull Map<String, LanguageInfo> languages;

    private InMemoryPackMetadataAsset(@NotNull Key key,
                                      @NotNull PackType type,
                                      @NotNull Set<ResourceLocation> features,
                                      @NotNull PackMetadataSection metadata,
                                      @NotNull Set<ResourceLocationPattern> filters,
                                      @NotNull Set<OverlayMetadataSection.OverlayEntry> overlays,
                                      @NotNull Map<String, LanguageInfo> languages) {
        super(key);
        this.type = type;
        this.features = features;
        this.metadata = metadata;
        this.filters = Collections.unmodifiableSet(filters);
        this.overlays = Collections.unmodifiableSet(overlays);
        this.languages = Collections.unmodifiableMap(languages);
    }

    public static @NotNull InMemoryPackMetadataAsset of(@NotNull PackType type, @NotNull Component description) {
        Key key = Key.of(InMemoryPackResources.ROOT_NAMESPACE, "", "pack", "mcmeta");
        PackMetadataSection metadata = new PackMetadataSection(
                description,
                SharedConstants.getCurrentVersion().packVersion(type),
                Optional.empty());
        return new InMemoryPackMetadataAsset(key, type, new HashSet<>(), metadata, new HashSet<>(), new HashSet<>(), new HashMap<>());
    }

    public @NotNull PackType type() {
        return type;
    }

    public @NotNull Component description() {
        return metadata.description();
    }

    public @NotNull Collection<ResourceLocationPattern> filters() {
        return filters;
    }

    public @NotNull Collection<LanguageInfo> languages() {
        return Collections.unmodifiableCollection(languages.values());
    }

    public @NotNull InMemoryPackMetadataAsset withType(@NotNull PackType type) {
        if (type.equals(this.type)) return this;
        PackMetadataSection metadata = new PackMetadataSection(
                this.metadata.description(),
                SharedConstants.getCurrentVersion().packVersion(type),
                Optional.empty());
        return new InMemoryPackMetadataAsset(key, type, features, metadata, filters, overlays, languages);
    }

    public @NotNull InMemoryPackMetadataAsset withFeature(@NotNull ResourceLocation feature) {
        if (features.contains(feature)) return this;
        Set<ResourceLocation> features = new HashSet<>(this.features);
        features.add(feature);
        return new InMemoryPackMetadataAsset(key, type, features, metadata, filters, overlays, languages);
    }

    public InMemoryPackMetadataAsset withDescription(Component description) {
        if (metadata.description().equals(description)) return this;
        return new InMemoryPackMetadataAsset(key(), type, features,
                new PackMetadataSection(description,
                        metadata.packFormat(),
                        metadata.supportedFormats()
                ), filters, overlays, languages);
    }

    public InMemoryPackMetadataAsset withFilter(@NotNull ResourceLocationPattern pattern) {
        if (this.filters.contains(pattern)) return this;
        Set<ResourceLocationPattern> filters = new HashSet<>(this.filters);
        filters.add(pattern);
        return new InMemoryPackMetadataAsset(key, type, features, metadata, filters, overlays, languages);
    }

    public InMemoryPackMetadataAsset withLanguage(@NotNull String code, @NotNull LanguageInfo info) {
        if (!languages.containsKey(code) && !languages.containsValue(info)) return this;
        Map<String, LanguageInfo> languages = new HashMap<>(this.languages);
        languages.put(code, info);
        return new InMemoryPackMetadataAsset(key, type, features, metadata, filters, overlays, languages);
    }

    @Override
    protected @NotNull JsonElement toJson() {
        Optional<JsonElement> optionalEncodedPackSection = encodePackSection(metadata);
        Optional<JsonElement> optionalFeaturesSection = encodeFeaturesSection(features);
        ResourceFilterSection filterSection = new ResourceFilterSection(filters.stream().toList());
        Optional<JsonElement> optionalEncodedFiltersSection = encodeFilterSection(filterSection);
        OverlayMetadataSection overlayMetadataSection = new OverlayMetadataSection(overlays.stream().toList());
        Optional<JsonElement> optionalOverlayMetadataSection = encodeOverlayMetadataSection(overlayMetadataSection);
        Optional<JsonElement> optionalLanguagesSection = encodeLanguagesSection(languages);

        JsonObject root = new JsonObject();
        optionalEncodedPackSection.ifPresent(jsonElement -> root.add("pack", jsonElement));
        optionalFeaturesSection.ifPresent(jsonElement -> root.add("features", jsonElement));
        optionalEncodedFiltersSection.ifPresent(jsonElement -> root.add("filter", jsonElement));
        optionalOverlayMetadataSection.ifPresent(jsonElement -> root.add("overlays", jsonElement));
        optionalLanguagesSection.ifPresent(jsonElement -> root.add("language", jsonElement));

        return root;
    }

    private static Optional<JsonElement> encodePackSection(PackMetadataSection section) {
        var encoded = PackMetadataSection.CODEC.encodeStart(
                JsonOps.INSTANCE,
                section
        );
        return encoded.resultOrPartial(error -> {
            throw new IllegalStateException("Failed to encode pack metadata section: " + error);
        });
    }

    private static Optional<JsonElement> encodeFeaturesSection(Set<ResourceLocation> features) {
        if (features.isEmpty()) {
            return Optional.empty();
        }
        JsonObject obj = new JsonObject();
        JsonArray array = new JsonArray();
        for (ResourceLocation feature : features) {
            array.add(feature.toString());
        }
        obj.add("enabled", array);
        return Optional.of(obj);
    }

    private static Optional<JsonElement> encodeFilterSection(ResourceFilterSection section) {
        var encoded = ResourceFilterSection.TYPE.codec().encodeStart(
                JsonOps.INSTANCE,
                section
        );
        return encoded.resultOrPartial(error -> {
            throw new IllegalStateException("Failed to encode resource filter section: " + error);
        });
    }

    private static Optional<JsonElement> encodeOverlayMetadataSection(OverlayMetadataSection section) {
        var encoded = OverlayMetadataSection.TYPE.codec().encodeStart(
                JsonOps.INSTANCE,
                section
        );
        return encoded.resultOrPartial(error -> {
            throw new IllegalStateException("Failed to encode overlays metadata section: " + error);
        });
    }

    private static Optional<JsonElement> encodeLanguagesSection(Map<String, LanguageInfo> languages) {
        if (languages.isEmpty()) {
            return Optional.empty();
        }

        UnboundedMapCodec<String, LanguageInfo> codec = Codec.unboundedMap(Codec.STRING, LanguageInfo.CODEC);

        var encoded = codec.encodeStart(JsonOps.INSTANCE, languages);
        return encoded.resultOrPartial(error -> {
            throw new IllegalStateException("Failed to encode languages section: " + error);
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InMemoryPackMetadataAsset that)) return false;
        return Objects.equals(this.key(), that.key()) &&
                type == that.type &&
                Objects.equals(metadata, that.metadata) &&
                Objects.equals(features, that.features) &&
                Objects.equals(filters, that.filters) &&
                Objects.equals(overlays, that.overlays) &&
                Objects.equals(languages, that.languages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key(), type, metadata, features, filters, overlays, languages);
    }

    @Override
    public String toString() {
        return "InMemoryPackMetadataAsset[" +
                "key=" + key() +
                ", type=" + type +
                ", metadata=" + metadata +
                ", features=" + features +
                ", filters=" + filters +
                ", overlays=" + overlays +
                ", languages=" + languages +
                ']';
    }
}