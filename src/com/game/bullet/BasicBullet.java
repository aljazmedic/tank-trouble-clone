package com.game.bullet;

import com.game.engine.Game;
import com.game.engine.collision.Collider;
import com.game.engine.math.Cooldown;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class BasicBullet extends Bullet {
    Collider collider;
    Shape body;
    private static Color color = new Color(0x000000);
    private static final int TIME_BEING = 25;
    Cooldown timeLeft;

    public BasicBullet(Shooter parent, float damage) {
        super(parent, damage);
        transform.velocity.normalize(SPEED);
        r = 10;
        body = new Ellipse2D.Double(-r / 2., -r / 2., r, r);
        collider = new Collider(this, new Ellipse2D.Double(-r / 2., -r / 2., r, r));
        this.timeLeft = new Cooldown(TIME_BEING);
        timeLeft.reset();
    }

    @Override
    public void tick() {
        if (timeLeft.timedOut()) {
            Game.getHandler().removeObject(this);
        }
        transform.position.add(transform.velocity);
    }

    public void drawBody(Graphics2D g) {
        g.fill(transform.getAffineTransform().createTransformedShape(body));
    }

    @Override
    public void paint(Graphics2D g) {
        g.setColor(color);
        drawBody(g);
    }

    @Override
    public void onOutsideScreen() {
        Game.getHandler().removeObject(this);
    }


    @Override
    public Collider getCollider() {
        return this.collider;
    }
}
