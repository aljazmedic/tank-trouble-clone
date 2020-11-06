package com.game;

import com.game.engine.math.Vector2D;

import java.awt.*;

public class DebugUtil {
    public static void drawVector(Graphics2D g, Vector2D start, Vector2D finish) {
        g.drawLine((int) start.x, (int) start.y, (int) finish.x , (int) finish.y );
    }

    public static void drawVector(Graphics2D g, Vector2D v2d2) {
        drawVector(g, Vector2D.ZERO, v2d2);
    }

    public static void drawVectorFrom(Graphics2D g, Vector2D start, Vector2D dir, double mag) {
        Vector2D temp = dir.asMag(mag).add(start);
        g.drawLine((int) start.x, (int) start.y, (int) temp.x, (int) temp.y);
    }
}
