package com.game.net.packets;

import com.game.engine.math.Transform;

import java.nio.ByteBuffer;

public class Packet03BulletShot extends TransformNamePacket {
    public Packet03BulletShot(String name, Transform t) {
        super(Type.BULLET_SHOT, name, t);
    }

    public Packet03BulletShot(ByteBuffer bb) throws InvalidPacketException {
        super(Type.BULLET_SHOT,bb);
    }

    @Override
    public void putData(ByteBuffer bb) {}


}
