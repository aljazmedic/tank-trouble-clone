package com.game;

import com.game.engine.Game;
import com.game.engine.GameObject;
import com.game.engine.Handler;
import com.game.engine.math.Vector2D;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse extends GameObject implements MouseMotionListener, MouseListener {
    private int w = 8;
    private Vector2D holdPos;

    public Mouse(Game forGame) {
        super(Vector2D.ZERO, ID.Mouse);
        forGame.addMouseListener(this);
        forGame.addMouseMotionListener(this);
        Game.getHandler().addObject(this);
        holdPos = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (holdPos == null)
            holdPos = this.transform.position;
        Vector2D v2d = new Vector2D(e.getX(), e.getY());
        Vector2D v2d1 = Handler.transformVector(v2d);
        this.transform.setPosition(v2d1);
    }

    public void mouseMoved(MouseEvent e) {
        holdPos = null;
        Vector2D v2d = new Vector2D(e.getX(), e.getY());
        Vector2D v2d1 = Handler.transformVector(v2d);
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
        this.holdPos = null;
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
        g.setColor(Color.BLACK);
        g.drawArc((int) this.transform.position.x - w / 2, (int) this.transform.position.y - w / 2, w, w, 0, 360);
        g.drawString(this.transform.position.toString(), (int) this.transform.position.x, (int) this.transform.position.y);
        if(holdPos != null){
            DebugUtil.drawVector(g,holdPos,this.transform.position);
        }
    }
}
