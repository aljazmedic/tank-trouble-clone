package com.game.engine.math;

import com.game.engine.GameObject;
import com.game.statics.Screen;

import java.awt.geom.AffineTransform;

public class Transform {
    public Vector2D position, velocity;

    public Transform(Vector2D pos, Vector2D vel) {
        this.position = pos;
        this.velocity = vel;
    }

    public AffineTransform getAffineTransform() {
        AffineTransform at = new AffineTransform();
        at.translate(this.position.x, this.position.y);
        at.rotate(this.velocity.phi);
        return at;
    }

    public void bounce(Screen screen, GameObject gameObject) {
        int colliderNumber = screen.test(gameObject);
        int vertical = colliderNumber % 3;
        int horizontal = (colliderNumber - vertical) / 3;

        if (vertical != 0)
            this.position.x -= this.velocity.x;

        if (horizontal != 0)
            this.position.y -= this.velocity.y;
    }

    public void move() {
        this.position.add(this.velocity);
    }

    public void setPosition(Vector2D v2d) {
        this.position = v2d.copy();
    }

    public String toString() {
        return String.format("%s(%s, )", this.getClass().getTypeName(), this.position.coordsString());
    }
}
