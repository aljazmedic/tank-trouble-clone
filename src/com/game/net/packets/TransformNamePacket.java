package com.game.net.packets;

import com.game.engine.math.Transform;
import com.game.engine.math.Vector2D;

import java.nio.ByteBuffer;

public abstract class TransformNamePacket extends NamePacket {
    private Transform t;

    TransformNamePacket(Type packetType, String name, Transform t) {
        super(packetType, name);
        this.t = t;
    }

    TransformNamePacket(Type t, ByteBuffer bb) throws Packet.InvalidPacketException {
        super(t, bb);
        readBufferTransform(bb);

    }

    private void readBufferTransform(ByteBuffer bb) throws Packet.InvalidPacketException {
        this.t = new Transform(Vector2D.ZERO, Vector2D.ZERO).fromByteCode(bb);
    }

    @Override
    public void putPayload(ByteBuffer bb) {
        super.putPayload(bb);
        putTransformData(bb);
    }

    private void putTransformData(ByteBuffer bb) {
        bb.put(t.toByteCode());
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
