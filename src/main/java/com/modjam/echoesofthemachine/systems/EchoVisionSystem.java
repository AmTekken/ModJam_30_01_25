package com.modjam.echoesofthemachine.systems;

import com.modjam.echoesofthemachine.EchoesOfTheMachinePlugin;
import com.modjam.echoesofthemachine.component.EchoBlock;
import com.modjam.echoesofthemachine.component.EchoState;
import com.modjam.echoesofthemachine.util.InventoryManager;
import com.modjam.echoesofthemachine.util.ModelHelper;
import com.hypixel.hytale.builtin.weather.resources.WeatherResource;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.protocol.packets.camera.SetServerCamera;
import com.hypixel.hytale.protocol.packets.world.UpdateEditorWeatherOverride;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAttachment;
import com.hypixel.hytale.server.core.entity.Frozen;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.container.DelegateItemContainer;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterType;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.Interactable;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import it.unimi.dsi.fastutil.Pair;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class EchoVisionSystem extends RefChangeSystem<EntityStore, EchoState> {
    @Nonnull
    private final ComponentType<EntityStore, EchoState> echoStateComponentType = EchoState.getComponentType();

    @Nonnull
    private final Query<EntityStore> query = Query.and(this.echoStateComponentType);

    @NonNullDecl
    @Override
    public ComponentType<EntityStore, EchoState> componentType() {
        return this.echoStateComponentType;
    }

    @Override
    public void onComponentAdded(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl EchoState echoState, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        PlayerRef playerRef =  commandBuffer.getComponent(ref, PlayerRef.getComponentType());
        Player player =  commandBuffer.getComponent(ref, Player.getComponentType());
        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        WeatherResource weatherResource = store.getResource(WeatherResource.getResourceType());

        NPCPlugin npcPlugin = NPCPlugin.get();
        int index = npcPlugin.getIndex("Player_Role");

        if(transformComponent == null) {return;}

        World world = store.getExternalData().getWorld();
        CompletableFuture<Void> done = new CompletableFuture<>();

        //holding immutable data
        final Vector3d[] startPos = new Vector3d[1];
        final Vector3f[] startRot = new Vector3f[1];
        final Vector3f[] startHead = new Vector3f[1];

        final Vector3d[] endPos = new Vector3d[1];
        final Vector3f[] endRot = new Vector3f[1];
        final Vector3f[] endHead = new Vector3f[1];

        Inventory playerInventory = player.getInventory();
        byte[] originalInvData;

        try{
            originalInvData = InventoryManager.serializeInventory(player);
        } catch (IOException E){
            done.complete(null);
            return;
        }

        world.execute(() -> {
            if (!ref.isValid()) { done.complete(null); return; }

            PlayerRef pr = store.getComponent(ref, PlayerRef.getComponentType());
            Player p1 =  store.getComponent(ref, Player.getComponentType());
            TransformComponent tc = store.getComponent(ref, TransformComponent.getComponentType());
            Model model = store.getComponent(ref, ModelComponent.getComponentType()).getModel();
            HeadRotation hr = store.getComponent(ref, HeadRotation.getComponentType());


            if (tc == null || pr == null || p1 == null) { done.complete(null); return; }

            startPos[0] = tc.getPosition().clone();
            startRot[0] = tc.getRotation().clone();
            startHead[0] = hr != null ? hr.getRotation().clone() : startRot[0].clone();

            //preparing character models
            Model echoModel = new Model(playerRef.getUsername() + "_EchoModel", model.getScale(), model.getRandomAttachmentIds(),
                    EchoesOfTheMachinePlugin.get().getEchoStatePlayerModel(), model.getBoundingBox(), model.getModel(), model.getTexture(), "Skin", "42", model.getEyeHeight(), model.getCrouchOffset(), model.getAnimationSetMap(), model.getCamera(), model.getLight(), model.getParticles(), model.getTrails(), model.getPhysicsValues(), model.getDetailBoxes(), model.getPhobia(), model.getPhobiaModelAssetId());

            List<ModelAttachment> playerModelAttachements =new ArrayList<>();
            ModelHelper.restoreSkinWithOverrides(ref, playerModelAttachements);

            Model defaultModel = new Model(playerRef.getUsername() + "_Skinned", model.getScale(), model.getRandomAttachmentIds(), playerModelAttachements.toArray(new ModelAttachment[0]), model.getBoundingBox(), model.getModel(), model.getTexture(), playerModelAttachements.getFirst().getGradientSet(), playerModelAttachements.getFirst().getGradientId(), model.getEyeHeight(), model.getCrouchOffset(), model.getAnimationSetMap(), model.getCamera(), model.getLight(), model.getParticles(), model.getTrails(), model.getPhysicsValues(), model.getDetailBoxes(), model.getPhobia(), model.getPhobiaModelAssetId());

            toggleEchoBlockState(store, transformComponent);

            //saving playing inventory
            CombinedItemContainer savedInventory = InventoryManager.deserializeInventory(originalInvData);
            DelegateItemContainer<CombinedItemContainer> delegateItemContainer = new DelegateItemContainer(savedInventory);
            delegateItemContainer.setGlobalFilter(FilterType.DENY_ALL);

            store.replaceComponent(ref, ModelComponent.getComponentType(), new ModelComponent(echoModel));
            setEchoMovement(playerRef);

            Vector3d npcPoisition = new Vector3d();
            npcPoisition.assign(startPos[0].x, startPos[0].y, startPos[0].z);
            //spawning player copy
            Pair<Ref<EntityStore>, NPCEntity> npcPair = npcPlugin.spawnEntity(store, index, npcPoisition, startRot[0], defaultModel, null);
            NPCEntity body = npcPair.second();

            body.setInventory(new Inventory());

            savedInventory.forEach((slot, itemStack) -> {
                body.getInventory().getCombinedEverything().setItemStackForSlot(slot, itemStack, false);
            });

            body.getInventory().setActiveUtilitySlot(playerInventory.getActiveUtilitySlot());
            body.getInventory().setActiveHotbarSlot(playerInventory.getActiveHotbarSlot());

            playerInventory.clear();

            //stopping time for all npcs
            freezeAllNPCs(store);

            //setting weather
            weatherResource.setForcedWeather("Echo_State");
            playerRef.getPacketHandler().write(new UpdateEditorWeatherOverride(weatherResource.getForcedWeatherIndex()));

            playerRef.sendMessage(Message.raw(String.format("Echo started. Move for %s seconds.", EchoState.ECHO_DURATION)));

            CompletableFuture
                    .runAsync(() -> {}, CompletableFuture.delayedExecutor(EchoState.ECHO_DURATION, TimeUnit.SECONDS))
                    .thenRun(() -> world.execute(() -> {
                        if (!ref.isValid()) { done.complete(null); return; }

                        TransformComponent tc2 = store.getComponent(ref, TransformComponent.getComponentType());
                        Player p2 =  store.getComponent(ref, Player.getComponentType());
                        HeadRotation hr2 = store.getComponent(ref, HeadRotation.getComponentType());
                        if (tc2 == null || p2 == null) { done.complete(null); return; }

                        endPos[0] = tc2.getPosition().clone();
                        endRot[0] = tc2.getRotation().clone();
                        endHead[0] = hr2 != null ? hr2.getRotation().clone() : endRot[0].clone();

                        body.setDespawning(true);
                        store.addComponent(ref, Teleport.getComponentType(), new Teleport(startPos[0], startRot[0]));
                        if (hr2 != null) {
                            hr2.teleportRotation(startHead[0]);
                        }
                        PlayerRef pr2 = store.getComponent(ref, PlayerRef.getComponentType());

                        thirdPersonChannelingCamera(pr2, startHead[0]);

                        store.replaceComponent(ref, ModelComponent.getComponentType(), new ModelComponent(defaultModel));

                        restrictPlayerMovement(pr2);
                        savedInventory.forEach((slot, itemStack) -> {
                            p2.getInventory().getCombinedEverything().setItemStackForSlot(slot, itemStack, false);
                        });

                        playerRef.sendMessage(Message.raw(String.format("Rewound. Channeling... (%ds)", EchoState.ECHO_CHANNELING_DURATION)));

                        CompletableFuture
                                .runAsync(() -> {}, CompletableFuture.delayedExecutor(EchoState.ECHO_CHANNELING_DURATION, TimeUnit.SECONDS))
                                .thenRun(() -> world.execute(() -> {
                                    if (!ref.isValid()) { done.complete(null); return; }

                                    PlayerRef pr3 = store.getComponent(ref, PlayerRef.getComponentType());
                                    HeadRotation hr3 = store.getComponent(ref, HeadRotation.getComponentType());
                                    TransformComponent tc3 = store.getComponent(ref, TransformComponent.getComponentType());
                                    if (pr3 == null || tc3 == null) { done.complete(null); return; }

                                    store.addComponent(ref, Teleport.getComponentType(), new Teleport(endPos[0], endRot[0]));
                                    if (hr3 != null) {
                                        hr3.teleportRotation(endHead[0]);
                                    }
                                    resetCamera(pr3);
                                    resetPlayerMovement(ref, store, pr3);

                                    store.removeComponent(ref, EchoState.getComponentType());
                                    unFreezeAllNPCs(store);
                                    weatherResource.setForcedWeather(null);
                                    pr3.getPacketHandler().write(new UpdateEditorWeatherOverride(0));

                                    playerRef.sendMessage(Message.raw("Echo released."));
                                    done.complete(null);
                                }))
                                .exceptionally(ex -> { done.completeExceptionally(ex); return null; });
                    }))
                    .exceptionally(ex -> { done.completeExceptionally(ex); return null; });
        });
    }

    private static void freezeAllNPCs(@NonNullDecl Store<EntityStore> store) {
        store.forEachEntityParallel(
                NPCEntity.getComponentType(),
                (index, archetypeChunk, commandBuffer) -> {
                    NPCEntity npc = commandBuffer.getComponent(archetypeChunk.getReferenceTo(index), NPCEntity.getComponentType());
                    if(npc != null && npc.getRoleName().equals("Player_Role")) {
                        return;
                    }
                    commandBuffer.ensureComponent(archetypeChunk.getReferenceTo(index), Frozen.getComponentType());
                }
        );
        store.forEachEntityParallel(ItemComponent.getComponentType(), (index, archetypeChunk, commandBuffer) -> {
            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
            NPCEntity npc = commandBuffer.getComponent(ref, NPCEntity.getComponentType());
            if(npc != null && npc.getRoleName().equals("Player_Role")) {
                return;
            }
            commandBuffer.ensureComponent(ref, Frozen.getComponentType());
            commandBuffer.ensureComponent(ref, Interactable.getComponentType());
        });
    }

    private static void unFreezeAllNPCs(@NonNullDecl Store<EntityStore> store) {
        store.forEachEntityParallel(
                NPCEntity.getComponentType(),
                (index, archetypeChunk, commandBuffer) -> {
                    commandBuffer.tryRemoveComponent(archetypeChunk.getReferenceTo(index), Frozen.getComponentType());
                }
        );
        store.forEachEntityParallel(ItemComponent.getComponentType(), (index, archetypeChunk, commandBuffer) -> {
            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
            commandBuffer.tryRemoveComponent(ref, Frozen.getComponentType());
            commandBuffer.tryRemoveComponent(ref, Interactable.getComponentType());
        });
    }

    private static void toggleEchoBlockState(@NonNullDecl Store<EntityStore> store, TransformComponent transformComponent) {
        Ref<ChunkStore> chunkRef = transformComponent.getChunkRef();
        Store<ChunkStore> chunkStore = chunkRef.getStore();
        WorldChunk worldChunk = chunkStore.getComponent(chunkRef, WorldChunk.getComponentType());
        World world = store.getExternalData().getWorld();

        EchoesOfTheMachinePlugin.get().getEchoBlockTracker().getAllBlockPositions().stream().forEach(position -> {
            Ref<ChunkStore> blockRef = BlockModule.getBlockEntity(world, position.x, position.y, position.z);
            if(blockRef == null) {return;}
            EchoBlock echoBlock = chunkStore.getComponent(blockRef, EchoBlock.getComponentType());
            BlockType blockType =worldChunk.getBlockType(position);
            worldChunk.setBlockInteractionState(position.x, position.y, position.z, blockType, echoBlock.getConfig().getOnState(), false);
        });
    }

    private void setEchoMovement(PlayerRef playerRef) {
        Ref<EntityStore> ref = playerRef.getReference();
        Store<EntityStore> store = ref.getStore();
        MovementManager movement = store.getComponent(ref, MovementManager.getComponentType());
        MovementSettings movementSettings = movement.getSettings();

        movementSettings.baseSpeed = 8f;
        movementSettings.jumpForce = 12f;

        movement.update(playerRef.getPacketHandler());
    }

    private void restrictPlayerMovement(PlayerRef playerRef) {
        Ref<EntityStore> ref = playerRef.getReference();
        Store<EntityStore> store = ref.getStore();
        MovementManager movement = store.getComponent(ref, MovementManager.getComponentType());
        MovementSettings movementSettings = movement.getSettings();

        movementSettings.baseSpeed = 0f;
        movementSettings.minSpeedMultiplier = 0f;
        movementSettings.maxSpeedMultiplier = 0f;
        movementSettings.airSpeedMultiplier = 0f;
        movementSettings.forwardCrouchSpeedMultiplier = 0f;
        movementSettings.jumpForce = 0f;

        movement.update(playerRef.getPacketHandler());
    }

    private void resetPlayerMovement(Ref<EntityStore> ref, Store<EntityStore> store, PlayerRef playerRef) {
        MovementManager movement = store.getComponent(ref, MovementManager.getComponentType());
        movement.applyDefaultSettings();

        movement.update(playerRef.getPacketHandler());
    }
    private static void resetCamera(PlayerRef playerRef) {
        playerRef.getPacketHandler().writeNoCache(new SetServerCamera(ClientCameraView.Custom, false, null));
    }
    private static void thirdPersonChannelingCamera(PlayerRef playerRef, Vector3f headRot) {
        ServerCameraSettings serverCameraSettings = new ServerCameraSettings();

        serverCameraSettings.isFirstPerson = false;
        serverCameraSettings.distance = 2.5f;
        serverCameraSettings.eyeOffset = true;
        serverCameraSettings.allowPitchControls = true;
        serverCameraSettings.displayCursor = false;
        serverCameraSettings.sendMouseMotion = false;
        serverCameraSettings.mouseInputType = MouseInputType.LookAtPlane;

        playerRef.getPacketHandler().writeNoCache(
                new SetServerCamera(ClientCameraView.Custom, true, serverCameraSettings)
        );
    }

    @Override
    public void onComponentSet(@NonNullDecl Ref<EntityStore> ref, @NullableDecl EchoState echoState, @NonNullDecl EchoState t1, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

    }

    @Override
    public void onComponentRemoved(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl EchoState echoState, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        PlayerRef playerRef =  commandBuffer.getComponent(ref, PlayerRef.getComponentType());

        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if(transformComponent == null) {return;}

        Ref<ChunkStore> chunkRef = transformComponent.getChunkRef();
        Store<ChunkStore> chunkStore = chunkRef.getStore();
        WorldChunk worldChunk = chunkStore.getComponent(chunkRef, WorldChunk.getComponentType());
        World world = store.getExternalData().getWorld();

        EchoesOfTheMachinePlugin.get().getEchoBlockTracker().getAllBlockPositions().stream().forEach(position -> {
            Ref<ChunkStore> blockRef = BlockModule.getBlockEntity(world, position.x, position.y, position.z);
            if(blockRef == null) {return;}
            EchoBlock echoBlock = chunkStore.getComponent(blockRef, EchoBlock.getComponentType());
            BlockType blockType =worldChunk.getBlockType(position);

            worldChunk.setBlockInteractionState(position.x, position.y, position.z, blockType, echoBlock.getConfig().getOffState(), false);
        });

        playerRef.sendMessage(Message.raw("Echo removed"));
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return this.query;
    }
}
