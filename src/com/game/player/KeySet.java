package com.game.player;

import java.awt.event.KeyEvent;

public enum KeySet {
    KEY_SET1(
            KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_NUMPAD0),
    KEY_SET2(
            KeyEvent.VK_W, KeyEvent.VK_D, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_C),
    KEY_SET3(
            KeyEvent.VK_U, KeyEvent.VK_K, KeyEvent.VK_J, KeyEvent.VK_H, KeyEvent.VK_COMMA);
    private int[] moveCodes;
    public boolean[] pressedKeys;

    KeySet(int up, int right, int down, int left, int shoot) {
        this.moveCodes = new int[]{up, right, down, left, shoot};
        this.pressedKeys = new boolean[5];
    }

    public void update(KeyEvent evt) {
        int typedChar = evt.getKeyCode();
        for (int i = 0; i < moveCodes.length; i++) {
            int currentCode = moveCodes[i];
            if (typedChar == currentCode) {
                switch (evt.getID()) {
                    case KeyEvent.KEY_PRESSED:
                        this.pressedKeys[i] = true;
                        return;
                    case KeyEvent.KEY_RELEASED:
                        this.pressedKeys[i] = false;
                        return;
                }
            }
        }
    }
}