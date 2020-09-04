package com.game.player;

import com.game.engine.Game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PlayerKeyListener extends KeyAdapter {

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_ESCAPE) Game.getHandler().stopGame();
        for (PlayerController pc : PlayerController.allControllers) {
            pc.checkAgainst(e);
        }
    }

    public void keyReleased(KeyEvent e) {
        for (PlayerController pc : PlayerController.allControllers) {
            pc.checkAgainst(e);
        }
    }
}