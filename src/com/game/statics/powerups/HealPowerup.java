package com.game.statics.powerups;

import com.game.player.Player;

import java.awt.*;
import java.util.Random;

public class HealPowerup extends Powerup {
    public HealPowerup(Random r) {
        super(r, 0);
        this.color = new Color(0xFF1B31);
    }

    @Override
    public void onPickup() {
        this.holder.setHealth(this.holder.getHealth() + 20);
    }

    @Override
    public boolean canRecieve(Player player) {
        return super.canRecieve(player) && (player.getHealth() != player.getMaxHealth());
    }
}
