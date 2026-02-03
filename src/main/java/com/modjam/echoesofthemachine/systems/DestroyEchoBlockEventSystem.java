package com.modjam.echoesofthemachine.systems;

import com.modjam.echoesofthemachine.EchoesOfTheMachinePlugin;
import com.modjam.echoesofthemachine.component.EchoBlock;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.RootDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

public class DestroyEchoBlockEventSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {

    public DestroyEchoBlockEventSystem() {
        super(BreakBlockEvent.class);
    }

    @Override
    public void handle(final int index, @Nonnull final ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull final Store<EntityStore> store, @Nonnull final CommandBuffer<EntityStore> commandBuffer, @Nonnull final BreakBlockEvent event) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

        if (playerRef != null && event.getBlockType().getId().startsWith("Echo_Path_Block")) {
            if (!PermissionsModule.get().hasPermission(playerRef.getUuid(), "OP")){
                event.setCancelled(true);
                return;
            }
            World world = store.getExternalData().getWorld();
            Vector3i targetBlock = event.getTargetBlock();

            ChunkStore chunkStore = world.getChunkStore();
            Ref<ChunkStore> blockRef = BlockModule.getBlockEntity(world, targetBlock.x, targetBlock.y, targetBlock.z);

            if(blockRef != null) {
                EchoBlock echoBlock = chunkStore.getStore().getComponent(blockRef, EchoBlock.getComponentType());
                if(echoBlock != null) {
                    EchoesOfTheMachinePlugin.get().getEchoBlockTracker().removeBlockPosition(new Vector3i(targetBlock.x, targetBlock.y, targetBlock.z));
                }
            }
        }
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Collections.singleton(RootDependency.first());
    }
}
