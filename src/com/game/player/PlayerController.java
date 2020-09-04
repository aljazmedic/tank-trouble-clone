package com.game.player;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class PlayerController {
    Player player;
    KeySet keySet;
    public static ArrayList<PlayerController> allControllers = new ArrayList<>();

    public PlayerController(Player player, KeySet ks) {
        /* controlling player object, controlKeys in KeyEvent.VK_...*/
        this.player = player;
        this.keySet = ks;
        allControllers.add(this);
    }

    public void checkAgainst(KeyEvent evt) {
        this.keySet.update(evt);
    }
}
