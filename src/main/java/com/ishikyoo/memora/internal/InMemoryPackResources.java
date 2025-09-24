package com.ishikyoo.memora.internal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@ApiStatus.Internal
public class InMemoryPackResources implements PackResources {

    @ApiStatus.Internal
    public static class Supplier implements Pack.ResourcesSupplier {

        private final @NotNull InMemoryPackResources resources;

        public Supplier(@NotNull InMemoryPackResources resources) {
            this.resources = resources;
        }

        @Override
        public @NotNull InMemoryPackResources openPrimary(PackLocationInfo packLocationInfo) {
            return resources;
        }

        @Override
        public @NotNull InMemoryPackResources openFull(PackLocationInfo packLocationInfo, Pack.Metadata metadata) {
            return resources;
        }

    }

    private static final Logger LOGGER = ModContext.LOGGER;

    public static final String ROOT_NAMESPACE = "root";
    public static final ResourceLocation ROOT_LOCATION = ResourceLocation.fromNamespaceAndPath(ROOT_NAMESPACE, "");
    public static final ResourceLocation PACK_META_LOCATION = ROOT_LOCATION.withPath(PACK_META);

    private final @NotNull PackType type;
    private final @NotNull PackLocationInfo location;
    private final @NotNull Map<String, Integer> namespaces = new HashMap<>();
    private final @NotNull Map<ResourceLocation, InMemoryResource> resources = new HashMap<>();

    public InMemoryPackResources(@NotNull PackType type, @NotNull PackLocationInfo location) {
        this.type = type;
        this.location = location;
    }

    public @NotNull Optional<InMemoryResource> addResource(@NotNull ResourceLocation location, byte @NotNull [] buffer) {
        if (resources.containsKey(location)) {
            LOGGER.warn("Trying to add an already present in-memory resource to the pack resources: {}", location);
            return Optional.empty();
        }
        InMemoryResource resource = InMemoryResource.of(buffer, this);
        resources.put(location, resource);
        addNamespace(location.getNamespace());
        return Optional.of(resource);
    }

    public @NotNull Optional<InMemoryResource> removeResource(@NotNull ResourceLocation location) {
        InMemoryResource resource = resources.remove(location);
        if (resource == null) {
            LOGGER.warn("Trying to remove a not present in-memory resource from the pack resources: {}", location);
            return Optional.empty();
        }
        String namespace = location.getNamespace();
        removeNamespace(namespace);
        return Optional.of(resource);
    }

    public @NotNull Optional<InMemoryResource> getResource(@NotNull ResourceLocation location) {
        InMemoryResource resource = resources.get(location);
        return Optional.ofNullable(resource);
    }

    public @NotNull Optional<InMemoryResource> getMetadataResource() {
        return getResource(PACK_META_LOCATION);
    }

    public boolean containsResource(ResourceLocation location) {
        return resources.containsKey(location);
    }

    public boolean containsMetadata() {
        return containsResource(PACK_META_LOCATION);
    }

    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String... strings) {
        String path = String.join("/", strings);
        InMemoryResource resource = resources.get(ROOT_LOCATION.withPath(path));
        return resource == null ? null : resource::open;
    }

    @Override
    public @Nullable IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
        if (!type.equals(this.type)) return null;
        InMemoryResource resource = resources.get(location);
        return resource == null ? null : resource::open;
    }

    @Override
    public void listResources(PackType type, String namespace, String path, ResourceOutput output) {
        if (!type.equals(this.type)) return;
        String prefix = path.isEmpty() ? "" : path + "/";
        resources.forEach((key, resource) -> {
            if (!key.getNamespace().equals(namespace)) return;
            if (!key.getPath().startsWith(prefix)) return;
            String remaining = key.getPath().substring(prefix.length());
            if (remaining.contains("/")) return;
            output.accept(key, resource::open);
        });
    }

    @Override
    public @NotNull Set<String> getNamespaces(PackType type) {
        return Collections.unmodifiableSet(namespaces.keySet());
    }


    @Override
    public @NotNull PackLocationInfo location() {
        return location;
    }

    @Override
    public @NotNull String packId() {
        return location.id();
    }

    @Override
    public @Nullable <T> T getMetadataSection(MetadataSectionType<T> metadataSectionType) throws IOException {
        InMemoryResource resource = resources.get(PACK_META_LOCATION);
        if (resource == null) return null;
        ResourceMetadata metadata = resource.metadata();
        Optional<T> section = metadata.getSection(metadataSectionType);
        return section.orElse(null);
    }

    @Override
    public void close() {

    }

    public int getResourcesCount() {
        return resources.size();
    }

    public int getResourcesCount(@NotNull String namespace) {
        if (namespaces.containsKey(namespace)) return 0;
        return namespaces.get(namespace);
    }

    public int getRootResourcesCount() {
        return getResourcesCount(ROOT_NAMESPACE);
    }

    private void addNamespace(@NotNull String namespace) {
        if (!namespaces.containsKey(namespace)) {
            namespaces.put(namespace, 1);
        } else {
            int count = namespaces.get(namespace);
            namespaces.replace(namespace, count + 1);
        }
    }

    private void removeNamespace(@NotNull String namespace) {
        if (!namespaces.containsKey(namespace)) return;
        int count = namespaces.get(namespace);
        if (count > 1) {
            namespaces.replace(namespace, count - 1);
        } else {
            namespaces.remove(namespace);
        }
    }
}