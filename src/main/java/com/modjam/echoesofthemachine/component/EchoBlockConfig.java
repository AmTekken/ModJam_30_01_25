package com.modjam.echoesofthemachine.component;

import com.hypixel.hytale.builtin.portals.utils.BlockTypeUtils;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;

public class EchoBlockConfig {
    public static final BuilderCodec<EchoBlockConfig> CODEC = BuilderCodec.builder(EchoBlockConfig.class, EchoBlockConfig::new)
            .<String>appendInherited(
                    new KeyedCodec<>("OnState", Codec.STRING),
                    (config, o) -> config.onState = o,
                    config -> config.onState,
                    (config, parent) -> config.onState = parent.onState
            )
            .documentation("The StateData when the player is in echo state.")
            .add()
            .<String>appendInherited(
                    new KeyedCodec<>("OffState", Codec.STRING),
                    (config, o) -> config.offState = o,
                    config -> config.offState,
                    (config, parent) -> config.offState = parent.offState
            )
            .documentation("The StateData when player is not in echo state.")
            .add()
            .build();

    private String onState = "On";
    private String offState = "default";

    public EchoBlockConfig() {
    }

    public String getOnState() {
        return this.onState;
    }

    public String getOffState() {
        return this.offState;
    }

    public String[] getBlockStates() {
        return new String[]{this.onState, this.offState};
    }

    public boolean areBlockStatesValid(BlockType baseBlockType) {
        for (String stateKey : this.getBlockStates()) {
            BlockType state = BlockTypeUtils.getBlockForState(baseBlockType, stateKey);
            if (state == null) {
                return false;
            }
        }

        return true;
    }
}
