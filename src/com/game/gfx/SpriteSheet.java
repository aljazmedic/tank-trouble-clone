package com.game.gfx;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SpriteSheet {
    private BufferedImage spriteSheetImage;
    private int[] pixels;
    private int width = 0, height = 0;
    static private final int TILE_WIDTH = 8;

    public SpriteSheet(String filePath) {
        spriteSheetImage = null;
        try {
            spriteSheetImage = ImageIO.read(SpriteSheet.class.getResourceAsStream(filePath));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        if (spriteSheetImage == null) {
            return;
        }
        this.width = spriteSheetImage.getWidth();
        this.height = spriteSheetImage.getHeight();

        pixels = spriteSheetImage.getRGB(0, 0, width, height, null, 0, width);

        for (int i = 0; i < 16; i++) {
            int noalpha = (pixels[i] & 0xffffff);
            System.out.println(Integer.toHexString(noalpha) + " " + (noalpha & 0xff) / 85);
        }

    }

    public BufferedImage getTile(int x, int y, int w, int h) {
        return spriteSheetImage.getSubimage(x * TILE_WIDTH, y * TILE_WIDTH, w * TILE_WIDTH, h * TILE_WIDTH);
    }
}
