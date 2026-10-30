package dev.nexus.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class NexusScreen extends Screen {
    private int panelLeft;
    private int panelTop;
    private int panelWidth;
    private int panelHeight;

    public NexusScreen() {
        super(Text.literal("NEXUS CLIENT"));
    }

    @Override
    protected void init() {
        clearChildren();
        panelWidth = Math.min(430, this.width - 30);
        panelHeight = 300;
        panelLeft = (this.width - panelWidth) / 2;
        panelTop = (this.height - panelHeight) / 2;

        int gridX = panelLeft + 26;
        int gridY = panelTop + 104;
        int cellW = 112;
        int cellH = 34;
        int gap = 8;

        for (int i = 0; i < NexusTheme.NAMES.length; i++) {
            final int index = i;
            int col = i % 3;
            int row = i / 3;
            addDrawableChild(ButtonWidget.builder(
                    Text.literal("●  " + NexusTheme.NAMES[i]),
                    button -> {
                        NexusConfig.themeIndex = index;
                        NexusConfig.save();
                    })
                    .dimensions(gridX + col * (cellW + gap), gridY + row * (cellH + gap), cellW, cellH)
                    .build());
        }

        int buttonY = gridY + 3 * (cellH + gap) + 8;
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Cosmetics: " + (NexusConfig.cosmeticsEnabled ? "ON" : "OFF")),
                button -> {
                    NexusConfig.cosmeticsEnabled = !NexusConfig.cosmeticsEnabled;
                    NexusConfig.save();
                    this.init();
                }).dimensions(panelLeft + 26, buttonY, 176, 28).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("HUD: " + (NexusConfig.hudEnabled ? "ON" : "OFF")),
                button -> {
                    NexusConfig.hudEnabled = !NexusConfig.hudEnabled;
                    NexusConfig.save();
                    this.init();
                }).dimensions(panelLeft + 210, buttonY, 176, 28).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Close"), button -> close())
                .dimensions(panelLeft + panelWidth - 92, panelTop + panelHeight - 36, 66, 22)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int accent = NexusTheme.accent();
        context.fill(0, 0, width, height, 0x99060912);

        // Main panel and accent edge.
        context.fill(panelLeft - 2, panelTop - 2, panelLeft + panelWidth + 2, panelTop + panelHeight + 2, accent);
        context.fill(panelLeft, panelTop, panelLeft + panelWidth, panelTop + panelHeight, 0xFF101522);
        context.fill(panelLeft + 1, panelTop + 1, panelLeft + panelWidth - 1, panelTop + 4, accent);

        context.drawText(textRenderer, "N E X U S", panelLeft + 26, panelTop + 20, 0xFFFFFFFF, false);
        context.drawText(textRenderer, "CLIENT  /  VISUAL SETTINGS", panelLeft + 26, panelTop + 39, 0xFFB8C1D8, false);
        context.drawText(textRenderer, "THEME COLORS", panelLeft + 26, panelTop + 78, 0xFFE4E9F5, false);
        context.drawText(textRenderer, "Choose an accent color for the interface", panelLeft + 26, panelTop + 91, 0xFF8995AE, false);

        // Selected theme indicator.
        int selectedX = panelLeft + 26 + (NexusConfig.themeIndex % 3) * 120;
        int selectedY = panelTop + 104 + (NexusConfig.themeIndex / 3) * 42;
        context.fill(selectedX - 2, selectedY - 2, selectedX + 114, selectedY + 36, accent);

        super.render(context, mouseX, mouseY, delta);
        context.drawText(textRenderer, "Right Shift  •  Open menu", panelLeft + 26,
                panelTop + panelHeight - 29, 0xFF8995AE, false);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
