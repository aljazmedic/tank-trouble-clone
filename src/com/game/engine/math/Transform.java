package com.game.engine.math;

import com.game.Screen;
import com.game.engine.GameObject;
import com.game.net.packets.Serializable;

import java.awt.geom.AffineTransform;
import java.nio.ByteBuffer;

public class Transform implements Serializable<Transform> {
    public Vector2D position, velocity;
    private boolean changed;

    public Transform(Vector2D pos, Vector2D vel) {
        this.position = pos.copy();
        this.velocity = vel.copy();
        this.changed = false;
    }

    public void resetChanges() {
        this.changed = false;
    }

    public boolean isChanged() {
        return changed;
    }

    public void changed() {
        this.changed = true;
    }

    public AffineTransform getAffineTransform() {
        AffineTransform at = new AffineTransform();
        at.translate(this.position.x, this.position.y);
        at.rotate(this.velocity.angle());
        return at;
    }

    public void bounce(Screen screen, GameObject gameObject) {
        int colliderNumber = screen.test(gameObject);
        int vertical = colliderNumber % 3;
        int horizontal = (colliderNumber - vertical) / 3;

        if (vertical != 0) {
            this.position.x -= this.velocity.x;
            this.changed = true;
        }

        if (horizontal != 0) {
            this.position.y -= this.velocity.y;
            this.changed = true;
        }
    }


    public void move(Vector2D change) {
        this.position = this.position.add(change);
        this.changed = true;
    }
    public void move() {
        this.move(this.velocity);
    }
    public void setPosition(Vector2D v2d) {
        this.position = v2d.copy();
        this.changed();
    }

    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity.copy();
        this.changed();
    }

    public String toString() {
        return String.format("%s(%s, %s)", this.getClass().getName(), this.position.coordsString(), this.velocity.coordsString());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Transform) {
            Transform othr = ((Transform) obj);
            return othr.position.equals(this.position) && othr.velocity.equals(this.velocity);
        } else
            return false;
    }

    @Override
    public byte[] toByteCode() {
        byte[] retArray = new byte[getNumberOfBytes()];
        ByteBuffer retB = ByteBuffer.wrap(retArray);
        retB.put(this.position.toByteCode()).put(this.velocity.toByteCode());
        return retArray;
    }

    @Override
    public Transform fromByteCode(ByteBuffer data) throws InvalidFormatException {
        if (data.remaining() < getNumberOfBytes()) throw new InvalidFormatException(this, data.toString());
        Vector2D pos = new Vector2D().fromByteCode(data);
        Vector2D vel = new Vector2D().fromByteCode(data);
        this.setPosition(pos);
        this.setVelocity(vel);
        return this;
    }

    @Override
    public int getNumberOfBytes() {
        return 2 * Vector2D.ZERO.getNumberOfBytes();
    }
}
