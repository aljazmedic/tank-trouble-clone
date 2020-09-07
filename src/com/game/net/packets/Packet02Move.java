package com.game.net.packets;

import com.game.engine.math.Transform;
import com.game.engine.math.Vector2D;

import java.nio.ByteBuffer;


public class Packet02Move extends NamePacket {
    private Transform t;

    public Packet02Move(String name, Transform t) {
        super(name, Type.MOVE);
        this.t = t;
    }

    private Packet02Move(ByteBuffer bb) throws Packet.InvalidPacketException
    {
        super(bb, Type.MOVE);
        try {
            t = new Transform(Vector2D.ZERO, Vector2D.ZERO).fromByteCode(bb);
        } catch (Serializable.InvalidFormatException e) {
            e.printStackTrace();
            throw new InvalidPacketException(e);
        }
    }

    @Override
    public void putData(ByteBuffer bb) {
        bb.put(t.toByteCode());
    }

    public static Packet02Move fromBytes(byte[] data)  throws Packet.InvalidPacketException
    {
        ByteBuffer bb = ByteBuffer.wrap(data);
        return new Packet02Move(bb);
    }

    @Override
    public String toString() {
        return String.format("%s(n:%s, %s)",
                this.getPacketType(),
                this.getName(),
                this.t.toString());
    }

    private void setTransform(Transform t) {
        this.t = t;
    }

    public Transform getTransform() {
        return t;
    }
}
