package com.game.engine;

import com.game.engine.collision.Collidable;
import com.game.engine.collision.Collider;

import java.awt.geom.Area;

public class Util {
    public static double clamp(double var, double min, double max) {
        /*Clamps the value between min and max*/
        return maxClamp(
                minClamp(var, min),
                max);
    }

    public static double maxClamp(double v, double max) {
        return Math.min(v, max);
    }

    public static double minClamp(double v, double min) {
        return Math.max(v, min);
    }

    public static double modClamp(double v, double mod) { //TODO Check implementaition
        while (v < 0) v += mod;
        v = v % mod;
        return v;
    }

    public static boolean intersects(Area shapeA, Area shapeB) {
        Area areaA = new Area(shapeA);
        areaA.intersect(shapeB);
        return !areaA.isEmpty();
    }

    public static boolean intersects(GameObject gameObject, GameObject other) {
        if (gameObject instanceof Collidable && other instanceof Collidable)
            return intersects(((Collidable) gameObject).getCollider(),
                    ((Collidable) other).getCollider());
        else return false;
    }

    public static boolean intersects(Collider firstCollider, Collider otherCollider) {
        return intersects(firstCollider.toWorldSpace(), otherCollider.toWorldSpace());
    }

}
