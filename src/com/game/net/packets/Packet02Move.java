package com.game.net.packets;

import com.game.engine.math.Transform;

import java.nio.ByteBuffer;


public class Packet02Move extends TransformNamePacket {

    public Packet02Move(String name, Transform t) {
        super(Type.MOVE, name, t);
    }

    public Packet02Move(ByteBuffer bb) throws InvalidPacketException {
        super(Type.MOVE, bb);
    }

    @Override
    public void putData(ByteBuffer bb) {}

}
