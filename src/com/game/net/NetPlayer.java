package com.game.net;

import com.game.engine.Game;
import com.game.engine.math.Vector2D;
import com.game.net.packets.Packet00Login;
import com.game.net.packets.Packet02Move;


import com.game.player.Player;
import com.game.player.PlayerController;

import java.awt.*;
import java.net.InetAddress;

public class NetPlayer extends Player {

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

    public Packet00Login craftLoginPacket() {
        return new Packet00Login(this.name, this.transform, this.colors);
    }

    private Packet02Move craftMovePacket() {
        return new Packet02Move(this.name, this.transform);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof NetPlayer){
            return //((NetPlayer) obj).serverPort == this.serverPort &&
                    //((NetPlayer) obj).ipAddress.toString().equals(this.ipAddress.toString()) &&
                    ((NetPlayer) obj).getName().equals(this.getName());
        }else return false;
    }
}
