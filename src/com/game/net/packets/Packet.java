package com.game.net.packets;

import com.game.net.GameClient;
import com.game.net.GameServer;

import java.nio.ByteBuffer;


public abstract class Packet {
    private Type packetId;

    public Type getPacketType() {
        return this.packetId;
    }

    private void putType(ByteBuffer bb) {
        for(char c : getPacketType().packetId.toCharArray())
            bb.putChar(c);
    }

    Packet(String packetId) {
        this.packetId = Type.lookup(packetId);
    }

    Packet(ByteBuffer bb, Type t) throws Packet.InvalidPacketException {
        this.readBufferType(bb);
        if(t != this.packetId) throw new InvalidPacketException("Read "+this.packetId+", expected "+t);
    }

    Packet(Type packetType) {
        this.packetId = packetType;
    }

    public void serverToClient(GameServer server) {
        server.sendDataToAllClients(this.build(1024));
    }

    public void clientToServer(GameClient client) {
        client.sendData(this.build(1024));
    }

    public abstract void putData(ByteBuffer bb);

    protected void putPayload(ByteBuffer bb) {
        putType(bb);
    }

    private void readBufferType(ByteBuffer data) throws Packet.InvalidPacketException {
        String id = "" + data.getChar() + data.getChar();
        this.packetId = Type.lookup(id);
        if (packetId == Type.INVALID) throw new Packet.InvalidPacketException("Invalid ID: "+id);
    }

    public String toString() {
        byte[] display = new byte[1024];
        ByteBuffer bb = ByteBuffer.wrap(display);
        putData(bb);
        return String.format("%s(%s)", packetId, new String(display));
    }

    public byte[] build(int i) {
        byte[] packetData  = new byte[i];
        ByteBuffer bb = ByteBuffer.wrap(packetData);
        putPayload(bb);
        putData(bb);
        return packetData;
    }

    public enum Type {
        INVALID("iv"),
        LOGIN("lg"),
        DISCONNECT("dc"),
        MOVE("mv"),
        NOP("np");
        private String packetId;

        Type(String id) {
            this.packetId = id;
        }

        public String getPacketId() {
            return packetId;
        }

        @Override
        public String toString() {
            return this.packetId;
        }

        public static Type lookup(byte[] startBytes) {
            ByteBuffer bb = ByteBuffer.wrap(startBytes);
            return lookup(""+bb.getChar()+bb.getChar());
        }

        public static Type lookup(String id) {
            for (Type pt : Type.values()) {
                if (pt.getPacketId().equals(id)) return pt;
            }
            return INVALID;
        }
    }

    public static class InvalidPacketException extends Exception {
        public InvalidPacketException(Throwable e) {
            super(e);
        }

        public InvalidPacketException() {
            this(new Exception());
        }
        public InvalidPacketException(String message)
        {
            super(message);
        }
    }

    public static String padLeftUntil(String s, int n, char p) {
        StringBuilder ret = new StringBuilder();
        while (ret.length() + s.length() < n) {
            ret.append(p);
        }
        return ret.toString() + s;

    }

    public static String padRightUntil(String s, int n, char p) {
        StringBuilder ret = new StringBuilder();
        while (ret.length() + s.length() < n) {
            ret.append(p);
        }
        return s + ret.toString();

    }

    public static String getByteHexStr(String s) {
        return getByteHexStr(s.getBytes());
    }

    public static String getByteHexStr(byte[] bytes) {
        if(bytes.length == 0) return "";
        StringBuilder s = new StringBuilder();
        s.append(String.format("%02x", bytes[0]));
        for (int i = 1; i < bytes.length; i++) {
            byte b = bytes[i];
            s.append(String.format(" %02x", b));
        }
        return s.toString();
    }
}
