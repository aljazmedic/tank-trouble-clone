package com.game.gfx;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SpriteSheet {
    private BufferedImage spriteSheetImage;
    private int[] pixels;
    private int width = 0, height = 0;

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

        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = (pixels[i] & 0xff) / 64;
        }



    }
}
