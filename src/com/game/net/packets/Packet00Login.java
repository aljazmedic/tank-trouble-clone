package com.game.net.packets;

import com.game.engine.GameObject;
import com.game.engine.math.Transform;
import com.game.net.NetPlayer;
import com.game.net.NetPlayerController;
import com.game.player.Player;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class Packet00Login extends TransformNamePacket {

    private Player.ColorPreset cp;

    public Packet00Login(String name, Transform t, Player.ColorPreset cp) {
        super(Type.LOGIN,name,t);
        this.cp = cp;
    }

    public Packet00Login(ByteBuffer bb) throws Packet.InvalidPacketException
    {
        super(Type.LOGIN, bb);
        cp = Player.ColorPreset.CP1.fromByteCode(bb);
    }

    @Override
    public void putData(ByteBuffer bb) {
        bb.put(cp.toByteCode());
    }


    @Override
    public String toString() {
        return String.format("%s(n:%s, %s, %s)",
                this.getPacketType(),
                this.getName(), cp,
                this.getTransform().toString());
    }

    public Player.ColorPreset getColorPreset(){return cp;}

    public NetPlayer createPlayer(InetAddress assignAddress, int assignPort)
    {

        return new NetPlayer(getTransform().position, getTransform().velocity, new NetPlayerController(),
                GameObject.ID.Player,
                getColorPreset(),
                getName(), assignAddress, assignPort);
    }
}
