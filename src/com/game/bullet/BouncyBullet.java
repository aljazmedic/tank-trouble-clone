package com.game.bullet;

import java.awt.*;

public class BouncyBullet extends BasicBullet {
    private static Color color = new Color(0x2D2D2D);

    public BouncyBullet(Shooter parent, float damage) {
        super(parent, damage);
        transform.setVelocity(transform.velocity.asMag(SPEED * .8));
    }

    @Override
    public void paint(Graphics2D g) {
        g.setColor(color);
        drawBody(g);
    }

    @Override
    public void onScreenExit(int horizontal, int vertical) {
        if (horizontal != 0) {
            transform.velocity.y *= -1;
        } else if (vertical != 0) {
            transform.velocity.x *= -1;
        }
    }

    @Override
    public void onOutsideScreen() {
    }
}
