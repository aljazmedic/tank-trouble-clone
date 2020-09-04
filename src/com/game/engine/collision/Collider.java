package com.game.engine.collision;

import com.game.engine.GameObject;

import java.awt.*;
import java.awt.geom.Area;

public class Collider {
    private Area collisionArea;
    private GameObject gameObject;

    public Collider(GameObject go, Shape[] shapes) {
        Area a = new Area();
        for (Shape shape : shapes) {
            a.add(new Area(shape));
        }
        collisionArea = a;
        gameObject = go;
    }

    public Collider(GameObject go, Shape s) {
        collisionArea = new Area(s);
        gameObject = go;
    }

    public Area toWorldSpace() {
        return new Area(gameObject.getTransform().getAffineTransform().createTransformedShape(this.getArea()));
    }

    public Area getArea() {
        return this.collisionArea;
    }

    public GameObject getGameObject() {
        return this.gameObject;
    }

}
