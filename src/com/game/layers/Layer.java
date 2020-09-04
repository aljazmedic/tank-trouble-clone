package com.game.layers;

import com.game.engine.GameObject;

import java.util.LinkedList;
import java.util.List;


public enum Layer {
    ENVIRONMENT(0),
    PLAYERS(1),
    BULLETS(2),
    POWERUPS(3),
    MOUSE(4);

    public static final long EVERYTHING = -1;
    private final int num;

    public static Layer[] layers = new Layer[]{ENVIRONMENT, PLAYERS, BULLETS, MOUSE};

    Layer(int num) {
        if (num < 0) throw new Error("Only positive numbers");
        this.num = num;
    }

    public static List<Layer> getLayers(GameObject go) {
        List<Layer> ret = new LinkedList<>();
        long l = go.getLayers();
        for (int idx = 0; l != 0 && idx < layers.length; idx++) {
            boolean bit = l % 2 == 1;
            l = l >> 1;
            if (bit)
                ret.add(layers[idx]);
        }
        return ret;
    }

    public static boolean fitsMask(GameObject go, long layerMask) {
        long bothLayers = go.getLayers() & layerMask;
        return bothLayers != 0;
    }

    public static boolean hasLayer(GameObject go, Layer l) {
        return fitsMask(go, l.getBit());
    }

    public static void setLayer(GameObject go, Layer l) {
        long layerBit = l.getBit();
        go.setLayers(go.getLayers() | layerBit);
    }

    private long getBit() {
        return 1 << this.num;
    }

    public static long all() {
        return -1;
    }

    public static long all(Layer... allLayers) {
        long ret = 0;
        for (Layer l : allLayers) {
            ret |= l.getBit();
        }
        return ret;
    }

    public static long not(Layer... allLayers) {
        return ~all(allLayers);
    }
}
