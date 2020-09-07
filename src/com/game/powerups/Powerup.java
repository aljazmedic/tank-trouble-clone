package com.game.powerups;

import com.game.engine.Game;
import com.game.engine.GameObject;
import com.game.engine.collision.Collidable;
import com.game.engine.collision.Collider;
import com.game.engine.math.Cooldown;
import com.game.engine.math.Vector2D;
import com.game.player.Player;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Random;

public abstract class Powerup extends GameObject implements Collidable {

    public static final int SIZE = 14;
    public Player holder;
    protected Cooldown timer;
    protected Color color;
    protected Collider collider;

    public Powerup(Vector2D pos, int time) {
        super(pos, ID.Powerup);
        holder = null;
        timer = new Cooldown(time);
        color = Color.BLACK;
        collider = new Collider(this, new Shape[]{new Ellipse2D.Double(-SIZE / 2., -SIZE / 2., SIZE, SIZE)});
    }

    public Powerup(Random rand, int time) {
        this(new Vector2D(
                (rand.nextDouble() * Game.WIDTH) - Game.WIDTH / 2.,
                (rand.nextDouble() * Game.HEIGHT) - Game.HEIGHT / 2.
        ), time);
    }

    public boolean hasExpired() {
        return this.pickedUp() && this.timer.timedOut();
    }


    public void onPickup() {
    }

    public void onTimeout() {
    }

    public boolean pickedUp() {
        return holder != null;
    }

    @Override
    public void tick() {
    }

    @Override
    public void paint(Graphics2D g) {
        Shape s = new Ellipse2D.Double(-SIZE / 2., -SIZE / 2., SIZE, SIZE);
        g.setColor(this.color);
        g.fill(transform.getAffineTransform().createTransformedShape(s));
    }

    @Override
    public Collider getCollider() {
        return this.collider;
    }

    public boolean canRecieve(Player player) {
        if (!(this instanceof SinglePowerupAAT)) return true;
        int consecutiveNum = ((SinglePowerupAAT) this).getConsecutiveNum();
        return (player.powerupsApplied & (1 << consecutiveNum)) == 0;
    }

    public static void give(Powerup p, Player player) {
        p.holder = player;
        p.timer.reset();
        if (!(p instanceof SinglePowerupAAT)) return;
        player.powerupsApplied |= 1 << ((SinglePowerupAAT) p).getConsecutiveNum();
    }

    public static void takeAway(Powerup p, Player player) {
        if (!(p instanceof SinglePowerupAAT)) return;
        player.powerupsApplied &= ~(1 << ((SinglePowerupAAT) p).getConsecutiveNum());
        Game.getHandler().removeObject(p);
    }
}
