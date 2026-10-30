package dev.nexus.client;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class NexusConfig {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("nexus-client.properties");

    public static int themeIndex = 4;
    public static boolean cosmeticsEnabled = true;
    public static boolean hudEnabled = true;

    private NexusConfig() {}

    public static void load() {
        if (!Files.exists(FILE)) {
            save();
            return;
        }
        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(FILE)) {
            properties.load(in);
            themeIndex = clamp(parseInt(properties.getProperty("theme"), 4), 0, NexusTheme.COLORS.length - 1);
            cosmeticsEnabled = Boolean.parseBoolean(properties.getProperty("cosmetics", "true"));
            hudEnabled = Boolean.parseBoolean(properties.getProperty("hud", "true"));
        } catch (IOException exception) {
            System.err.println("[NEXUS] Could not load config: " + exception.getMessage());
        }
    }

    public static void save() {
        Properties properties = new Properties();
        properties.setProperty("theme", Integer.toString(themeIndex));
        properties.setProperty("cosmetics", Boolean.toString(cosmeticsEnabled));
        properties.setProperty("hud", Boolean.toString(hudEnabled));
        try {
            Files.createDirectories(FILE.getParent());
            try (OutputStream out = Files.newOutputStream(FILE)) {
                properties.store(out, "NEXUS CLIENT settings");
            }
        } catch (IOException exception) {
            System.err.println("[NEXUS] Could not save config: " + exception.getMessage());
        }
    }

    private static int parseInt(String value, int fallback) {
        try { return Integer.parseInt(value); }
        catch (NumberFormatException ignored) { return fallback; }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
