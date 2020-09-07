package com.game.bullet;

import com.game.engine.Dyable;
import com.game.engine.Game;
import com.game.engine.GameObject;
import com.game.engine.collision.Collidable;
import com.game.engine.math.Vector2D;


public abstract class Bullet extends GameObject implements Collidable {
    protected Shooter parent;
    protected Vector2D nextNormal;
    final static double SPEED = 5;
    int r;
    double damage;

    public Bullet(Shooter parent, float damage) {
        super(parent.getBulletOrigin(),
                Vector2D.fromPol(parent.getBulletSpawnAngle()),
                ID.Bullet);
        this.parent = parent;
        this.damage = damage;
    }

    @Override
    public void onHit(GameObject other) {
        if (other instanceof Dyable) {
            Dyable d = (Dyable) other;
            d.setHealth(d.getHealth() - this.damage);
            Game.getHandler().removeObject(this);
        }
    }
}

