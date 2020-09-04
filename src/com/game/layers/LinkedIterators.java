package com.game.layers;

import com.game.engine.GameObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class LinkedIterators implements Iterator<GameObject> {

    private Iterator<Iterator<? extends GameObject>> iterators;

    private boolean hasNext;
    private Iterator<? extends GameObject> currentIterator;
    private GameObject current;

    @SafeVarargs
    public LinkedIterators(Iterator<? extends GameObject>... iterators) {
        if (iterators.length == 0) {
            hasNext = false;
            currentIterator = null;
            return;
        }
        this.iterators = new LinkedList<>(Arrays.asList(iterators)).iterator();
        currentIterator = this.iterators.next();
        hasNext = readyNext();
    }

    private boolean readyNext() {
        if (currentIterator.hasNext()) {
            current = this.currentIterator.next();
            return true;
        } else if (iterators.hasNext()) {
            currentIterator = iterators.next();
            return readyNext();
        } else
            return false;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public GameObject next() {
        GameObject tmp = this.current;
        hasNext = readyNext();
        return tmp;
    }
}
