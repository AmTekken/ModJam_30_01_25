package com.modjam.echoesofthemachine.systems;

import com.modjam.echoesofthemachine.EchoesOfTheMachinePlugin;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.RootDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

public class PlaceEchoBlockEventSystem extends EntityEventSystem<EntityStore, PlaceBlockEvent> {
    public PlaceEchoBlockEventSystem() {
        super(PlaceBlockEvent.class);
    }

    @Override
    public void handle(final int index, @Nonnull final ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull final Store<EntityStore> store, @Nonnull final CommandBuffer<EntityStore> commandBuffer, @Nonnull final PlaceBlockEvent event) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

        if (playerRef != null && event.getItemInHand().getItemId().startsWith("Echo_Path_Block")) {
            if (!PermissionsModule.get().hasPermission(playerRef.getUuid(), "OP")){
                event.setCancelled(true);
                return;
            }
            EchoesOfTheMachinePlugin.get().getEchoBlockTracker().addBlockPosition(event.getTargetBlock());
        }
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Collections.singleton(RootDependency.last());
    }

}
