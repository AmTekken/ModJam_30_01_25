package com.modjam.echoesofthemachine.interactions;

import com.modjam.echoesofthemachine.component.EchoState;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;

import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ActivateEchoState extends SimpleInstantInteraction {
    public static final BuilderCodec<ActivateEchoState> CODEC = BuilderCodec.builder(ActivateEchoState.class, ActivateEchoState::new).build();

    @Override
    public void handle(@NonNullDecl Ref<EntityStore> ref, boolean firstRun, float time, @NonNullDecl InteractionType type, @NonNullDecl InteractionContext interactionContext) {
        super.handle(ref, firstRun, time, type, interactionContext);
        var store = ref.getStore();
        World world = store.getExternalData().getWorld();

        world.execute(() -> {

            EchoState echoState = store.getComponent(ref, EchoState.getComponentType());
            if(echoState != null) {
                return;}
            store.addComponent(ref, EchoState.getComponentType(), new EchoState());
        });
    }

    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NonNullDecl CooldownHandler cooldownHandler) {

    }
}
