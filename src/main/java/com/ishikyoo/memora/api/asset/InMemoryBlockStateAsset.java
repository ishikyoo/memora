package com.ishikyoo.memora.api.asset;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public final class InMemoryBlockStateAsset extends InMemoryJSONAsset {

    private final @NotNull Map<String, Variant> variants;
    private final @NotNull Set<Multipart> multipart;

    private InMemoryBlockStateAsset(@NotNull InMemoryAsset.Key key,
                                    @NotNull Map<String, Variant> variants,
                                    @NotNull Set<Multipart> multipart) {
        super(key);
        this.variants = Collections.unmodifiableMap(variants);
        this.multipart = Collections.unmodifiableSet(multipart);
    }

    public static @NotNull InMemoryBlockStateAsset of(@NotNull ResourceLocation location) {
        Objects.requireNonNull(location);
        return of(location.getNamespace(), location.getPath());
    }

    public static @NotNull InMemoryBlockStateAsset of(@NotNull String namespace, @NotNull String name) {
        InMemoryAsset.Key key = Key.of(Objects.requireNonNull(namespace), "blockstates", Objects.requireNonNull(name), "json");
        return new InMemoryBlockStateAsset(key, new LinkedHashMap<>(), new LinkedHashSet<>());
    }

    public @NotNull InMemoryBlockStateAsset withVariant(@NotNull String key, @NotNull Variant variant) {
        if (variants.containsKey(Objects.requireNonNull(key)) && variants.containsValue(Objects.requireNonNull(variant))) return this;
        Map<String, Variant> copy = new LinkedHashMap<>(variants);
        copy.put(key, variant);
        return new InMemoryBlockStateAsset(this.key(), copy, new LinkedHashSet<>());
    }

    public @NotNull InMemoryBlockStateAsset withMultipart(@NotNull Multipart multipart) {
        if (this.multipart.contains(multipart)) return this;
        Set<Multipart> copy = new LinkedHashSet<>(this.multipart);
        copy.add(multipart);
        return new InMemoryBlockStateAsset(this.key(), new LinkedHashMap<>(), copy);
    }

    @Override
    protected @NotNull JsonElement toJson() {
        JsonObject root = new JsonObject();

        if (!variants.isEmpty()) {
            JsonObject v = new JsonObject();
            variants.forEach((k, variant) -> v.add(k, variant.toJson()));
            root.add("variants", v);
        }

        if (!multipart.isEmpty()) {
            JsonArray parts = new JsonArray();
            multipart.forEach(m -> parts.add(m.toJson()));
            root.add("multipart", parts);
        }

        return root;
    }

    public static class Variant {
        private final @NotNull Set<Model> models;

        private Variant(@NotNull Set<Model> models) {
            this.models = Collections.unmodifiableSet(new LinkedHashSet<>(models));
        }

        public static Variant of(@NotNull Model model) {
            Objects.requireNonNull(model);
            return new Variant(Collections.singleton(model));
        }

        public static Variant of(@NotNull Collection<Model> models) {
            Objects.requireNonNull(models);
            return new Variant(new LinkedHashSet<>(models));
        }

        JsonElement toJson() {
            if (models.size() == 1) {
                return models.iterator().next().toJson();
            }
            JsonArray arr = new JsonArray();
            models.forEach(m -> arr.add(m.toJson()));
            return arr;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Variant v)) return false;
            return models.equals(v.models);
        }

        @Override
        public int hashCode() {
            return Objects.hash(models);
        }

        @Override
        public String toString() {
            return "Variant" + models;
        }
    }

    public static class Model {
        private final @NotNull ResourceLocation path;
        private final int x, y, weight;
        private final boolean uvlock;

        private Model(@NotNull ResourceLocation path, int x, int y, boolean uvlock, int weight) {
            if (weight < 1) throw new IllegalArgumentException("Weight must be >= 1");
            this.path = path;
            this.x = x;
            this.y = y;
            this.uvlock = uvlock;
            this.weight = weight;
        }

        public static @NotNull Model of(@NotNull ResourceLocation path) {
            return new Model(Objects.requireNonNull(path), 0, 0, false, 1);
        }

        public @NotNull Model withPath(@NotNull ResourceLocation path) {
            if (Objects.requireNonNull(path).equals(this.path)) return this;
            return new Model(path, x, y, uvlock, weight);
        }

        public @NotNull Model withX(int degrees) {
            int clampDegrees = clampDegrees(degrees);
            if (clampDegrees == this.x) return this;
            return new Model(path, clampDegrees, y, uvlock, weight);
        }

        public @NotNull Model withY(int degrees) {
            int clampDegrees = clampDegrees(degrees);
            if (clampDegrees == this.y) return this;
            return new Model(path, x, clampDegrees, uvlock, weight);
        }

        public @NotNull Model withUVLock(boolean uvlock) {
            if (uvlock == this.uvlock) return this;
            return new Model(path, x, y, uvlock, weight);
        }

        public @NotNull Model withWeight(int weight) {
            if (weight == this.weight) return this;
            return new Model(path, x, y, uvlock, weight);
        }

        public @NotNull ResourceLocation path() { return path; }
        public int x() { return x; }
        public int y() { return y; }
        public int weight() { return weight; }
        public boolean uvlock() { return uvlock; }

        JsonElement toJson() {
            JsonObject o = new JsonObject();
            o.addProperty("model", path.toString());
            if (x != 0) o.addProperty("x", x);
            if (y != 0) o.addProperty("y", y);
            if (uvlock) o.addProperty("uvlock", true);
            if (weight != 1) o.addProperty("weight", weight);
            return o;
        }

        private static int clampDegrees(int degrees) {
            int normalized = ((degrees % 360) + 360) % 360;
            return (Math.round(normalized / 90f) * 90) % 360;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Model model)) return false;
            return x == model.x && y == model.y && weight == model.weight &&
                    uvlock == model.uvlock && path.equals(model.path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path, x, y, uvlock, weight);
        }

        @Override
        public String toString() {
            return "Model[" + path + ", x=" + x + ", y=" + y +
                    ", uvlock=" + uvlock + ", weight=" + weight + "]";
        }
    }

    public static class Multipart {
        private final @Nullable Condition when;
        private final @NotNull Set<Model> apply;

        private Multipart(@Nullable Condition when, @NotNull Set<Model> apply) {
            this.when = when;
            this.apply = Collections.unmodifiableSet(apply);
        }

        public static @NotNull Multipart of(@Nullable Condition when, @NotNull Model apply) {
            return new Multipart(when, Collections.singleton(Objects.requireNonNull(apply)));
        }

        public static @NotNull Multipart of(@Nullable Condition when, @NotNull Collection<Model> apply) {
            Objects.requireNonNull(apply);
            return new Multipart(when, new LinkedHashSet<>(apply));
        }

        public @NotNull Multipart withWhen(@Nullable Condition when) {
            if (Objects.equals(when, this.when)) return this;
            return new Multipart(when, apply);
        }

        public @NotNull Multipart withApply(@NotNull Model model) {
            Objects.requireNonNull(model);
            if (apply.size() == 1 && apply.iterator().next().equals(model)) return this;
            return new Multipart(when, Collections.singleton(model));
        }

        public @NotNull Multipart withApply(@NotNull Collection<Model> models) {
            Objects.requireNonNull(models);
            if (new LinkedHashSet<>(models).equals(apply)) return this;
            return new Multipart(when, new LinkedHashSet<>(models));
        }

        public @NotNull Multipart withoutWhen() {
            if (this.when == null) return this;
            return new Multipart(null, apply);
        }

        JsonElement toJson() {
            JsonObject o = new JsonObject();
            if (when != null) o.add("when", when.toJson());
            if (apply.size() == 1) {
                o.add("apply", apply.iterator().next().toJson());
            } else {
                JsonArray arr = new JsonArray();
                apply.forEach(m -> arr.add(m.toJson()));
                o.add("apply", arr);
            }
            return o;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Multipart that)) return false;
            return Objects.equals(when, that.when) && apply.equals(that.apply);
        }

        @Override
        public int hashCode() {
            return Objects.hash(when, apply);
        }

        @Override
        public String toString() {
            return "Multipart[when=" + when + ", apply=" + apply + "]";
        }
    }

    public interface Condition {
        JsonElement toJson();
    }

    public static class PropertyCondition implements Condition {
        private final @NotNull String key;
        private final @NotNull Set<String> values;

        public PropertyCondition(@NotNull String key, @NotNull Set<String> values) {
            this.key = Objects.requireNonNull(key);
            this.values = Collections.unmodifiableSet(new LinkedHashSet<>(Objects.requireNonNull(values)));
        }

        @Override
        public JsonElement toJson() {
            JsonObject o = new JsonObject();
            if (values.size() == 1) {
                o.addProperty(key, values.iterator().next());
            } else {
                JsonArray arr = new JsonArray();
                values.forEach(arr::add);
                o.add(key, arr);
            }
            return o;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PropertyCondition that)) return false;
            return key.equals(that.key) && values.equals(that.values);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, values);
        }

        @Override
        public String toString() {
            return "PropertyCondition[" + key + "=" + values + "]";
        }
    }

    public static class CompositeCondition implements Condition {
        private final @NotNull String op; // "or" or "and"
        private final @NotNull Set<Condition> children;

        private CompositeCondition(@NotNull String op, @NotNull Set<Condition> children) {
            this.op = Objects.requireNonNull(op);
            this.children = Collections.unmodifiableSet(new LinkedHashSet<>(children));
        }

        public static CompositeCondition and(@NotNull Collection<Condition> children) {
            return new CompositeCondition("and", new LinkedHashSet<>(children));
        }

        public static CompositeCondition or(@NotNull Collection<Condition> children) {
            return new CompositeCondition("or", new LinkedHashSet<>(children));
        }

        @Override
        public JsonElement toJson() {
            JsonObject o = new JsonObject();
            JsonArray arr = new JsonArray();
            children.forEach(c -> arr.add(c.toJson()));
            o.add(op, arr);
            return o;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CompositeCondition that)) return false;
            return op.equals(that.op) && children.equals(that.children);
        }

        @Override
        public int hashCode() {
            return Objects.hash(op, children);
        }

        @Override
        public String toString() {
            return "CompositeCondition[" + op + " " + children + "]";
        }
    }
}