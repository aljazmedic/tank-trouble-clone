package com.game.engine.graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class SpriteSheet {
    private BufferedImage spriteSheetImage;
    private BufferedImage[] sprites;

    private final int spriteWidth, cols, rows, spriteHeight;

    public SpriteSheet(String filePath, int spriteWidth, int spriteHeight) {
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        spriteSheetImage = null;
        loadSprite(filePath);
        this.cols = spriteSheetImage.getWidth() / spriteWidth;
        this.rows = spriteSheetImage.getHeight() / spriteHeight;
        this.sprites = sliceAllSprites();
    }

    private void loadSprite(String name) {
        try {
            spriteSheetImage = ImageIO.read(getClass().getClassLoader().getResource(name));
        } catch (Exception ioe) {
            System.out.println("Error reading file: " + ioe);
        }
    }

    private BufferedImage[] sliceAllSprites() {
        BufferedImage[] ret = new BufferedImage[cols * rows];
        for (int i = 0; i < rows * cols; i++) {
            int x = i % rows;
            int y = i / cols;
            ret[i] = spriteSheetImage.getSubimage(x * spriteWidth, y * spriteHeight, spriteWidth, spriteHeight);
        }
        return ret;
    }

    public BufferedImage[] getSpriteSequence(int starti, int endi) {
        BufferedImage[] ret = new BufferedImage[endi - starti];
        if (endi - starti >= 0) System.arraycopy(this.sprites, starti, ret, 0, endi - starti);
        return ret;
    }

    public BufferedImage[] getAllSprites() {
        return this.sprites;
    }

    public BufferedImage getSprite(int n) {
        return this.sprites[n];
    }

    public BufferedImage getSprite(int x, int y) {
        return this.sprites[y * cols + x];
    }

    public int getSpriteHeight() {
        return spriteHeight;
    }

    public int getSpriteWidth() {
        return spriteWidth;
    }
}
