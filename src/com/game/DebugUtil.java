package com.game;

import com.game.engine.math.Vector2D;

import java.awt.*;

public class DebugUtil {
    public static void drawVector(Graphics2D g, Vector2D start, Vector2D finish, double mag) {
        Vector2D temp = finish.copy().normalize(mag);
        g.drawLine((int) start.x, (int) start.y, (int) temp.x + (int) start.x, (int) temp.y + (int) start.y);
    }

    public static void drawVector(Graphics2D g, Vector2D v2d2, double mag) {
        drawVector(g, Vector2D.ZERO, v2d2, mag);
    }

    public static void drawVectorFrom(Graphics2D g, Vector2D start, Vector2D dir, double mag) {
        Vector2D temp = dir.copy().normalize(mag).add(start);
        g.drawLine((int) start.x, (int) start.y, (int) temp.x, (int) temp.y);
    }
}
