package com.ishikyoo.memora.mixin;

import com.google.common.collect.ImmutableMap;
import com.ishikyoo.memora.internal.Memora;
import com.ishikyoo.memora.internal.InMemoryPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Mixin(PackRepository.class)
public class PackRepositoryMixin {

	@Inject(method = "discoverAvailable", at = @At("RETURN"), cancellable = true)
	private void addInMemoryPack(CallbackInfoReturnable<Map<String, Pack>> cir) {
		Map<String, Pack> original = cir.getReturnValue();

		Map<String, Pack> map = new TreeMap<>(original);

		for (InMemoryPack pack : Memora.repository.getAvailablePacks()) {
			Optional<Pack> optional = pack.base();
			if (optional.isEmpty()) continue;
			Pack mcPack = optional.get();
			map.put(mcPack.getId(), mcPack);
		}

		cir.setReturnValue(ImmutableMap.copyOf(map));
	}

	@Inject(method = "setSelected", at = @At("TAIL"))
	private void setSelectedInMemoryPacks(Collection<String> collection, CallbackInfo ci) {
		Memora.repository.setSelected(collection);
	}
}