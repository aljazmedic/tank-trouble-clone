package com.game.net.packets;

import java.nio.ByteBuffer;

public abstract class NamePacket extends Packet {
    private String name;

    NamePacket(String name, Type type) {
        super(type);
        this.name = name;
    }

    NamePacket(ByteBuffer bb, Type t) throws Packet.InvalidPacketException
    {
        super(bb, t);
        readBufferName(bb);
    }

    private ByteBuffer putNameData(ByteBuffer bb) {
        short nameLen = (short) (this.getName().length());
        bb.putShort(nameLen);

        for(int i = 0; i < Math.min(nameLen,name.length()); i++)
        {
            bb.putChar(this.name.charAt(i));
        }
        return bb;
    }


    public void putPayload(ByteBuffer bb) {
        super.putPayload(bb); //Header
        putNameData(bb);
    }

    protected void readBufferName(ByteBuffer data) {
        int l = data.getShort();
        StringBuilder sb = new StringBuilder(l);
        for(int i = 0; i<l;i++) sb.append(data.getChar());
        this.name = sb.toString();;
    }

    public String getName() {
        return this.name;
    }


    @Override
    public String toString() {
        byte[] display = new byte[1024];
        ByteBuffer bb = ByteBuffer.wrap(display);
        putData(bb);
        String dataStr = new String(display);
        if(dataStr.length() >= 20) dataStr = dataStr.substring(0, 20)+"..";
        return String.format("%s(n:%s, %s)",
                this.getPacketType(),
                this.getName(),
                dataStr);
    }
}
