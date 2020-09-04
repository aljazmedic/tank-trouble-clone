package com.game.engine.graphics;

import com.game.engine.math.Transform;
import com.game.engine.math.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Sprite {
    private final Vector2D imgSize;
    private BufferedImage img;

    public Sprite(BufferedImage img) { //TODO Replace BodyPart with Sprite, collision from sprites?
        this.img = img;
        this.imgSize = new Vector2D(img.getWidth(), img.getHeight());
    }

    public void draw(Graphics2D g, Vector2D position, double theta) {
        AffineTransform at = new AffineTransform();
        at.translate(position.x, position.y);
        at.rotate(theta);
        at.translate(imgSize.x / 2, imgSize.y / 2);
        g.drawImage(img, at, null);
    }

    public void draw(Graphics2D g, Transform t) {
        AffineTransform at2 = t.getAffineTransform();
        at2.translate(imgSize.x / 2, imgSize.y / 2);
        g.drawImage(img, at2, null);
    }

    public void draw(Graphics2D g, AffineTransform at) {
        AffineTransform at2 = new AffineTransform(at);
        at2.translate(-imgSize.x / 2, -imgSize.y / 2);
        g.drawImage(img, at2, null);
    }

    public static Sprite fromSpriteSheet(SpriteSheet sprSheet, int x, int y) {
        return new Sprite(sprSheet.getSprite(x, y));
    }

    public static Sprite fromSpriteSheet(SpriteSheet sprSheet, int i) {
        return new Sprite(sprSheet.getSprite(i));
    }

    public static Sprite[] arrayFromSpriteSheet(SpriteSheet sprSheet, int i1, int i2) {
        Sprite[] ret = new Sprite[i2 - i1];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = new Sprite(sprSheet.getSprite(i1 + i));
        }
        return ret;
    }
}
