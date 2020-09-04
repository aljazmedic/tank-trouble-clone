package com.game.engine.math;

public class Cooldown {
    public double getWaitTime() {
        return waitTime;
    }

    protected final double waitTime;
    protected long end;

    public Cooldown(double waitTime) {
        this.waitTime = waitTime;
        end = System.currentTimeMillis();
    }

    public double timeLeft() {
        if (timedOut()) return 0;
        return ((double) (this.end - System.currentTimeMillis()) / 1000);
    }

    public void reset() {
        this.end = System.currentTimeMillis() + ((long) (waitTime * 1000));
    }

    public boolean timedOut() {
        return System.currentTimeMillis() >= end;
    }
}
