package com.modjam.echoesofthemachine.util;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import org.bson.BsonDocument;
import org.bson.RawBsonDocument;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

public class InventoryManager {

    public static byte[] serializeInventory(Player player) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] var11;
        try {
            DataOutputStream dos = new DataOutputStream(baos);

            byte[] byArray;
            try {
                CombinedItemContainer invContainer = player.getInventory().getCombinedEverything();
                AtomicInteger count = new AtomicInteger();
                invContainer.forEach((var1, var2) -> {
                    count.getAndIncrement();
                });
                dos.writeInt(count.get());
                invContainer.forEach((index, itemStack) -> {
                    try {
                        dos.writeShort(index);
                        dos.writeUTF(itemStack.getItem().getId());
                        dos.writeInt(itemStack.getQuantity());
                        dos.writeDouble(itemStack.getDurability());
                        dos.writeDouble(itemStack.getMaxDurability());
                        BsonDocument metadata = itemStack.getMetadata();
                        if (metadata == null) {
                            metadata = new BsonDocument();
                        }

                        dos.writeUTF(metadata.toJson());
                    } catch (IOException var4) {
                        throw new UncheckedIOException(var4);
                    }
                });
                dos.flush();
                byArray = baos.toByteArray();
            } catch (Throwable var9) {
                try {
                    dos.close();
                } catch (Throwable var8) {
                    var9.addSuppressed(var8);
                }

                throw var9;
            }

            dos.close();
            var11 = byArray;
        } catch (Throwable var10) {
            try {
                baos.close();
            } catch (Throwable var7) {
                var10.addSuppressed(var7);
            }

            throw var10;
        }

        baos.close();
        return var11;
    }

    public static CombinedItemContainer deserializeInventory(byte[] data) {
            CombinedItemContainer container = new CombinedItemContainer(new ItemContainer[0]);
            if (data != null && data.length != 0) {
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(data);

                    try {
                        DataInputStream dis = new DataInputStream(bais);

                        try {
                            int count = dis.readInt();
                            short countShort = 100;
                            container = new CombinedItemContainer(new ItemContainer[]{new SimpleItemContainer(countShort)});

                            for(int i = 0; i < count; ++i) {
                                short slot = dis.readShort();
                                String itemId = dis.readUTF();
                                int quantity = dis.readInt();
                                double durability = dis.readDouble();
                                double maxDurability = dis.readDouble();
                                String metadata = dis.readUTF();
                                BsonDocument bMetadata = metadata.isEmpty() ? new BsonDocument() : RawBsonDocument.parse(metadata);
                                container.setItemStackForSlot(slot, new ItemStack(itemId, quantity, durability, maxDurability, (BsonDocument)bMetadata), false);
                            }
                        } catch (Throwable var18) {
                            try {
                                dis.close();
                            } catch (Throwable var17) {
                                var18.addSuppressed(var17);
                            }

                            throw var18;
                        }

                        dis.close();
                    } catch (Throwable var19) {
                        try {
                            bais.close();
                        } catch (Throwable var16) {
                            var19.addSuppressed(var16);
                        }

                        throw var19;
                    }

                    bais.close();
                } catch (IOException var20) {}

                return container;
            } else {
                return container;
            }
    }

}
