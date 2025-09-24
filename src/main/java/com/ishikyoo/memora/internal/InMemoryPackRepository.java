package com.ishikyoo.memora.internal;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@ApiStatus.Internal
public final class InMemoryPackRepository {

    private final @NotNull Map<String, InMemoryPack> available = new HashMap<>();
    private @NotNull Map<String, InMemoryPack> selected = new LinkedHashMap<>();

    public @NotNull Optional<InMemoryPack> addPack(@NotNull InMemoryPack pack) {
        String id = pack.location().id();
        if (available.containsKey(id)) {
            return Optional.empty();
        }
        InMemoryPack added = available.put(id, pack);
        return Optional.ofNullable(added);
    }

    public @NotNull Optional<InMemoryPack> removePack(@NotNull String id) {
        InMemoryPack removed = available.remove(id);
        return Optional.ofNullable(removed);
    }

    public @NotNull Optional<InMemoryPack> removePack(@NotNull InMemoryPack pack) {
        return removePack(pack.location().id());
    }

    public @NotNull Optional<InMemoryPack> getPack(@NotNull String id) {
        return Optional.ofNullable(available.get(id));
    }

    public boolean containsPack(@NotNull String id) {
        return available.containsKey(id);
    }

    public @NotNull Collection<InMemoryPack> getAvailablePacks() {
        return Collections.unmodifiableCollection(available.values());
    }

    public @NotNull Collection<String> getAvailableIds() {
        return Collections.unmodifiableCollection(available.keySet());
    }

    public @NotNull Collection<InMemoryPack> getSelectedPacks() {
        return Collections.unmodifiableCollection(selected.values());
    }

    public @NotNull Collection<String> getSelectedIds() {
        return Collections.unmodifiableCollection(selected.keySet());
    }

    public void setSelected(@NotNull Collection<String> availableBasePacks) {
        Map<String, InMemoryPack> selected = new LinkedHashMap<>();
        for (String packId : availableBasePacks) {
            InMemoryPack pack = available.get(packId);
            if (pack == null) continue;
            selected.put(packId, pack);
        }
        this.selected = selected;
    }

}
