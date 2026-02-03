package com.modjam.echoesofthemachine.util;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.PlayerSkin;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAttachment;
import com.hypixel.hytale.server.core.cosmetics.CosmeticRegistry;
import com.hypixel.hytale.server.core.cosmetics.CosmeticsModule;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSkinComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.List;

public class ModelHelper {
    public static void restoreSkinWithOverrides(Ref<EntityStore> ref, List<ModelAttachment> attachments) {
        CosmeticRegistry registry = CosmeticsModule.get().getRegistry();

        Store<EntityStore> store = ref.getStore();
        PlayerSkin playerSkin = store.getComponent(ref, PlayerSkinComponent.getComponentType()).getPlayerSkin();

        String gradientId = playerSkin.bodyCharacteristic.split("\\.")[1];
        String[] bodyCharacteristicParts = playerSkin.bodyCharacteristic.split("\\.");

        // This large block of code checks each vanilla cosmetic slot. If it's not in the 'overrides' map,
        // it resolves the corresponding attachment from the vanilla registry and adds it to the list.
        var bodyCharacteristic = registry.getBodyCharacteristics().get(bodyCharacteristicParts[0]);
        if (bodyCharacteristic != null) {
            attachments.add(ModelUtils.resolveAttachment(bodyCharacteristic, bodyCharacteristicParts, gradientId));
        }

            if (playerSkin.facialHair != null) {
                String[] facialHairsParts = playerSkin.facialHair.split("\\.");
                var facialHairs = registry.getFacialHairs().get(facialHairsParts[0]);
                if (facialHairs != null) {
                    attachments.add(ModelUtils.resolveAttachment(facialHairs, facialHairsParts, gradientId));
                }
            }

            if (playerSkin.ears != null) {
                String[] earsParts = playerSkin.ears.split("\\.");
                var ears = registry.getEars().get(earsParts[0]);
                if (ears != null) {
                    attachments.add(ModelUtils.resolveAttachment(ears, earsParts, playerSkin.bodyCharacteristic.split("\\.")[1]));
                }
            }

            if (playerSkin.eyebrows != null) {
                String[] eyebrowsParts = playerSkin.eyebrows.split("\\.");
                var eyebrows = registry.getEyebrows().get(eyebrowsParts[0]);
                if (eyebrows != null) {
                    attachments.add(ModelUtils.resolveAttachment(eyebrows, eyebrowsParts, gradientId));
                }
            }

            if (playerSkin.eyes != null) {
                String[] eyesParts = playerSkin.eyes.split("\\.");
                var eyes = registry.getEyes().get(eyesParts[0]);
                if (eyes != null) {
                    attachments.add(ModelUtils.resolveAttachment(eyes, eyesParts, gradientId));
                }
            }

            if (playerSkin.face != null) {
                String[] faceParts = playerSkin.face.split("\\.");
                var face = registry.getFaces().get(faceParts[0]);
                if (face != null) {
                    attachments.add(ModelUtils.resolveAttachment(face, faceParts, playerSkin.bodyCharacteristic.split("\\.")[1]));
                }
            }

            if (playerSkin.mouth != null) {
                String[] mouthsParts = playerSkin.mouth.split("\\.");
                var mouths = registry.getMouths().get(mouthsParts[0]);
                if (mouths != null) {
                    attachments.add(ModelUtils.resolveAttachment(mouths, mouthsParts, gradientId));
                }
            }

            if (playerSkin.haircut != null) {
                String[] haircutsParts = playerSkin.haircut.split("\\.");
                var haircuts = registry.getHaircuts().get(haircutsParts[0]);
                if (haircuts != null) {
                    attachments.add(ModelUtils.resolveAttachment(haircuts, haircutsParts, gradientId));
                }
        }


            if (playerSkin.cape != null) {
                String[] capesParts = playerSkin.cape.split("\\.");
                var capes = registry.getCapes().get(capesParts[0]);
                if (capes != null) {
                    attachments.add(ModelUtils.resolveAttachment(capes, capesParts, gradientId));
                }
            }

            if (playerSkin.faceAccessory != null) {
                String[] faceAccessoriesParts = playerSkin.faceAccessory.split("\\.");
                var faceAccessories = registry.getFaceAccessories().get(faceAccessoriesParts[0]);
                if (faceAccessories != null) {
                    attachments.add(ModelUtils.resolveAttachment(faceAccessories, faceAccessoriesParts, gradientId));
                }
            }

            if (playerSkin.gloves != null) {
                String[] glovesParts = playerSkin.gloves.split("\\.");
                var gloves = registry.getGloves().get(glovesParts[0]);
                if (gloves != null) {
                    attachments.add(ModelUtils.resolveAttachment(gloves, glovesParts, gradientId));
                }
            }

            if (playerSkin.headAccessory != null) {
                String[] headAccessoriesParts = playerSkin.headAccessory.split("\\.");
                var headAccessories = registry.getHeadAccessories().get(headAccessoriesParts[0]);
                if (headAccessories != null) {
                    attachments.add(ModelUtils.resolveAttachment(headAccessories, headAccessoriesParts, gradientId));
                }
        }

            if (playerSkin.overpants != null) {
                String[] overpantsParts = playerSkin.overpants.split("\\.");
                var overpants = registry.getOverpants().get(overpantsParts[0]);
                if (overpants != null) {
                    attachments.add(ModelUtils.resolveAttachment(overpants, overpantsParts, gradientId));
                }
            }

            if (playerSkin.overtop != null) {
                String[] overtopsParts = playerSkin.overtop.split("\\.");
                var overtops = registry.getOvertops().get(overtopsParts[0]);
                if (overtops != null) {
                    attachments.add(ModelUtils.resolveAttachment(overtops, overtopsParts, gradientId));
                }
        }

            if (playerSkin.pants != null) {
                String[] pantsParts = playerSkin.pants.split("\\.");
                var pants = registry.getPants().get(pantsParts[0]);
                if (pants != null) {
                    attachments.add(ModelUtils.resolveAttachment(pants, pantsParts, gradientId));
                }
        }

            if (playerSkin.shoes != null) {
                String[] shoesParts = playerSkin.shoes.split("\\.");
                var shoes = registry.getShoes().get(shoesParts[0]);
                if (shoes != null) {
                    attachments.add(ModelUtils.resolveAttachment(shoes, shoesParts, gradientId));
                }
        }

            if (playerSkin.undertop != null) {
                String[] undertopsParts = playerSkin.undertop.split("\\.");
                var undertops = registry.getUndertops().get(undertopsParts[0]);
                if (undertops != null) {
                    attachments.add(ModelUtils.resolveAttachment(undertops, undertopsParts, gradientId));
                }
        }

            if (playerSkin.underwear != null) {
                String[] underwearParts = playerSkin.underwear.split("\\.");
                var underwear = registry.getUnderwear().get(underwearParts[0]);
                if (underwear != null) {
                    attachments.add(ModelUtils.resolveAttachment(underwear, underwearParts, gradientId));
                }
        }

            if (playerSkin.earAccessory != null) {
                String[] earAccessoriesParts = playerSkin.earAccessory.split("\\.");
                var earAccessories = registry.getEarAccessories().get(earAccessoriesParts[0]);
                if (earAccessories != null) {
                    attachments.add(ModelUtils.resolveAttachment(earAccessories, earAccessoriesParts, gradientId));
                }
        }

            if(playerSkin.skinFeature != null) {
                String[] skinFeaturesParts = playerSkin.skinFeature.split("\\.");
                var skinFeatures = registry.getSkinFeatures().get(skinFeaturesParts[0]);
                if (skinFeatures != null) {
                    attachments.add(ModelUtils.resolveAttachment(skinFeatures, skinFeaturesParts, gradientId));
                }
            }
    }

}
