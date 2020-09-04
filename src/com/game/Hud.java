package com.game;

import com.game.engine.Util;
import com.game.engine.math.Vector2D;
import com.game.player.Player;

import java.awt.*;

public class Hud {

    private Player player;
    private double targetHealth;
    private double maxHealth;
    private double greenValue = 255;

    private int score = 0;
    private int level = 1;
    private Vector2D pos;

    private Rectangle backRect, healthRect;

    public Hud(Player p, Vector2D pos) {
        this.player = p;
        this.pos = pos;
        targetHealth = player.getHealth();
        maxHealth = player.getMaxHealth();
        createRect();
    }

    private void createRect() {
        this.backRect = new Rectangle(0, -32, (int) maxHealth * 2, 32);
        this.healthRect = new Rectangle(0, -32, (int) targetHealth * 2, 32);
    }

    public void tick() {
        targetHealth = player.getHealth();
        this.healthRect.setSize((int) targetHealth * 2, 32);
        greenValue = (targetHealth * 2);
        greenValue = Util.clamp(greenValue, 0, 255);
        score++;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    private void drawShapes(Graphics2D g) {
        g.setColor(Color.gray);
        g.fill(this.backRect);
        g.setColor(new Color(75, (int) greenValue, 0));
        g.fill(this.healthRect);
        g.setColor(Color.white);
        g.draw(this.backRect);
    }

    public void paint(Graphics2D g) {
        drawShapes(g);
        g.setColor(Color.black);
        g.drawString("Score: " + score, (int) pos.x + 15, (int) pos.y + 64);
        g.drawString("Level: " + level, (int) pos.x + 15, (int) pos.y + 80);
    }

}