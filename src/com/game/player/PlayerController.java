package com.game.player;

import com.game.engine.Handler;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PlayerController extends KeyAdapter {
    private KeySet keySet;

    public PlayerController(Handler handler, KeySet ks) {
        /* controlling player object, controlKeys in KeyEvent.VK_...*/
        this.keySet = ks;
        handler.addKeyListener(this);
    }

    public boolean[] getPressedKeys() {
        return this.keySet.pressedKeys;
    }

    public void keyPressed(KeyEvent evt) {
        this.keySet.update(evt);
    }

    public void keyReleased(KeyEvent evt) {
        this.keySet.update(evt);
    }
}
