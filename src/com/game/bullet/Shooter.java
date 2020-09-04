package com.game.bullet;

import com.game.engine.math.Vector2D;

public interface Shooter {
    Vector2D getBulletOrigin();

    boolean canShoot();

    double getBulletSpawnAngle();

    void shoot();
}
