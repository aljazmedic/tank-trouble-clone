package com.game.powerups;

import java.awt.*;
import java.util.Random;

public class SpeedPowerup extends Powerup implements SinglePowerupAAT {
    double[] oldMoveMatrix;

    public SpeedPowerup(Random rand) {
        super(rand, 400);
        this.color = Color.BLUE;
    }

    @Override
    public int getConsecutiveNum() {
        return 1;
    }

    @Override
    public void onPickup() {
        this.oldMoveMatrix = this.holder.getMoveMatrix();
        double[] newMatrix = new double[oldMoveMatrix.length];
        for (int i = 0; i < oldMoveMatrix.length; i++) {
            newMatrix[i] = oldMoveMatrix[i] * 1.4f;
        }
        newMatrix[0] = oldMoveMatrix[0]; //preserve radial speed
        this.holder.setMoveMatrix(newMatrix);
    }

    @Override
    public void onTimeout() {
        this.holder.setMoveMatrix(oldMoveMatrix);
    }
}
