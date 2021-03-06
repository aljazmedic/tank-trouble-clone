package com.game.engine;

import com.game.engine.collision.Collidable;
import com.game.engine.math.Transform;
import com.game.engine.math.Vector2D;
import com.game.layers.Layer;

import java.awt.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public abstract class GameObject implements Tickeable {

    protected Transform transform;
    private ID id;
    private long layers;

    public GameObject(Vector2D pos, ID id) {
        this(pos, Vector2D.ZERO, id);
    }

    public GameObject(Vector2D pos, Vector2D vel, ID id) {
        this(pos, vel, id, id.getLayers());
    }

    public GameObject(Vector2D pos, Vector2D vel, ID id, long layers) {
        this.transform = new Transform(pos, vel);
        this.setLayers(layers);
        this.id = id;

    }

    public abstract void tick();

    public abstract void paint(Graphics2D g);

    public void gizmosPaint(Graphics2D g) {
    }

    public void colliderGizmosPaint(Graphics2D g) {
        g.setColor(new Color(0xFF4BF1));
        if (this instanceof Collidable)
            g.draw(
                    ((Collidable) this).getCollider().toWorldSpace()
            );
    }

    public boolean isInsideScreen() {
        return this.layoutToScreen() == 0;
    }

    public int layoutToScreen() {
        return Game.getHandler().getScreen().test(this);
    }

    public void onOutsideScreen() {
    }

    public void onScreenEntry(int h, int v) {
    }

    public void onScreenExit(int h, int v) {
    }

    public void onInsideScreen() {
    }

    public Transform getTransform() {
        return transform;
    }

    public boolean doesCollide(GameObject other) {
        return Util.intersects(this, other);
    }

    public void onHit(GameObject other) {
    }

    public void onEnter(GameObject other) {
    }

    public void onExit(GameObject other) {
    }

    public void onDie() {
    }

    public ID getId() {
        return id;
    }

    public long getLayers() {
        return this.layers;
    }

    public void setLayers(long l) {
        this.layers = l;
    }

    public enum ID {
        Player(Layer.PLAYERS),
        Bullet(Layer.BULLETS), Screen(Layer.ENVIRONMENT), Powerup(Layer.POWERUPS), Wall(Layer.ENVIRONMENT), Mouse(Layer.MOUSE);

        private final long layers;
        private final String type;

        ID(Layer... layers) {
            this.layers = Layer.all(layers);
            this.type = this.name();
        }

        public long getLayers() {
            return this.layers;
        }


    }

    public String toString() {
        return String.format("%s(%s)", this.getClass().getTypeName(), this.transform.position.coordsString());
    }

    public static String hash(GameObject go) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        md.update(go.getId().name().getBytes());
        md.update(go.transform.toByteCode());
        md.update(new Date().toString().getBytes());

// Call md.update with class's own data and recurse using
// updateDigest methods of internal objects

// Compute the digest
        byte[] result = md.digest();

// Convert to string to be able to use in a hash map
        BigInteger mediator = new BigInteger(1, result);
        return String.format("%040x", mediator);
    }
}