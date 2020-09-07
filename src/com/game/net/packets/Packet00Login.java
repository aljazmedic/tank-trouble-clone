package com.game.net.packets;

import com.game.engine.Game;
import com.game.engine.GameObject;
import com.game.engine.math.Transform;
import com.game.engine.math.Vector2D;
import com.game.net.NetPlayer;
import com.game.net.NetPlayerController;
import com.game.player.Player;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class Packet00Login extends NamePacket {

    private Transform t;
    private Player.ColorPreset cp;

    public Packet00Login(String name, Transform t, Player.ColorPreset cp) {
        super(name, Type.LOGIN);
        this.t = t;
        this.cp = cp;
    }

    private Packet00Login(ByteBuffer bb) throws Packet.InvalidPacketException
    {
        super(bb, Type.LOGIN);
        try {
            t = new Transform(Vector2D.ZERO, Vector2D.ZERO).fromByteCode(bb);
            cp = Player.ColorPreset.CP1.fromByteCode(bb);
        } catch (Serializable.InvalidFormatException e) {
            e.printStackTrace();
            throw new InvalidPacketException(e);
        }
    }

    @Override
    public void putData(ByteBuffer bb) {
        bb.put(t.toByteCode()).put(cp.toByteCode());
    }

    public static Packet00Login fromBytes(byte[] data)  throws Packet.InvalidPacketException
    {
        ByteBuffer bb = ByteBuffer.wrap(data);
        return new Packet00Login(bb);
    }

    @Override
    public String toString() {
        return String.format("%s(n:%s, %s, %s)",
                this.getPacketType(),
                this.getName(), cp,
                this.t.toString());
    }

    public Player.ColorPreset getColorPreset(){return cp;}

    public Transform getTransform() {
        return t;
    }

    public NetPlayer createPlayer(InetAddress assignAddress, int assignPort)
    {

        return new NetPlayer(t.position, t.velocity, new NetPlayerController(),
                GameObject.ID.Player,
                getColorPreset(),
                getName(), assignAddress, assignPort);
    }
}
