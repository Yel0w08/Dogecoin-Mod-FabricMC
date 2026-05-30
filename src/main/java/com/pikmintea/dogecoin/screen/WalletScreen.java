package com.pikmintea.dogecoin.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class WalletScreen extends Screen {
    private static final int W = 220, H = 230;
    private static final int BG = 0xFF1A1A2E;
    private static final int PANEL = 0xFF16213E;
    private static final int ACCENT = 0xFFE94560;
    private static final int TEXT_GREEN = 0xFF53D769;
    private static final int TEXT_WHITE = 0xFFE0E0E0;
    private static final int TEXT_DIM = 0xFF888888;
    private static final int BTN = 0xFF2D2D5E;
    private static final int BTN_H = 0xFF3D3D7E;
    private static final int BTN_A = 0xFF4D4D9E;

    private long balance;
    private int inventoryCount;
    private TextFieldWidget amountField;
    private int cx, cy;

    private static class Btn {
        final int x, y, w, h;
        final String label;
        final Runnable action;
        Btn(int x, int y, int w, int h, String label, Runnable action) {
            this.x = x; this.y = y; this.w = w; this.h = h;
            this.label = label; this.action = action;
        }
        boolean hit(int mx, int my) { return mx >= x && mx < x + w && my >= y && my < y + h; }
    }
    private Btn[] buttons;

    public WalletScreen(long balance, int inventoryCount) {
        super(Text.literal(""));
        this.balance = balance;
        this.inventoryCount = inventoryCount;
    }

    public void updateData(long balance, int inventoryCount) {
        this.balance = balance;
        this.inventoryCount = inventoryCount;
    }

    private int amount() {
        try { return Math.max(1, MathHelper.clamp(Integer.parseInt(amountField.getText()), 1, 9999)); }
        catch (Exception e) { return 1; }
    }

    @Override
    protected void init() {
        super.init();
        cx = (width - W) / 2;
        cy = (height - H) / 2;

        amountField = new TextFieldWidget(textRenderer, cx + 80, cy + 94, 90, 16, Text.literal(""));
        amountField.setText("1");
        amountField.setMaxLength(9);
        addDrawableChild(amountField);

        int bx = cx + 12, by = cy + 118;
        buttons = new Btn[] {
            new Btn(bx, by, 40, 18, "1", () -> amountField.setText("1")),
            new Btn(bx + 46, by, 42, 18, "10", () -> amountField.setText("10")),
            new Btn(bx + 94, by, 42, 18, "64", () -> amountField.setText("64")),
            new Btn(bx + 142, by, 50, 18, "All", () -> amountField.setText(String.valueOf(inventoryCount))),
            new Btn(bx, cy + 144, 92, 22, "DEPOSIT", () -> act(0)),
            new Btn(bx + 100, cy + 144, 96, 22, "WITHDRAW", () -> act(1)),
            new Btn(bx + 54, cy + 176, 88, 22, "CLOSE", this::close),
            new Btn(bx + 54, cy + 200, 88, 22, "DEPOSIT ALL", () -> act(2)),
        };
    }

    private void act(int action) {
        ClientPlayNetworking.send(new WalletPayload.Action(action, amount()));
    }

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        if (btn == 0) {
            for (var b : buttons) {
                if (b.hit((int)mx, (int)my)) {
                    b.action.run();
                    return true;
                }
            }
        }
        return super.mouseClicked(mx, my, btn);
    }

    @Override
    public void renderBackground(DrawContext ctx, int mx, int my, float delta) {
        // no default dark overlay
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        ctx.fill(0, 0, width, height, 0x88000000);

        ctx.getMatrices().push();
        ctx.getMatrices().translate(0, 0, 100);

        // Drop shadow
        ctx.fill(cx - 3, cy - 3, cx + W + 3, cy + H + 3, 0x60000000);
        ctx.fill(cx, cy, cx + W, cy + H, BG);

        // Title bar
        ctx.fill(cx, cy, cx + W, cy + 32, ACCENT);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("DOGECOIN ATM"), cx + W / 2, cy + 9, 0xFFFFFFFF);

        // Balance panel
        ctx.fill(cx + 12, cy + 40, cx + W - 12, cy + 68, PANEL);
        ctx.drawText(textRenderer, Text.literal("BALANCE"), cx + 18, cy + 44, TEXT_DIM, false);
        ctx.drawText(textRenderer, Text.literal("DOGE " + String.format("%,d", balance)), cx + 18, cy + 56, TEXT_GREEN, false);
        ctx.drawText(textRenderer, Text.literal("INV " + inventoryCount), cx + 140, cy + 56, TEXT_WHITE, false);

        // Separator
        ctx.fill(cx + 12, cy + 76, cx + W - 12, cy + 78, ACCENT);

        // Amount label
        ctx.drawText(textRenderer, Text.literal("AMOUNT"), cx + 18, cy + 96, TEXT_DIM, false);

        // Quick-amount bar background
        ctx.fill(cx + 10, cy + 115, cx + W - 10, cy + 140, 0xFF0D0D1A);

        // Draw custom buttons
        for (var b : buttons) {
            boolean hovered = b.hit(mx, my);
            int col = hovered ? BTN_H : BTN;
            ctx.fill(b.x, b.y, b.x + b.w, b.y + b.h, col);
            if (hovered) ctx.fill(b.x, b.y, b.x + b.w, b.y + b.h, 0x30FFFFFF);
            int tx = b.x + (b.w - textRenderer.getWidth(b.label)) / 2;
            ctx.drawText(textRenderer, b.label, tx, b.y + (b.h - 8) / 2, hovered ? 0xFFFFFFFF : TEXT_WHITE, false);
        }

        super.render(ctx, mx, my, delta);
        ctx.getMatrices().pop();
    }
}
