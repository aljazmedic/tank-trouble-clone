package com.game.engine;

import com.game.engine.collision.Collidable;
import com.game.engine.math.Transform;
import com.game.engine.math.Vector2D;
import com.game.layers.Layer;

import java.awt.*;

public abstract class GameObject implements Tickeable {

    protected Transform transform;
    private ID id;
    private long layers;

    public GameObject(Vector2D pos, ID id) {
        this(pos, Vector2D.ZERO, id);
    }

    public GameObject(Vector2D pos, Vector2D vel, ID id) {
        this(Game.getHandler(), pos, vel, id, id.getLayers());
    }

    public GameObject(Handler handler, Vector2D pos, Vector2D vel, ID id, long layers) {
        this.transform = new Transform(pos.copy(), vel.copy());
        this.setLayers(layers);
        this.id = id;
        handler.addObject(this);

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
        Player1(Layer.PLAYERS),
        Player2(Layer.PLAYERS),
        Player3(Layer.PLAYERS),
        Bullet(Layer.BULLETS), Screen(Layer.ENVIRONMENT), Powerup(Layer.POWERUPS), Wall(Layer.ENVIRONMENT), Mouse(Layer.MOUSE);

        private final long layers;

        ID(Layer... layers) {
            this.layers = Layer.all(layers);
        }

        public long getLayers() {
            return this.layers;
        }
    }

    public String toString() {
        return String.format("%s(%s)", this.getClass().getTypeName(), this.transform.position.coordsString());
    }
}