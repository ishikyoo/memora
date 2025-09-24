package com.ishikyoo.memora.internal;

import net.fabricmc.api.ModInitializer;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class Memora implements ModInitializer {

	public static final InMemoryPackRepository repository = new InMemoryPackRepository();

	@Override
	public void onInitialize() {

	}
}