package com.ishikyoo.memora.api.asset;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public abstract class InMemoryAsset {

    protected final @NotNull Key key;
    private @Nullable ResourceLocation location;
    protected byte @Nullable [] bytes;

    protected InMemoryAsset(@NotNull Key key, byte @Nullable [] bytes) {
        this.key = key;
        this.bytes = bytes;
    }

    public @NotNull Key key() {
        return key;
    }

    public @NotNull ResourceLocation location() {
        if (location == null) {
            location = key.location();
        }
        return location;
    }

    public byte @NotNull [] bytes() {
        try {
            if (bytes == null) {
                bytes = toBytes();
            }
            return bytes;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to convert asset to bytes: type=" + key(), e
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InMemoryAsset that)) return false;
        return key.equals(that.key) &&
                Arrays.equals(bytes, that.bytes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(key);
        result = 31 * result + Arrays.hashCode(bytes);
        return result;
    }

    @Override
    public String toString() {
        return "InMemoryAsset{" +
                "key=" + key +
                ", bytes=" + (bytes == null ? "null" : (bytes.length + " bytes")) +
                '}';
    }

    protected abstract byte @NotNull [] toBytes();

    public static class Key {

        public static final String PATH_SEPARATOR = "/";

        public static final String SUFFIX_SEPARATOR = ".";

        @NotNull String namespace;
        @NotNull String prefix;
        @NotNull String name;
        @NotNull String suffix;

        private Key(@NotNull String namespace, @NotNull String prefix,
                    @NotNull String name, @NotNull String suffix) {
            this.namespace = namespace;
            this.prefix = prefix;
            this.name = name;
            this.suffix = suffix;
        }

        public @NotNull ResourceLocation location() {
            return ResourceLocation.fromNamespaceAndPath(
                    namespace,
                    (prefix.isEmpty() ? "" : prefix + PATH_SEPARATOR) + String.join(SUFFIX_SEPARATOR, name, suffix)
            );
        }

        public static @NotNull Key of(@NotNull String namespace, @NotNull String prefix,
                                      @NotNull String name, @NotNull String suffix) {
            assertValidNamespace(namespace);
            assertValidPrefix(prefix);
            assertValidName(name);
            assertValidSuffix(suffix);
            return new Key(namespace, prefix, name, suffix);
        }

        public static @NotNull Key of(@NotNull ResourceLocation location) {
            String path = location.getPath();

            int lastSlash = path.lastIndexOf(PATH_SEPARATOR);
            String prefix = lastSlash == -1 ? "" : path.substring(0, lastSlash);

            String filename = lastSlash == -1 ? path : path.substring(lastSlash + 1);

            int lastDot = filename.lastIndexOf(SUFFIX_SEPARATOR);

            String name = filename.substring(0, lastDot);
            String suffix = filename.substring(lastDot + 1);

            return new Key(location.getNamespace(), prefix, name, suffix);
        }

        public @NotNull Key withNamespace(@NotNull String namespace) {
            return new Key(assertValidNamespace(namespace), prefix, name, suffix);
        }

        public @NotNull Key withPrefix(@NotNull String prefix) {
            return new Key(namespace, assertValidPrefix(prefix), name, suffix);
        }

        public @NotNull Key withName(@NotNull String name) {
            return new Key(namespace, prefix, assertValidName(name), suffix);
        }

        public @NotNull Key withSuffix(@NotNull String suffix) {
            return new Key(namespace, prefix, name, assertValidSuffix(suffix));
        }

        public @NotNull String namespace() {
            return namespace;
        }

        public @NotNull String prefix() {
            return prefix;
        }

        public @NotNull String name() {
            return name;
        }

        public @NotNull String suffix() {
            return suffix;
        }

        public static boolean isValidNamespace(@NotNull String namespace) {
            return ResourceLocation.isValidNamespace(namespace);
        }

        public static boolean isValidName(@NotNull String name) {
            return ResourceLocation.isValidNamespace(name);
        }

        public static boolean isValidPrefix(@NotNull String prefix) {
            return ResourceLocation.isValidPath(prefix);
        }

        public static boolean isValidSuffix(@NotNull String suffix) {
            return ResourceLocation.isValidNamespace(suffix);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key key)) return false;
            return namespace.equals(key.namespace) &&
                    prefix.equals(key.prefix) &&
                    name.equals(key.name) &&
                    suffix.equals(key.suffix);
        }

        @Override
        public int hashCode() {
            return Objects.hash(namespace, prefix, name, suffix);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(namespace).append(':');
            if (!prefix.isEmpty()) {
                sb.append(prefix).append(PATH_SEPARATOR);
            }
            sb.append(name).append(SUFFIX_SEPARATOR).append(suffix);
            return sb.toString();
        }

        private static @NotNull String assertValidNamespace(@NotNull String namespace) {
            if (!isValidNamespace(namespace)) {
                throw new IllegalArgumentException(
                        "In-memory asset key namespace must be [a-z0-9._-], but was: " + namespace
                );
            }
            return namespace;
        }

        private static @NotNull String assertValidName(@NotNull String name) {
            if (!isValidName(name)) {
                throw new IllegalArgumentException(
                        "In-memory asset key name must be [a-z0-9._-], but was: " + name
                );
            }
            return name;
        }

        private static @NotNull String assertValidPrefix(@NotNull String prefix) {
            if (!isValidPrefix(prefix)) {
                throw new IllegalArgumentException(
                        "In-memory asset key prefix must be path-like ([a-z0-9._-]/...), but was: " + prefix
                );
            }
            return prefix;
        }

        private static @NotNull String assertValidSuffix(@NotNull String suffix) {
            if (!isValidSuffix(suffix)) {
                throw new IllegalArgumentException(
                        "In-Memory asset key suffix must be [a-z0-9._-], but was: " + suffix
                );
            }
            return suffix;
        }
    }
}