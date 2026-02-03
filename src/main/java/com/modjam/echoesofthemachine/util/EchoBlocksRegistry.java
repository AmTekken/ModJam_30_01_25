package com.modjam.echoesofthemachine.util;

import com.hypixel.hytale.protocol.Vector3i;

import java.util.HashSet;
import java.util.Set;

public class EchoBlocksRegistry {
    private static EchoBlocksRegistry INSTANCE;

    private static final String ECHO_BLOCK_ID = "Echo_Path_Block";

    private final Set<Vector3i> echoBlocksRegistry = new HashSet<>();
    public static EchoBlocksRegistry get() {
        if (INSTANCE == null) INSTANCE = new EchoBlocksRegistry();

        return INSTANCE;
    }

    public Set<Vector3i> getEchoBlocksRegistry() {
        return echoBlocksRegistry;
    }

    public void register(Vector3i echoBlockPosition) {
        echoBlocksRegistry.add(echoBlockPosition);
    }

    public void remove(Vector3i echoBlockPosition) {
        echoBlocksRegistry.remove(echoBlockPosition);
    }

}
