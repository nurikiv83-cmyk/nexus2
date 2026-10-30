package com.visualsx;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.Random;

public class VisualsXUltimateManager implements ClientModInitializer {
    // Tugmalar
    private static KeyBinding killauraKey;
    private static KeyBinding freecamKey;
    private static KeyBinding espKey;

    // Rejimlar (Switch'lar)
    private boolean killauraActive = false;
    private boolean freecamActive = false;
    private boolean espActive = false;

    private int tickCounter = 0;
    private final Random random = new Random();

    // Freecam uchun o'yinchi turgan joyni saqlab turish
    private double savedX, savedY, savedZ;

    @Override
    public void onInitializeClient() {
        // 1. Killaura ("H" tugmasi)
        killauraKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.visualsx.killaura", InputUtil.Type.KEY_SYM, GLFW.GLFW_KEY_H, "category.visualsx"
        ));
        // 2. Freecam ("U" tugmasi)
        freecamKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.visualsx.freecam", InputUtil.Type.KEY_SYM, GLFW.GLFW_KEY_U, "category.visualsx"
        ));
        // 3. ESP ("X" tugmasi)
        espKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.visualsx.esp", InputUtil.Type.KEY_SYM, GLFW.GLFW_KEY_X, "category.visualsx"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            // --- Tugma bosilishini tekshirish ---
            while (killauraKey.wasPressed()) {
                killauraActive = !killauraActive;
                client.player.sendMessage(Text.literal("§c[VisualsX] Killaura: " + (killauraActive ? "§aYOQILDI" : "§cO'CHIRILDI")), true);
            }
            while (freecamKey.wasPressed()) {
                freecamActive = !freecamActive;
                if (freecamActive) {
                    savedX = client.player.getX();
                    savedY = client.player.getY();
                    savedZ = client.player.getZ();
                    client.player.sendMessage(Text.literal("§e[VisualsX] Freecam: §aYOQILDI"), true);
                } else {
                    client.player.setPosition(savedX, savedY, savedZ);
                    client.player.sendMessage(Text.literal("§e[VisualsX] Freecam: §cO'CHIRILDI"), true);
                }
            }
            while (espKey.wasPressed()) {
                espActive = !espActive;
                client.player.sendMessage(Text.literal("§b[VisualsX] ESP (Wallhack): " + (espActive ? "§aYOQILDI" : "§cO'CHIRILDI")), true);
            }

            // --- 1. Freecam logikasi (Serverga harakat paketini yubormay turib, kamerani ko'chirish) ---
            if (freecamActive) {
                client.player.noClip = true;
                client.player.setVelocity(0, 0, 0);
                // O'yinchining serverdagi pozitsiyasini muzlatib qo'yamiz (Anticheat siljishni sezmaydi)
            } else {
                client.player.noClip = false;
            }

            // --- 2. Yashirin Killaura (Silent Rotation & Random Delay) ---
            if (killauraActive) {
                tickCounter++;
                if (tickCounter >= (7 + random.nextInt(5))) {
                    tickCounter = 0;
                    Entity target = client.world.getEntitiesByClass(LivingEntity.class,
                                    client.player.getBoundingBox().expand(4.5),
                                    e -> e != client.player && e.isAlive())
                            .stream()
                            .min(Comparator.comparingDouble(e -> client.player.distanceTo(e)))
                            .orElse(null);

                    if (target != null) {
                        double dX = target.getX() - client.player.getX();
                        double dZ = target.getZ() - client.player.getZ();
                        float targetYaw = (float) (Math.toDegrees(Math.atan2(dZ, dX)) - 90.0F);

                        // Kamerani silkimasdan serverga yo'nalish yuborish
                        client.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
                                targetYaw, client.player.getPitch(), client.player.isOnGround()
                        ));

                        client.interactionManager.attackEntity(client.player, target);
                        client.player.swingHand(Hand.MAIN_HAND);
                    }
                }
            }

            // --- 3. ESP logikasi (Client-side render optimizatsiyasi) ---
            if (espActive) {
                // Bu qismda o'yinchilarning siluetini ko'rsatish uchun client render ishlaydi
                for (Entity entity : client.world.getEntities()) {
                    if (entity instanceof LivingEntity && entity != client.player) {
                        entity.setGlowing(true); // O'yinchilar va moblarni porlaydigan (glowing) qilish
                    }
                }
            }
        });
    }
}
