package com.game.net;

import com.game.engine.Game;
import com.game.engine.Handler;
import com.game.net.packets.Packet;
import com.game.net.packets.Packet00Login;
import com.game.net.packets.Packet01Disconnect;
import com.game.net.packets.Packet02Move;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class GameServer extends Thread implements ThreadFactory {
    private DatagramSocket socket;

    private Game game;

    public GameServer(Game _g) {
        super("ServerThread");
        this.game = _g;
        try {
            Logging.log("Server listening on %4d ", 1331);
            this.socket = new DatagramSocket(1331);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        Logging.log("Started!");
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

    @SuppressWarnings("Duplicates")
    private void onPacketRecieved(DatagramPacket packet) {
        parsePacket(packet);
        String message = Packet.getByteHexStr(packet.getData());
        if (message.length() >= 10) message = message.substring(0, 20) + " ...";
        Logging.log("SERVER %-7s [%s:%-5s]: [%4d] %s", "recieved", packet.getAddress().getHostAddress(), packet.getPort(), message.length(), message);

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
                    Logging.log(packet);
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
                    Logging.log(packet);
                } catch (Packet.InvalidPacketException e) {
                    e.printStackTrace();
                    return;
                }
                removeConnection((Packet01Disconnect) packet);
                break;
            case MOVE:
                try {
                    packet = new Packet02Move(bb);
                    Logging.log(packet);
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
    }

    private void handleMove(Packet02Move packet) {
        NetPlayer np = ServerConnection.byPlayerName(packet.getName());
        if (np == null) return;
        np.setTransform(packet.getTransform());
        packet.serverToClient(this);
    }

    private void removeConnection(Packet01Disconnect packet) {
        ServerConnection gsc = ServerConnection.remove(packet.getName());
        if (gsc == null) return;
        sendDataToAllClients(packet);
    }


    public void addConnection(NetPlayer newNetPlayer, Packet00Login newPlayerPacket) {
        Logging.log("Adding new player '%s'", newNetPlayer.getName());

        //Connection already added
        if (ServerConnection.allConnections.containsKey(newNetPlayer.getName())) {
            Logging.log("Already Exists!");
            return;
        }

        if (newNetPlayer.equals(this.game.player) &&
                (this.game.player.ipAddress == null || this.game.player.port == -1)) {
            Logging.log("Recieved own packet! Setting local player!");
            this.game.player.ipAddress = newNetPlayer.ipAddress;
            this.game.player.port = newNetPlayer.port;
        }
        new ServerConnection(newNetPlayer, this);
    }

    public void sendDataToAllClients(byte[] data) {
        for (ServerConnection conn : ServerConnection.allConnections.values()) {
            conn.sendToClient(data);
        }
    }

    private void sendDataToAllClients(Packet01Disconnect packet) {
        for (ServerConnection conn : ServerConnection.allConnections.values()) {
            conn.sendToClient(packet);
        }

    }
    public void tick() {


    }

    @Override
    public Thread newThread(@NotNull Runnable runnable){
        int n = ServerConnection.allConnections.size()+1;
        return new Thread(runnable, String.format("server-conn-%d", n));
    }
}