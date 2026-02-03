package com.modjam.echoesofthemachine.component;

import com.modjam.echoesofthemachine.EchoesOfTheMachinePlugin;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;


public class EchoState implements Component<EntityStore> {

    public static final int ECHO_DURATION = 5;
    public static final int ECHO_CHANNELING_DURATION = 1;

    public static final BuilderCodec<EchoState> CODEC = BuilderCodec.builder(EchoState.class, EchoState::new)
            .build();

    public static ComponentType<EntityStore, EchoState> getComponentType() {
        return EchoesOfTheMachinePlugin.get().getEchoStateComponentType();
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        EchoState data = new EchoState();

        return data;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> cloneSerializable() {
        return Component.super.cloneSerializable();
    }
}
