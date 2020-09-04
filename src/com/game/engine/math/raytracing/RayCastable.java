package com.game.engine.math.raytracing;

import com.game.engine.GameObject;
import com.game.engine.Handler;
import com.game.engine.math.Vector2D;

import java.awt.*;
import java.util.Iterator;
import java.util.function.Function;

public interface RayCastable {
    double MIN_RAY_LENGTH = 0.01;

    Vector2D getCastDirection();

    Vector2D getCastOrigin();

    Function<Handler, Iterator<GameObject>> getIteratorFn();

    void debugPaint(Graphics2D g);

    Ray createEmptyRay();

    void onRayHit(Ray ray);

    boolean cast(Ray storeRay, long layerMask);

    long getLayerMask();
}
