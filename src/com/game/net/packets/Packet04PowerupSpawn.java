package com.game.net.packets;

import com.game.engine.math.Transform;
import com.game.powerups.HealPowerup;
import com.game.powerups.Powerup;
import com.game.powerups.SpeedPowerup;

import java.nio.ByteBuffer;

public class Packet04PowerupSpawn extends TransformNamePacket {
    public Packet04PowerupSpawn(String name, Transform t) {
        super(Type.POWERUP_SPAWN, name, t);
    }

    public Packet04PowerupSpawn(ByteBuffer bb) throws InvalidPacketException {
        super(Type.POWERUP_SPAWN, bb);
    }

    @Override
    public void putData(ByteBuffer bb) {

    }

    public Powerup createPowerup(){
        Powerup.Type powType = Powerup.Type.lookup(getName());
        switch(powType){
            case INVALID:
                return null;
            case HEAL:
                return new HealPowerup(getTransform());
            case SPEED:
                return new SpeedPowerup(getTransform());
        }
        return null;
    }
}
