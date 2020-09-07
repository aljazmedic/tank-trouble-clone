package com.game.net.packets;

import java.nio.ByteBuffer;

public class Packet01Disconnect extends NamePacket {

    public Packet01Disconnect(String name) {
        super(name, Type.DISCONNECT);
    }

    private Packet01Disconnect(ByteBuffer bb) throws Packet.InvalidPacketException {
        super(bb, Type.DISCONNECT);
    }

    @Override
    public void putData(ByteBuffer bb) {
    }

    public static Packet01Disconnect fromBytes(byte[] data) throws Packet.InvalidPacketException {

        ByteBuffer bb = ByteBuffer.wrap(data);
        return new Packet01Disconnect(bb);
    }

}
