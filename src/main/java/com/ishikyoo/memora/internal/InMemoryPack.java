package com.ishikyoo.memora.internal;

import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public final class InMemoryPack {

    private static final Logger LOGGER = ModContext.LOGGER;

    private final @NotNull PackType type;
    private final @NotNull PackLocationInfo location;
    private final @NotNull InMemoryPackResources resources;
    private final @NotNull InMemoryPackResources.Supplier supplier;
    private final @NotNull PackSelectionConfig config;
    private @Nullable Pack base;

    public InMemoryPack(@NotNull PackType type, @NotNull PackLocationInfo location,
                        @NotNull PackSelectionConfig config) {
        this.type = type;
        this.location = location;
        this.resources = new InMemoryPackResources(type, location);
        supplier = new InMemoryPackResources.Supplier(resources);
        this.config = config;
    }

    public void create() {
        if (base != null) {
            LOGGER.warn("Trying to create a already created In-Memory Pack: {}", resources.location().id());
        }

        int format = SharedConstants.getCurrentVersion().packVersion(type);
        Pack.Metadata resourcesMetadata = Pack.readPackMetadata(resources.location(), supplier, format);
        Pack.Metadata metadata = Objects.requireNonNullElseGet(resourcesMetadata, InMemoryPack::createDefaultMetadata);

        base = new Pack(resources.location(), supplier, metadata, config);
    }

    public Optional<InMemoryResource> addResource(@NotNull ResourceLocation location, byte @NotNull [] buffer) {
        return resources.addResource(location, buffer);
    }

    public Optional<InMemoryResource> removeResource(@NotNull ResourceLocation location) {
        return resources.removeResource(location);
    }

    public boolean containsResource(@NotNull ResourceLocation location) {
        return resources.containsResource(location);
    }

    public @NotNull PackType type() {
        return type;
    }

    public @NotNull PackLocationInfo location() {
        return location;
    }

    public @NotNull Optional<Pack> base() {
        return Optional.ofNullable(base);
    }

    public @NotNull InMemoryPackResources open() {
        return supplier.openFull(resources.location(), null);
    }

    private static Pack.Metadata createDefaultMetadata() {
        return new Pack.Metadata(
                Component.literal("The in-memory pack of Memora"),
                PackCompatibility.COMPATIBLE,
                null,
                new ArrayList<>()
        );
    }

}