package com.game.net;

import com.game.bullet.Bullet;
import com.game.engine.Game;
import com.game.engine.math.Vector2D;
import com.game.net.packets.*;


import com.game.player.Player;
import com.game.player.PlayerController;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class NetPlayer extends Player implements Serializable<NetPlayer> {

    public InetAddress ipAddress;
    public int port;

    public NetPlayer(Vector2D pos, Vector2D vel, PlayerController pc, ID id, ColorPreset cp, String name, InetAddress ipAddress, int port) {
        super(pos, vel, pc, id, cp, name);
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    protected void move(boolean[] keysDown) {
        this.transform.resetChanges();
        super.move(keysDown);
        if (this.transform.isChanged()) {
            Packet02Move p2mov = craftMovePacket();
            p2mov.clientToServer(Game.instance.socketClient);
        }
    }

    @Override
    public Bullet shoot() {
        Bullet b = super.shoot();
        craftShootPacket(b).clientToServer(Game.instance.socketClient);
        return b;
    }

    @Override
    public void onDie() {
        super.onDie();
        new Packet01Disconnect(getName()).clientToServer(Game.instance.socketClient);
    }

    public Packet00Login craftLoginPacket() {
        return new Packet00Login(this.name, this.transform, this.colors);
    }

    private Packet02Move craftMovePacket() {
        return new Packet02Move(this.name, this.transform);
    }

    private Packet03BulletShot craftShootPacket(Bullet b) {
        return new Packet03BulletShot(this.name, b.getTransform());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NetPlayer) {
            return //((NetPlayer) obj).serverPort == this.serverPort &&
                    //((NetPlayer) obj).ipAddress.toString().equals(this.ipAddress.toString()) &&
                    ((NetPlayer) obj).getName().equals(this.getName());
        } else return false;
    }


    @Override
    public byte[] toByteCode() {
        byte[] o = new byte[getNumberOfBytes()];
        ByteBuffer bb = ByteBuffer.wrap(o);
        bb
                .put(this.colors.toByteCode())
                .putDouble(this.getHealth())
                .put(this.transform.toByteCode());
        return o;
    }

    @Override
    public NetPlayer fromByteCode(ByteBuffer data) throws Packet.InvalidPacketException {
        this.colors.fromByteCode(data);
        this.setHealth(data.getDouble());
        this.transform.fromByteCode(data);
        return this;
    }

    @Override
    public int getNumberOfBytes() {
        return this.transform.getNumberOfBytes() + this.colors.getNumberOfBytes() + Double.BYTES;
    }

}
