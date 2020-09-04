package com.game.statics;

import com.game.engine.Game;
import com.game.engine.GameObject;
import com.game.engine.Handler;
import com.game.engine.Util;
import com.game.engine.collision.Collidable;
import com.game.engine.collision.Collider;
import com.game.engine.math.Vector2D;

import java.awt.*;
import java.awt.geom.Area;
import java.util.LinkedList;
import java.util.List;

public class Screen extends GameObject implements Collidable {
    public static int WIDTH, HEIGHT;
    //final static Rectangle bg = new Rectangle();
    private Color backgroundColor = new Color(0xEBE8F2);
    private Collider collider;

    public Screen(Handler handler, int w, int h) {
        super(handler, Vector2D.ZERO, Vector2D.ZERO, ID.Screen, ID.Screen.getLayers());
        WIDTH = w;
        HEIGHT = h;
        collider = new Collider(this, new Rectangle(-WIDTH / 2, -HEIGHT / 2, WIDTH, HEIGHT));
    }

    @Override
    public void tick() {

    }

    public void paint(Graphics2D g) {
        g.setColor(backgroundColor);
        g.fillRect(-WIDTH / 2, -HEIGHT / 2, WIDTH, HEIGHT);
    }

    public void gizmosPaint(Graphics2D g) {
        int w = 14;
        //Points
        g.drawArc(Game.WIDTH / 2 - w / 2, Game.HEIGHT / 2 - w / 2, w, w, 0, 360);
        g.drawArc(-Game.WIDTH / 2 - w / 2, Game.HEIGHT / 2 - w / 2, w, w, 0, 360);
        g.drawArc(Game.WIDTH / 2 - w / 2, -Game.HEIGHT / 2 - w / 2, w, w, 0, 360);
        g.drawArc(-Game.WIDTH / 2 - w / 2, -Game.HEIGHT / 2 - w / 2, w, w, 0, 360);
    }

    public int test(GameObject go) {
        List<ScreenSide> retL = new LinkedList<>();
        int sum = 0;
        for (ScreenSide s : ScreenSide.allScreenSides) {
            if (s.inside(go))
                sum += s.num;
        }
        return sum;
    }

    @Override
    public Collider getCollider() {
        return collider;
    }

    public enum ScreenSide {
        LEFT(new Rectangle(-WIDTH / 2 - WIDTH, -HEIGHT / 2 - HEIGHT, WIDTH, 3 * HEIGHT), 1), //RIGHT, flipped
        TOP(new Rectangle(-WIDTH / 2 - WIDTH, HEIGHT / 2, 3 * WIDTH, HEIGHT), 3), //UP
        RIGHT(new Rectangle(WIDTH / 2, -HEIGHT / 2 - HEIGHT, WIDTH, 3 * HEIGHT), 2), //LEFT, flipped
        BOTTOM(new Rectangle(-WIDTH / 2 - WIDTH, -HEIGHT / 2 - HEIGHT, 3 * WIDTH, HEIGHT), 6); //DOWN
        private final Shape colliderShape;
        private final int num;
        public static ScreenSide[] allScreenSides = new ScreenSide[]{TOP, RIGHT, BOTTOM, LEFT};

        ScreenSide(Rectangle s, int num) {
            this.colliderShape = s;
            this.num = num;
        }

        public boolean inside(GameObject other) {
            if (!(other instanceof Collidable)) return false;
            return Util.intersects(((Collidable) other).getCollider().toWorldSpace(), new Area(colliderShape));
        }
    }
}
