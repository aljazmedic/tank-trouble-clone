package com.game;

import com.game.engine.GameObject;
import com.game.engine.Handler;
import com.game.engine.math.Vector2D;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse extends GameObject implements MouseMotionListener, MouseListener {
    private int w = 8;

    public Mouse() {
        super(Vector2D.ZERO, ID.Mouse);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
        Vector2D v2d = new Vector2D(e.getX(), e.getY());
        Vector2D v2d1 = v2d.copy();
        Handler.transformVector(v2d1);
        this.transform.setPosition(v2d1);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void tick() {
    }

    @Override
    public void paint(Graphics2D g) {
        g.drawArc((int) this.transform.position.x - w / 2, (int) this.transform.position.y - w / 2, w, w, 0, 360);
    }
}
