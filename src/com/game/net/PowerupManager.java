package com.game.net;

import com.game.powerups.HealPowerup;
import com.game.powerups.Powerup;
import com.game.powerups.SpeedPowerup;

import java.util.HashMap;
import java.util.Random;

public class PowerupManager extends Thread {
    private final int sleep;
    private final Random r;
    private GameServer server;
    private HashMap<String, Powerup> activePowerups;


    PowerupManager(int sleep, Random r, GameServer server){
        super("PowerupSpawnerThread");
        this.sleep = sleep;
        this.r = r;
        this.server = server;
    }

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int c = r.nextInt(Powerup.Type.values().length); //NO invalid
            Powerup p;
            switch(Powerup.Type.values()[c]){
                case SPEED:
                    p = new SpeedPowerup(r);
                    break;
                case HEAL:
                    p = new HealPowerup(r);
                    break;
                default:
                    p = null;
            }
            if(p != null){
                p.craftPacket().serverToClient(server);
            }


        }
    }
}
