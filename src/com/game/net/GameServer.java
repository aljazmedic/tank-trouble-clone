package com.game.net;

import com.game.engine.Game;
import com.game.engine.GameObject;
import com.game.engine.Handler;
import com.game.engine.math.Transform;
import com.game.engine.math.Vector2D;
import com.game.net.packets.Packet;
import com.game.net.packets.Packet00Login;
import com.game.net.packets.Packet01Disconnect;
import com.game.net.packets.Packet02Move;
import com.game.player.Player;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.Executors;

public class GameServer extends Thread {
    private DatagramSocket socket;

    private Game game;
    private Handler handler;
    private HashMap<String, NetPlayer> connectedPlayers;

    public GameServer(Game _g) {
        this.game = _g;
        handler = Game.getHandler();
        connectedPlayers = new HashMap<>();
        try {
            this.socket = new DatagramSocket(1331);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        Logging.log("SERVER", "Started!");
        while (true) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
                this.onPacketRecieved(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onPacketRecieved(DatagramPacket packet) {
        Executors.newSingleThreadExecutor().execute(()->{
            String message = Packet.getByteHexStr(packet.getData());
            if (message.length() >= 10) message = message.substring(0, 20)+" ...";
            Logging.log("SERVER","%-7s [%s:%-5s]: [%4d] %s", "CLIENT", packet.getAddress().getHostAddress(), packet.getPort(), message.length(), message);
        });
        parsePacket(packet);
    }

    @SuppressWarnings({"Duplicates"})
    private void parsePacket(DatagramPacket datagramPacket) {
        byte[] data = datagramPacket.getData();
        InetAddress address = datagramPacket.getAddress();
        int port = datagramPacket.getPort();
        ByteBuffer bb = ByteBuffer.wrap(data);
        Packet.Type pt = Packet.parseType(bb);
        Packet packet = null;
        switch (pt) {
            case LOGIN:
                try {
                    packet = new Packet00Login(bb);
                    NetPlayer np = ((Packet00Login) packet).createPlayer(address, port);
                    addConnection(np, (Packet00Login) packet);
                } catch (Packet.InvalidPacketException e) {
                    e.printStackTrace();
                    return;
                }
                break;
            case DISCONNECT:
                try {
                    packet = new Packet01Disconnect(bb);
                } catch (Packet.InvalidPacketException e) {
                    e.printStackTrace();
                    return;
                }
                removeConnection((Packet01Disconnect) packet);
                break;
            case MOVE:
                try {
                    packet = new Packet02Move(bb);
                    handleMove((Packet02Move) packet);
                } catch (Packet.InvalidPacketException e) {
                    e.printStackTrace();
                    return;
                }
                break;
            case INVALID:
            default:
                break;
        }
        Logging.log("SERVER", packet);
    }

    private void handleMove(Packet02Move packet) {
        NetPlayer np = this.connectedPlayers.getOrDefault(packet.getName(), null);
        if (np == null || np == this.game.player) return;
        Logging.log("SERVER", "Setting transform " + packet.getTransform());
        np.setTransform(packet.getTransform());
        packet.serverToClient(this);
    }

    private void removeConnection(Packet01Disconnect packet) {
        NetPlayer np = connectedPlayers.remove(packet.getName());
        if (np == null) return;
        packet.serverToClient(this);
    }

    public void addConnection(NetPlayer newNetPlayer, Packet00Login newPlayerPacket) {
        Logging.log("SERVER", "Adding new player '%s'", newNetPlayer.getName());
        if (this.connectedPlayers.containsKey(newNetPlayer.getName())) return;

        if (newNetPlayer.getName().equals(this.game.player.getName()) &&
                (this.game.player.ipAddress == null || this.game.player.port == -1)) {
            Logging.log("SERVER", "Recieved own packet! Setting local player!");
            this.game.player.ipAddress = newNetPlayer.ipAddress;
            this.game.player.port = newNetPlayer.port;
        }

        for (Map.Entry<String, NetPlayer> entry : connectedPlayers.entrySet()) {
            NetPlayer otherNetPlayer = entry.getValue();
            if (!entry.getKey().equals(newNetPlayer.getName())) {
                sendData(newPlayerPacket, otherNetPlayer.ipAddress, otherNetPlayer.port);
                sendData(otherNetPlayer.craftLoginPacket(), newNetPlayer.ipAddress, newNetPlayer.port);
            }
        }
        this.connectedPlayers.put(newNetPlayer.getName(), newNetPlayer);

    }

    public void sendData(byte[] data, InetAddress ipAddress, int port) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData(Packet p, InetAddress ipAddress, int port) {
        this.sendData(p.build(1024), ipAddress, port);
    }

    public void sendDataToAllClients(byte[] data) {
        for (NetPlayer np : this.connectedPlayers.values()) {
            sendData(data, np.ipAddress, np.port);
        }
        Logging.log("SERVER", "sent %s", new String(data));
    }

    public void tick() {


    }
}