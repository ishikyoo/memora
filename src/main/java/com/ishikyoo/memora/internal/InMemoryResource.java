package com.ishikyoo.memora.internal;

import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@ApiStatus.Internal
public class InMemoryResource extends Resource {

    @ApiStatus.Internal
    public static class IoSupplier implements net.minecraft.server.packs.resources.IoSupplier<InputStream> {

        private final byte @NotNull [] buffer;

        public IoSupplier(byte @NotNull [] buffer) {
            this.buffer = Objects.requireNonNull(buffer);
        }

        @Override
        public @NotNull InputStream get() {
            return new ByteArrayInputStream(buffer);
        }

    }

    @ApiStatus.Internal
    public static class IoSupplierMetadata implements net.minecraft.server.packs.resources.IoSupplier<ResourceMetadata> {

        private final byte @NotNull [] buffer;

        public IoSupplierMetadata(byte @NotNull [] buffer) {
            this.buffer = Objects.requireNonNull(buffer);
        }

        @Override
        public @NotNull ResourceMetadata get() throws IOException {
            InputStream stream = new ByteArrayInputStream(buffer);
            return ResourceMetadata.fromJsonStream(stream);
        }

    }

    private InMemoryResource(@NotNull PackResources resources,
                             @NotNull net.minecraft.server.packs.resources.IoSupplier<InputStream> stream,
                             @NotNull net.minecraft.server.packs.resources.IoSupplier<ResourceMetadata> metadata) {
        super(resources, stream, metadata);
    }

    public static InMemoryResource of(byte @NotNull [] data, InMemoryPackResources resources) {
        net.minecraft.server.packs.resources.IoSupplier<InputStream> stream = new InMemoryResource.IoSupplier(data);
        net.minecraft.server.packs.resources.IoSupplier<ResourceMetadata> metadata = new InMemoryResource.IoSupplierMetadata(data);
        return new InMemoryResource(resources, stream, metadata
        );
    }

}