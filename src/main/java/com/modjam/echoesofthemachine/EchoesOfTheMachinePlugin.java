package com.modjam.echoesofthemachine;

import com.modjam.echoesofthemachine.commands.EchoCommand;
import com.modjam.echoesofthemachine.component.EchoBlock;
import com.modjam.echoesofthemachine.component.EchoState;
import com.modjam.echoesofthemachine.interactions.ActivateEchoState;
import com.modjam.echoesofthemachine.systems.DestroyEchoBlockEventSystem;
import com.modjam.echoesofthemachine.systems.EchoVisionSystem;
import com.modjam.echoesofthemachine.systems.PlaceEchoBlockEventSystem;
import com.modjam.echoesofthemachine.util.EchoBlockTracker;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAttachment;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * EchoesOfTheMachine - A Hytale server plugin.
 *
 * @author AmTekken
 * @version 1.0.0
 */
public class EchoesOfTheMachinePlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private ComponentType<EntityStore, EchoState> echoStateComponentType;
    private ComponentType<ChunkStore, EchoBlock> echoBlockComponentType;
    private ModelAttachment[] echoStatePlayerModel = {};

    private static EchoesOfTheMachinePlugin INSTANCE;

    private EchoBlockTracker echoBlockTracker;

    public EchoesOfTheMachinePlugin(@Nonnull JavaPluginInit init) {
        super(init);
        INSTANCE = this;
        this.echoBlockTracker = new EchoBlockTracker();
    }

    public static EchoesOfTheMachinePlugin get() {
        return INSTANCE;
    }

    @Override
    protected void setup() {

        LOGGER.at(Level.INFO).log("[EchoesOfTheMachine] Setting up...");

        var folder= new File("EchoesOfTheMachine");
        if (!folder.exists()) folder.mkdirs();

        this.echoBlockTracker.syncLoad();

        this.getCommandRegistry().registerCommand(new EchoCommand());
        echoStateComponentType = this.getEntityStoreRegistry().registerComponent(EchoState.class, "EchoState", EchoState.CODEC);
        echoBlockComponentType = this.getChunkStoreRegistry().registerComponent(EchoBlock.class, "EchoBlock", EchoBlock.CODEC);
        this.getEntityStoreRegistry().registerSystem(new EchoVisionSystem());
        this.getEntityStoreRegistry().registerSystem(new PlaceEchoBlockEventSystem());
        this.getEntityStoreRegistry().registerSystem(new DestroyEchoBlockEventSystem());

        this.getCodecRegistry(Interaction.CODEC).register("EchoesOfTheMachine_ActivateEchoState_Interaction", ActivateEchoState.class, ActivateEchoState.CODEC);


        LOGGER.at(Level.INFO).log("[EchoesOfTheMachine] Setup complete!");
        LOGGER.at(Level.INFO).log("[EchoesOfTheMachine] Refresh Works");

    }

    @Override
    protected void start() {
        LOGGER.at(Level.INFO).log("[EchoesOfTheMachine] Started!");
    }

    @Override
    protected void shutdown() {
        LOGGER.at(Level.INFO).log("[EchoesOfTheMachine] Shutting down...");
        INSTANCE = null;
        this.echoBlockTracker.syncSave();
    }

    public EchoBlockTracker getEchoBlockTracker() {return  this.echoBlockTracker;}

    private static final ScheduledExecutorService SCHEDULER =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "Echoes-Delay");
                t.setDaemon(true);
                return t;
            });

    public ComponentType<EntityStore, EchoState> getEchoStateComponentType() {
        return this.echoStateComponentType;
    }

    public ComponentType<ChunkStore, EchoBlock> getEchoBlockComponentType() {
        return this.echoBlockComponentType;
    }

    public ModelAttachment[] getEchoStatePlayerModel() {
        if(this.echoStatePlayerModel.length != 0) {
            return this.echoStatePlayerModel;
        }
        List<ModelAttachment> echoModelAttachements = new ArrayList<>();

        echoModelAttachements.add(new ModelAttachment("Characters/Player.blockymodel", "Characters/Player_Textures/Player_Greyscale.png", "Skin", "42", 1));
        echoModelAttachements.add(new ModelAttachment("Characters/Body_Attachments/Ears/Ears1.blockymodel", "Characters/Body_Attachments/Ears/Ears.png", "Skin", "42", 1));
        echoModelAttachements.add(new ModelAttachment("Characters/Body_Attachments/Eyes/Eyes.blockymodel", "Characters/Body_Attachments/Eyes/Eyes_Textures/Reptile_Eye.png", "Eyes_Gradient", "BlueLight", 1));
        echoModelAttachements.add(new ModelAttachment("Characters/Body_Attachments/Faces/Player_Face_Detached.blockymodel", "Characters/Body_Attachments/Faces/Faces_Detached_Textures/Face.png", "Skin", "42", 1));
        echoModelAttachements.add(new ModelAttachment("Characters/Body_Attachments/Mouths/Mouth1.blockymodel", "Characters/Body_Attachments/Mouths/Mouth1_Textures/Default_Greyscale.png", "Skin", "42", 1));
        echoModelAttachements.add(new ModelAttachment("Cosmetics/Underwears/Underwear.blockymodel", "Cosmetics/Underwears/Underwear_Textures/Underwear_Boxer_Texture.png", "Colored_Cotton", "Turquoise",1 ));

        this.echoStatePlayerModel = echoModelAttachements.toArray(new ModelAttachment[0]);

        return this.echoStatePlayerModel;
    }

    public static ScheduledFuture<?> after(long delay, TimeUnit unit, Runnable r) {
        return SCHEDULER.schedule(r, delay, unit);
    }

}