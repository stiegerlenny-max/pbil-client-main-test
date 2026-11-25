package de.timgioh.styles;

import java.util.HashMap;
import java.util.Map;

/** Kleine 16x16-Vorschau, wie ein Eintrag mit einem bestimmten Look aussieht (wird nur mit fill() gezeichnet). */
public final class Preview {
    public static final int SIZE = 16;

    private static final Map<String, String> PATHS = new HashMap<>();
    private static final Map<String, int[]> CACHE = new HashMap<>();

    static {
        PATHS.put("respawn_anchor", "textures/block/respawn_anchor_top.png");
        PATHS.put("tnt", "textures/block/tnt_side.png");
        PATHS.put("fire", "textures/block/fire_0.png");
        PATHS.put("crossbow", "textures/item/crossbow_standby.png");
        PATHS.put("hud", "textures/gui/sprites/hud/heart/full.png");
        PATHS.put("sky_light", "textures/block/daylight_detector_top.png");
        PATHS.put("sounds", "textures/block/note_block.png");
        PATHS.put("inventory", "textures/item/chest.png");
        PATHS.put("shield", "textures/item/shield.png");
    }

    /** Pfad (ab assets/minecraft/) der Textur, die den Eintrag zeigt. */
    static String path(Catalog.Item item, Mixer.Source src) {
        String p = PATHS.get(item.id());
        if (p != null && src.read(p) != null) {
            return p;
        }
        for (String c : new String[] {"textures/item/" + item.icon() + ".png", "textures/block/" + item.icon() + ".png"}) {
            if (src.read(c) != null) {
                return c;
            }
        }
        return null;
    }

    /** 16x16 ARGB-Pixel oder null, wenn es keine Vorschau gibt (dann wird das Item-Symbol gezeichnet). */
    public static synchronized int[] get(Catalog.Item item, Catalog.Option opt, Colors.Color color) {
        boolean usesColor = opt.id().equals(Catalog.FLACH) || opt.id().equals(Catalog.TIMGIOH) || opt.id().equals(Catalog.WARDEN);
        String key = item.id() + "|" + opt.id() + (usesColor ? "|" + color.id() : "");
        if (CACHE.containsKey(key)) {
            return CACHE.get(key);
        }
        int[] result = null;
        try {
            result = make(item, opt, color);
        } catch (Exception | LinkageError ignored) {
            // keine Vorschau: es bleibt beim Item-Symbol
        }
        CACHE.put(key, result);
        return result;
    }

    private static int[] make(Catalog.Item item, Catalog.Option opt, Colors.Color color) throws java.io.IOException {
        Mixer.Source src = StyleManager.vanilla();
        String path = path(item, src);
        String id = opt.id();
        if (id.equals(Catalog.FLACH)) {
            return thumb(Mixer.flat(false, color.rgb()));
        }
        if (path == null) {
            return null;
        }
        if (id.equals(Catalog.TIMGIOH) || id.equals(Catalog.WARDEN)) {
            Map<String, byte[]> files = new HashMap<>();
            Bundle.collect(id, color.id(), item, files);
            byte[] b = files.get("assets/minecraft/" + path);
            if (b != null) {
                Png p = Png.decode(b);
                if (p != null) {
                    return thumb(p);
                }
            }
        }
        byte[] raw = src.read(path);
        Png vanilla = raw == null ? null : Png.decode(raw);
        if (vanilla == null) {
            return null;
        }
        Theme theme = Theme.byId(id);
        if (theme == null) {
            return thumb(vanilla);
        }
        Png out = vanilla.copy();
        theme.recolor(vanilla, out, 0, 0, 1, 1);
        return thumb(out);
    }

    /** Oberes Quadrat (animierte Streifen) auf 16x16 verkleinern. */
    static int[] thumb(Png p) {
        int side = Math.min(p.width, p.height);
        int[] out = new int[SIZE * SIZE];
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                int sx = Math.min(p.width - 1, (x * 2 + 1) * side / (SIZE * 2));
                int sy = Math.min(p.height - 1, (y * 2 + 1) * side / (SIZE * 2));
                out[y * SIZE + x] = p.argb[sy * p.width + sx];
            }
        }
        return out;
    }

    private Preview() {
    }
}
