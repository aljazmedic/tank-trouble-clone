package com.game.engine.math.raytracing;

import com.game.DebugUtil;
import com.game.engine.GameObject;
import com.game.engine.math.Vector2D;

import java.awt.*;

public class Ray extends Vector2D {
    protected long layerMask;

    protected GameObject target;
    protected Vector2D origin, normal, tangent, hitPoint;
    protected boolean hit;

    Ray(RayCastable rc) { //Constructor for empty Ray
        this(rc.getCastOrigin(), rc.getCastDirection(), rc.getLayerMask());
    }

    Ray(Vector2D origin, Vector2D direction, long layerMask) { //Constructor for empty Ray
        super(direction.x, direction.y);
        hit = false;
        this.origin = origin;
        this.target = null;
        this.layerMask = layerMask;
    }

    Ray(RayCastable parent, Vector2D path, GameObject target, Vector2D tangent) {
        super(path.x, path.y);
        this.tangent = tangent;
        this.target = target;
        this.origin = parent.getCastOrigin();
        this.layerMask = parent.getLayerMask();
        hit(path, target, tangent);
    }

    public void debugPaint(Graphics2D g) {
        if (!isHit()) return;
        DebugUtil.drawVectorFrom(g, origin, this, this.mag());
        g.setColor(new Color(0xFF030A));
        DebugUtil.drawVectorFrom(g, this.getHitPoint(), this.normal, 25);
        g.setColor(new Color(0x1DFF0B));
        DebugUtil.drawVectorFrom(g, this.getHitPoint(), this.tangent, 25);
    }

    public String toString() {
        return String.format("Ray(%s, l=%.2f, %s)", origin.coordsString(), this.mag(), target == null ? "None" : target.getId());
    }

    public boolean isHit() {
        return hit;
    }

    public boolean hit(Vector2D direction, GameObject target, Vector2D tangent) {
        this.set(direction.x, direction.y);
        this.target = target;
        this.tangent = tangent.normalize().copy();
        this.normal = tangent.copy().rotate(-Math.PI / 2).normalize();
        this.hitPoint = this.origin.copy().add(direction);
        this.hit = true;
        return this.hit;
    }

    public boolean fromRay(Ray othr) {
        if (!othr.isHit()) {
            this.hit = false;
            return false;
        }
        return hit(othr, othr.target, othr.tangent);
    }

    public Vector2D getHitPoint() {
        return this.hitPoint.copy();
    }

    protected Vector2D getNormal() {
        return this.normal;
    }

    public Vector2D getOrigin() {
        return origin;
    }
}
