package com.game.layers;

import com.game.engine.GameObject;

import java.util.Iterator;
import java.util.function.Function;

public class CombinedLayerFilterIterator extends LayerFilterIterator {
    @SafeVarargs
    public CombinedLayerFilterIterator(long layerMask, Iterator<? extends GameObject>... iter) {
        super(new LinkedIterators(iter), layerMask);
    }

    @SafeVarargs
    public CombinedLayerFilterIterator(Iterator<? extends GameObject>... iter) {
        super(new LinkedIterators(iter), Layer.EVERYTHING);
    }

    @SafeVarargs
    public CombinedLayerFilterIterator(Function<GameObject, Boolean> filterFn, Iterator<? extends GameObject>... iter) {
        super(new LinkedIterators(iter), filterFn);
    }
}
