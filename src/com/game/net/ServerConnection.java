package com.game.net;

import com.game.net.packets.Packet;
import com.game.net.packets.Packet00Login;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerConnection {

    static final HashMap<String, ServerConnection> allConnections = new HashMap<>();
    private NetPlayer player;
    private DatagramSocket clientSocket;
    private ExecutorService executor;

    ServerConnection(NetPlayer np, GameServer server) {
        this.player = np;
        this.executor = Executors.newFixedThreadPool(1, server);
        try {
            Logging.log("ServerConnection recieved from %s:%4d", np.ipAddress.getHostAddress(), np.port);
            this.clientSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }
        Packet00Login p0login = player.craftLoginPacket();
        for (Map.Entry<String, ServerConnection> entry : allConnections.entrySet()) {
            ServerConnection otherConnection = entry.getValue();
            otherConnection.sendToClient(p0login);
            this.sendToClient(otherConnection.getPlayerLoginPacket());

        }
        allConnections.put(np.getName(), this);
    }

    public static ServerConnection remove(String name) {
        ServerConnection conn = allConnections.remove(name);
        if(conn == null) return null;
        conn.executor.shutdown();
        return conn;
    }

    void sendToClient(Packet p) {
        executor.execute(() -> {
            byte[] bytes = p.build(1024);
            try {
                DatagramPacket dp = new DatagramPacket(bytes, bytes.length, this.player.ipAddress, this.player.port);
                this.clientSocket.send(dp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Logging.log("Sent data to %s:%4d", getPlayerAddress().getHostAddress(), getPlayerPort());
        });
    }

    void sendToClient(byte[] bytes) {
        executor.execute(() -> {
            try {
                DatagramPacket dp = new DatagramPacket(bytes, bytes.length, this.player.ipAddress, this.player.port);
                this.clientSocket.send(dp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Logging.log("Sent data to %s:%4d", getPlayerAddress().getHostAddress(), getPlayerPort());
        });
    }

    InetAddress getPlayerAddress() {
        return this.player.ipAddress;
    }

    int getPlayerPort() {
        return this.player.port;
    }


    public NetPlayer getPlayer() {
        return player;
    }

    public String getPlayerName() {
        return player.getName();
    }

    public Packet00Login getPlayerLoginPacket() {
        return this.player.craftLoginPacket();
    }

    public static NetPlayer byPlayerName(String name) {
        ServerConnection sc = allConnections.getOrDefault(name, null);
        if (sc == null) return null;
        return sc.getPlayer();
    }
}
