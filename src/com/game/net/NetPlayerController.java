package com.game.net;

import com.game.engine.Game;
import com.game.player.PlayerController;

import java.awt.event.KeyEvent;

public class NetPlayerController extends PlayerController {
    public NetPlayerController() {
        super(Game.getHandler(), null);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public boolean[] getPressedKeys() {
        return new boolean[]{false,false,false,false,false};
    }

    @Override
    public void keyPressed(KeyEvent evt) {}

    @Override
    public void keyReleased(KeyEvent evt) {}
}
