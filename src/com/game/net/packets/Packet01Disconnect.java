package com.game.net.packets;

import java.nio.ByteBuffer;

public class Packet01Disconnect extends NamePacket {

    public Packet01Disconnect(String name) {
        super(Type.DISCONNECT, name);
    }

    public Packet01Disconnect(ByteBuffer bb) throws Packet.InvalidPacketException {
        super(Type.DISCONNECT,bb);
    }

    @Override
    public void putData(ByteBuffer bb) {
    }

}
