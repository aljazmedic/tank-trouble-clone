package com.game.engine.math.raytracing;


import com.game.DebugUtil;
import com.game.engine.Game;
import com.game.engine.GameObject;
import com.game.engine.Handler;
import com.game.engine.collision.Collidable;
import com.game.engine.collision.Collider;
import com.game.engine.math.Vector2D;
import com.game.layers.Layer;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.PathIterator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Function;

public class RayCaster implements MouseMotionListener, RayCastable {
    private Function<Handler, Iterator<GameObject>> iteratorFn;
    protected Vector2D origin, direction;
    protected LinkedList<RayHitListener> hitListeners;
    protected long layerMask;

    public RayCaster(RayCastable rc) {
        this(rc.getCastOrigin(), rc.getCastDirection(), rc.getLayerMask(), rc.getIteratorFn(), false);
    }

    public RayCaster(Vector2D origin, double angle, Function<Handler, Iterator<GameObject>> iteratorFn) {
        this(origin, Vector2D.fromAngle(angle), Layer.EVERYTHING, iteratorFn, true);
    }

    public RayCaster(Vector2D origin, Vector2D to, Function<Handler, Iterator<GameObject>> iteratorFn) {
        this(origin, to, Layer.EVERYTHING, iteratorFn, true);
    }

    public RayCaster(Vector2D origin, Vector2D to, long layerMask, Function<Handler, Iterator<GameObject>> iteratorFn, boolean handlerAdd) {
        this.origin = origin.copy();
        this.direction = to.copy().normalize();
        this.layerMask = layerMask;
        this.iteratorFn = iteratorFn;
        hitListeners = new LinkedList<>();
        if (handlerAdd)
            Game.getHandler().addRayCaster(this);

    }

    public RayCaster(Vector2D origin, long layerMask, Function<Handler, Iterator<GameObject>> iteratorFn) {
        this(origin, Vector2D.UP.copy().normalize(), layerMask, iteratorFn, true);
        Game.instance.addMouseMotionListener(this);
        System.out.println("RAYCASTER MOUSE LISTENER");
    }

    public void debugPaint(Graphics2D g) {
        DebugUtil.drawVectorFrom(g, origin, direction, 25);
        g.drawString(origin.toString(), (int) origin.x, (int) origin.y);

    }

    public void setDirection(Vector2D direction) {
        this.direction = direction.copy().normalize();
    }

    public static Vector2D checkRay(RayCastable rc, Vector2D start, Vector2D end) {
        final double x1 = start.x;
        final double y1 = start.y;
        final double x2 = end.x;
        final double y2 = end.y;

        Vector2D origin = rc.getCastOrigin(), direction = rc.getCastDirection();
        final double x3 = origin.x;
        final double y3 = origin.y;
        final double x4 = origin.x + direction.x;
        final double y4 = origin.y + direction.y;

        final double den = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (den == 0) return null;
        final double t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / den;
        final double u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / den;
        if (t <= 0 || t >= 1 || u <= 0) return null;
        final Vector2D line = end.copy().sub(start);
        return start.copy().sub(origin).add(line.normalize(line.mag() * t));
    }

    public boolean cast(Ray storeRay, long layerMask) {
        Iterator<GameObject> filteredIterator = this.getIteratorFn().apply(Game.getHandler());
        Ray bestRay = this.createEmptyRay();
        while ((filteredIterator.hasNext())) {
            Ray currentRay = this.createEmptyRay();
            GameObject go = filteredIterator.next();
            if (!(go instanceof Collidable)) continue;
            if (this.castOne((Collidable) go, currentRay)) {
                if (!bestRay.isHit() || currentRay.mag() < bestRay.mag()) {
                    bestRay.fromRay(currentRay);
                }
            }
        }
        if (bestRay.isHit()) {
            storeRay.fromRay(bestRay);
            this.onRayHit(bestRay);
            return true;
        }
        return false;
    }

    @Override
    public long getLayerMask() {
        return this.layerMask;
    }

    @SuppressWarnings({"ConstantConditions", "Duplicates"})
    private boolean castOne(Collidable c, Ray storeRay) {
        final double flatness = 0.1;
        Collider collider = c.getCollider();
        PathIterator a = collider.toWorldSpace().getPathIterator(null, flatness);

        Vector2D last = null;
        double shortestRayMag = Integer.MAX_VALUE;
        Ray shortestRay = this.createEmptyRay();
        Vector2D first = null;

        while (!a.isDone()) {
            float[] coords = new float[2];
            int segmentType = a.currentSegment(coords);
            Vector2D thisVector = new Vector2D(coords);
            switch (segmentType) {
                case PathIterator.SEG_CLOSE:
                    thisVector = first;
                case PathIterator.SEG_LINETO:
                    //System.out.format("(%s) -> (%s)\n", last.coordsString(), thisVector.coordsString());
                    Vector2D currentRayVector = RayCaster.checkRay(this, last, thisVector);
                    if (currentRayVector != null) {
                        double currentRayMag = currentRayVector.mag();
                        if (shortestRayMag >= currentRayMag) {
                            shortestRay = new Ray(this, currentRayVector, collider.getGameObject(), last.copy().sub(thisVector));
                            shortestRayMag = currentRayMag;
                        }
                    }
                case PathIterator.SEG_MOVETO:
                    last = thisVector;
                    break;

            }
            if (first == null) first = thisVector.copy();
            a.next();
        }
        if (shortestRayMag != Integer.MAX_VALUE && shortestRay.isHit() && shortestRay.mag() > RayCastable.MIN_RAY_LENGTH) {
            storeRay.fromRay(shortestRay);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Vector2D getCastDirection() {
        return this.direction;
    }

    public Vector2D getCastOrigin() {
        return origin.copy();
    }

    @Override
    public Function<Handler, Iterator<GameObject>> getIteratorFn() {
        return iteratorFn;
    }

    public Ray createEmptyRay() {
        return new Ray(this);
    }

    public void onRayHit(Ray r) {
        this.hitListeners.forEach(e -> e.onRayHit(r));
    }


    public void addRayHitListener(RayHitListener thl) {
        this.hitListeners.add(thl);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Vector2D v2d = new Vector2D(e.getX(), e.getY());
        Handler.transformVector(v2d);
        this.setDirection(v2d.sub(this.origin));
    }

}
