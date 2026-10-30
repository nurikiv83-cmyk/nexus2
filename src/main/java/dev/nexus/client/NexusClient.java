package dev.nexus.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class NexusClient implements ClientModInitializer {
    private static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        NexusConfig.load();

        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.nexus.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyBinding.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new NexusScreen());
                }
            }

            // Local-only cosmetic particles. They are not synchronized to other players.
            if (NexusConfig.cosmeticsEnabled && client.player != null && client.world != null
                    && client.player.age % 8 == 0) {
                double x = client.player.getX();
                double y = client.player.getY() + 0.8;
                double z = client.player.getZ();
                client.world.addParticle(ParticleTypes.END_ROD,
                        x + (client.world.random.nextDouble() - 0.5) * 0.7,
                        y + client.world.random.nextDouble() * 0.8,
                        z + (client.world.random.nextDouble() - 0.5) * 0.7,
                        0.0, 0.025, 0.0);
            }
        });

        System.out.println("[NEXUS] Client initialized. Press Right Shift to open the menu.");
    }
}
