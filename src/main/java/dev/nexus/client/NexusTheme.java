package dev.nexus.client;

public final class NexusTheme {
    public static final String[] NAMES = {
            "Blue", "Orange", "Yellow",
            "Red", "Purple", "Pink",
            "Green", "Dark Blue", "Skin"
    };

    // ARGB palette in the requested 3 x 3 order.
    public static final int[] COLORS = {
            0xFF398BFF, 0xFFFF922E, 0xFFFFD43B,
            0xFFFF4D5D, 0xFF9B5CFF, 0xFFFF72B6,
            0xFF35C978, 0xFF263A78, 0xFFE5B18C
    };

    private NexusTheme() {}

    public static int accent() {
        return COLORS[Math.max(0, Math.min(COLORS.length - 1, NexusConfig.themeIndex))];
    }
}
