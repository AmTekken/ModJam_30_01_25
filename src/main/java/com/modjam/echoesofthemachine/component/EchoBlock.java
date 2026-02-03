package com.modjam.echoesofthemachine.component;

import com.modjam.echoesofthemachine.EchoesOfTheMachinePlugin;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

public class EchoBlock implements Component<ChunkStore> {
    public static final BuilderCodec<EchoBlock> CODEC = BuilderCodec.builder(EchoBlock.class, EchoBlock::new)
            .append(new KeyedCodec<>("Config", EchoBlockConfig.CODEC), (echoBlock, o) -> echoBlock.config = o, echoBlock -> echoBlock.config)
            .add()
            .append(new KeyedCodec<>("BaseBlockType", Codec.STRING), (echoBlock, o) -> echoBlock.baseBlockTypeKey = o, echoBlock -> echoBlock.baseBlockTypeKey)
            .add()
            .build();

    private EchoBlockConfig config;
    private String baseBlockTypeKey;

    public static ComponentType<ChunkStore, EchoBlock> getComponentType() {
        return EchoesOfTheMachinePlugin.get().getEchoBlockComponentType();
    }

    private EchoBlock() {
    }

    public EchoBlock(EchoBlockConfig config, String baseBlockTypeKey) {
        this.config = config;
        this.baseBlockTypeKey = baseBlockTypeKey;
    }

    public EchoBlockConfig getConfig() { return this.config; }

    public String getBaseBlockTypeKey() {
        return this.baseBlockTypeKey;
    }

    public BlockType getBaseBlockType() {
        return BlockType.getAssetMap().getAsset(this.baseBlockTypeKey);
    }

    @Override
    public Component<ChunkStore> clone() {
        EchoBlock echoBlock = new EchoBlock();
        echoBlock.config = this.config;
        echoBlock.baseBlockTypeKey = this.baseBlockTypeKey;

        return echoBlock;
    }
}
