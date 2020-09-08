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

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;

public class GameClient extends Thread implements WindowListener {
    private InetAddress ipAddress;
    private DatagramSocket socket;

    private Game game;
    private static final int GAME_PORT = 1331;
    private Handler handler;

    public GameClient(Game _g, String ipAddress) {
        this.game = _g;
        handler = Game.getHandler();
        try {
            this.ipAddress = InetAddress.getByName(ipAddress);
            this.socket = new DatagramSocket();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        this.game.window.addWindowListener(this);
    }

    public void run() {
        Logging.log("CLIENT","Started!");
        while (true) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
                onPacketRecieved(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onPacketRecieved(DatagramPacket packet) {
        Executors.newSingleThreadExecutor().execute(()->{
            String message = Packet.getByteHexStr(packet.getData());
            if (message.length() >= 10) message = message.substring(0, 20)+" ...";
            Logging.log("CLIENT","%-7s [%s:%-5s]: [%4d] %s", "CLIENT", packet.getAddress().getHostAddress(), packet.getPort(), message.length(), message);
        });
       parsePacket(packet);
    }

    @SuppressWarnings("Duplicates")
    private void parsePacket(DatagramPacket datagramPacket) {
        byte[] data = datagramPacket.getData();
        InetAddress address = datagramPacket.getAddress();
        int port = datagramPacket.getPort();

        ByteBuffer bb = ByteBuffer.wrap(data);
        Packet.Type pt = Packet.parseType(bb);
        Packet packet = null;
        switch (pt) {
            case INVALID:
                break;
            case LOGIN:
                try {
                    packet = new Packet00Login(bb);
                } catch (Packet.InvalidPacketException e) {
                    e.printStackTrace();
                    return;
                }
                NetPlayer np = ((Packet00Login) packet).createPlayer(address,port);
                handler.addObject(np);
                break;
            case DISCONNECT:
                try {
                    packet = new Packet01Disconnect(bb);
                } catch (Packet.InvalidPacketException e) {
                    e.printStackTrace();
                    return;
                }
                handler.removeObject(handler.getNetPlayerByName(((Packet01Disconnect) packet).getName()));
                break;
            case MOVE:
                try {
                    packet = new Packet02Move(bb);
                    handler
                            .getNetPlayerByName(((Packet02Move) packet).getName())
                            .setTransform(((Packet02Move) packet).getTransform());
                } catch (Packet.InvalidPacketException e) {
                    e.printStackTrace();
                    return;
                }
                break;
        }
        Logging.log("CLIENT",packet);
    }

    public void sendData(byte[] data) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, GAME_PORT);
        try {
            socket.send(packet);
            Logging.log("CLIENT","sent %s",new String(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData(Packet p) {
        this.sendData(p.build(1024));
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        Packet01Disconnect dcPacket = new Packet01Disconnect(game.player.getName());
        dcPacket.clientToServer(this);
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}