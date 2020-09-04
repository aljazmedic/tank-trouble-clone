package com.game.layers;

import com.game.engine.GameObject;

import java.util.Iterator;
import java.util.function.Function;

public class LayerFilterIterator implements Iterator<GameObject> {
    private final Function<GameObject, Boolean> filterfunction;
    private Iterator<? extends GameObject> wrapped;
    private GameObject next;
    private boolean hasNextfiltered;

    public LayerFilterIterator(Iterator<? extends GameObject> iter, Function<GameObject, Boolean> fn) {
        this.wrapped = iter;
        this.filterfunction = fn;
        hasNextfiltered = getNextFiltered();
    }

    public LayerFilterIterator(Iterator<? extends GameObject> iter, long layerMask) {
        this(iter, gameObject -> Layer.fitsMask(gameObject, layerMask));
    }

    private boolean getNextFiltered() {
        do {
            if (!wrapped.hasNext()) {
                return false;
            }
            next = wrapped.next();
        } while (!filterfunction.apply(next));

        return true;
    }

    @Override
    public boolean hasNext() {
        return hasNextfiltered;
    }

    @Override
    public GameObject next() {
        if (!hasNextfiltered) throw new Error("No more elements!");
        GameObject ret = next;
        hasNextfiltered = this.getNextFiltered();
        return ret;
    }
}
