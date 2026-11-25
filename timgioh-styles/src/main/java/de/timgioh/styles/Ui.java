package de.timgioh.styles;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/** Kleine Zeichen-Helfer fuer das dunkle, runde Menue-Design. */
final class Ui {
    static final int TEAL = 0xFF4FE3B2;
    static final int WHITE = 0xFFFFFFFF;
    static final int GREY = 0xFF9A9494;
    static final int ORANGE = 0xFFFFB347;

    /** Rechteck mit abgeschnittenen Ecken (ohne Ueberlappung, damit Transparenz sauber bleibt). */
    static void pill(DrawContext c, int x, int y, int w, int h, int color) {
        c.fill(x, y + 1, x + w, y + h - 1, color);
        c.fill(x + 1, y, x + w - 1, y + 1, color);
        c.fill(x + 1, y + h - 1, x + w - 1, y + h, color);
    }

    /** Zeichnet die 16x16-Vorschau (gleiche Nachbarn werden zu einem Streifen zusammengefasst). */
    static void thumb(DrawContext c, int[] px, int x, int y) {
        int n = Preview.SIZE;
        for (int row = 0; row < n; row++) {
            int col = 0;
            while (col < n) {
                int p = px[row * n + col];
                int end = col + 1;
                while (end < n && px[row * n + end] == p) {
                    end++;
                }
                if ((p >>> 24) != 0) {
                    c.fill(x + col, y + row, x + end, y + row + 1, p);
                }
                col = end;
            }
        }
    }

    static boolean inside(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    /** Unsichtbare Klickflaeche (gezeichnet wird alles selbst). */
    static ButtonWidget hit(int x, int y, int w, int h, Runnable action) {
        ButtonWidget b = ButtonWidget.builder(Text.empty(), btn -> action.run()).dimensions(x, y, w, h).build();
        b.setAlpha(0f);
        return b;
    }

    static ItemStack icon(String name) {
        Identifier id = Identifier.of("minecraft", name);
        Item item = Registries.ITEM.containsId(id) ? Registries.ITEM.get(id) : Items.PAPER;
        return new ItemStack(item);
    }

    private Ui() {
    }
}
