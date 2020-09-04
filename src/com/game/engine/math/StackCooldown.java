package com.game.engine.math;

public class StackCooldown extends Cooldown {
    private Cooldown betweenStacks;

    private final int ntimes;
    private int timesLeft;

    public StackCooldown(double betweenShots, int ntimes, double betweenStacks) {
        super(betweenShots);
        this.ntimes = ntimes;
        timesLeft = this.ntimes;
        this.betweenStacks = new Cooldown(betweenStacks);
    }

    @Override
    public void reset() {
        super.reset();
        if (timesLeft > 0) {
            timesLeft--;
        }
        if (timesLeft <= 0) {
            this.betweenStacks.reset();
            timesLeft = ntimes;
        }
    }

    public int getTimesLeft() {
        if (!betweenStacks.timedOut()) {
            return 0;
        }
        return timesLeft;
    }

    public double getBetweenStacksMax() {
        return betweenStacks.getWaitTime();
    }

    @Override
    public double timeLeft() {
        return Math.max(super.timeLeft(), betweenStacks.timeLeft());
    }

    @Override
    public boolean timedOut() {
        return super.timedOut() && betweenStacks.timedOut();
    }
}
