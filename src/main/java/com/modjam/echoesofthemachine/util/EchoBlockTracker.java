package com.modjam.echoesofthemachine.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.util.io.BlockingDiskFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.locks.Lock;

public class EchoBlockTracker extends BlockingDiskFile {

    private final Set<Vector3i> echoBlockTracker;

    public EchoBlockTracker() {
        super(Path.of("EchoesOfTheMachine/EchoBlocks.json"));
        this.echoBlockTracker = new HashSet<>();
    }

    @Override
    protected void read(BufferedReader reader) throws IOException {
        Set<Vector3i> loaded = new HashSet<>();
        try {
            JsonElement rootEl = JsonParser.parseReader(reader);
            if (!rootEl.isJsonArray()) {
                throw new IOException("EchoBlocks.json root must be a JSON array");
            }

            JsonArray root = rootEl.getAsJsonArray();
            for (JsonElement el : root) {
                JsonObject echoBlockCoordObj = el.getAsJsonObject();

                int x = echoBlockCoordObj.get("x").getAsInt();
                int y = echoBlockCoordObj.get("y").getAsInt();
                int z = echoBlockCoordObj.get("z").getAsInt();

                Vector3i vectorPosition = new Vector3i(x, y, z);

                loaded.add(vectorPosition);
            }
        } catch (Exception e) {
            throw new IOException("Failed to parse EchoBlocks.json; refusing to overwrite file with empty data", e);
        }

        fileLock.writeLock().lock();
        try {
            echoBlockTracker.clear();
            echoBlockTracker.addAll(loaded);
        } finally {
            fileLock.writeLock().unlock();
        }
    }

    @Override
    protected void write(BufferedWriter bufferedWriter) throws IOException {
        JsonArray root = new JsonArray();

        for (Vector3i blockPosition : echoBlockTracker) {
            JsonObject blockPositionObj = new JsonObject();
            blockPositionObj.addProperty("x", blockPosition.x);
            blockPositionObj.addProperty("y", blockPosition.y);
            blockPositionObj.addProperty("z", blockPosition.z);


            root.add(blockPositionObj);
        }

        bufferedWriter.write(root.toString());
    }

    @Override
    protected void create(BufferedWriter bufferedWriter) throws IOException {
        try (JsonWriter jsonWriter = new JsonWriter(bufferedWriter)) {
            jsonWriter.beginArray().endArray();
        }
    }

    public void addBlockPosition(Vector3i blockPosition) {
        Lock lock = fileLock.writeLock();
        lock.lock();
        try {
            echoBlockTracker.add(blockPosition);
        } finally {
            lock.unlock();
        }
        syncSave();
    }

    public void removeBlockPosition(Vector3i blockPosition) {
        Lock lock = fileLock.writeLock();
        lock.lock();
        try {
            echoBlockTracker.remove(blockPosition);
        } finally {
            lock.unlock();
        }
        syncSave();
    }

    public boolean containsBlockPosition(Vector3i blockPosition){
        return this.echoBlockTracker.contains(blockPosition);
    }

    public List<Vector3i> getAllBlockPositions() {
        return echoBlockTracker.stream().toList();
    }
}
