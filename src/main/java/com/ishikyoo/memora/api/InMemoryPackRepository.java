package com.ishikyoo.memora.api;

import com.ishikyoo.memora.internal.Memora;
import com.ishikyoo.memora.internal.ModContext;
import com.ishikyoo.memora.api.asset.InMemoryAsset;
import net.minecraft.server.packs.PackLocationInfo;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.*;

public final class InMemoryPackRepository {

    private static final Logger LOGGER = ModContext.LOGGER;

    private final Map<String, InMemoryPack> packs = new HashMap<>();

    public @NotNull Optional<InMemoryPack> addPack(@NotNull InMemoryPack pack) {
        Objects.requireNonNull(pack);

        if (packs.containsKey(pack.metadata().id())) {
            LOGGER.warn("In-memory pack already exists in repository: {}", pack.metadata().id());
            return Optional.empty();
        }

        packs.put(pack.metadata().id(), pack);

        com.ishikyoo.memora.internal.InMemoryPack internal = new com.ishikyoo.memora.internal.InMemoryPack(
                pack.metadata().type(),
                new PackLocationInfo(
                        pack.metadata().id(),
                        pack.metadata().title(),
                        pack.metadata().source(),
                        Optional.empty() ),
                pack.metadata().config()
        );

        for (InMemoryAsset asset : pack.assets()) {
            byte[] buffer = asset.bytes();
            internal.addResource(asset.location(), buffer);
        }

        internal.create();

        Memora.repository.addPack(internal);

        return Optional.of(pack);
    }

    public @NotNull Optional<InMemoryPack> removePack(@NotNull String id) {
        Objects.requireNonNull(id);

        InMemoryPack removed = packs.remove(id);
        if (removed == null) {
            LOGGER.warn("Attempted to remove non-existent in-memory pack: {}", id);
            return Optional.empty();
        }

        Memora.repository.removePack(id);

        return Optional.of(removed);
    }

    public @NotNull Optional<InMemoryPack> getPack(@NotNull String id) {
        Objects.requireNonNull(id);
        return Optional.ofNullable(packs.get(id));
    }

    public @NotNull Collection<InMemoryPack> getPacks() {
        return Collections.unmodifiableCollection(packs.values());
    }
}