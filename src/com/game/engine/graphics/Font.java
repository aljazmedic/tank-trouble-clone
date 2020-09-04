package com.game.engine.graphics;

import com.game.engine.math.Vector2D;
import javafx.scene.text.TextAlignment;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Font {
    private BufferedImage[] imgs;
    private Vector2D imgSize;
    private final int letterWidth;
    private final int letterHeight;

    public Font(BufferedImage[] img, int letterWidth, int letterHeight) {
        this.imgs = img;
        this.imgSize = new Vector2D(img[0].getWidth(), img[0].getHeight());
        this.letterWidth = letterWidth;
        this.letterHeight = letterHeight;
    }

    private BufferedImage letterFromChar(char c) {
        return imgs[((int) c - '!')];
    }

    public static Font fromSpriteSheet(SpriteSheet sprSheet) {
        return new Font(sprSheet.getAllSprites(), sprSheet.getSpriteWidth(), sprSheet.getSpriteHeight());
    }

    public void draw(Graphics2D g, String text, AffineTransform at, double percent, TextAlignment ta) {
        Vector2D drawPos = Vector2D.ZERO.copy();
        int imgRows = 1, tmpCol = 0;
        int imgCols = 1;
        for (char c : text.toCharArray()) {
            if (c == '\n') {
                imgRows++;
                tmpCol = 0;
            } else tmpCol++;
            if (tmpCol >= imgCols) imgCols = tmpCol;
        }
        double startX = drawPos.x;
        int sizeX = (int) ((double) letterWidth * percent);
        int sizeY = (int) ((double) letterHeight * percent);
        BufferedImage textImage = new BufferedImage(imgCols * sizeX, imgRows * sizeY, BufferedImage.TYPE_INT_ARGB);
        Graphics2D textG = textImage.createGraphics();
        for (char c : text.toCharArray()) {
            switch (c) {
                case '\n':
                    drawPos = new Vector2D(startX, drawPos.y + sizeY);
                    break;
                case ' ':
                    drawPos.add(new Vector2D(sizeX, 0));
                    break;
                default:
                    textG.drawImage(letterFromChar(c), (int) drawPos.x, (int) drawPos.y, sizeX, sizeY, null);
                    drawPos.add(new Vector2D(sizeX, 0));
                    break;
            }
        }
        Font.drawOn(g, textImage, at, ta);
    }

    private static void drawOn(Graphics2D g, BufferedImage textImage, AffineTransform at, TextAlignment ta) {
        switch (ta) {
            case RIGHT:
                at.translate(-textImage.getWidth(), 0);
                break;
            case CENTER:
                at.translate(-textImage.getWidth() / 2., 0);
                break;
            case LEFT:
                break;
        }
        g.drawImage(textImage, at, null);
    }

    public void draw(Graphics2D g, String text, Vector2D pos, double percent, TextAlignment ta) {
        AffineTransform at = new AffineTransform();
        at.translate(pos.x, pos.y);
        this.draw(g, text, at, percent, ta);
    }

    public void draw(Graphics2D g, String text, Vector2D pos, double percent) {
        this.draw(g, text, pos, percent, TextAlignment.LEFT);
    }

    public void draw(Graphics2D g, String text, Vector2D pos) {
        this.draw(g, text, pos, 1);
    }

    public void draw(Graphics2D g, String text, AffineTransform at) {
        this.draw(g, text, at, 1, TextAlignment.LEFT);
    }

    public int getLetterWidth() {
        return letterWidth;
    }

    public int getLetterHeight() {
        return letterHeight;
    }
}
