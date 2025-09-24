package com.ishikyoo.memora.internal;

import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApiStatus.Internal
public final class ModContext {
    private ModContext() { }

    public static final String MOD_NAME = "Memora";
    public static final String MOD_ID = "memora";
    public static final String GAME_ID = "minecraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

}