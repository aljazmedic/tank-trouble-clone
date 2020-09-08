package com.game.engine;

import javax.swing.*;
import java.util.Random;

public class GameSettings {

    private final String name;
    private final String ip;
    private boolean runServer;
    private boolean runClient;

    private GameSettings(String name, String ip, boolean runServer, boolean runClient) {

        this.name = name;
        this.ip = ip;

        this.runServer = runServer;
        this.runClient = runClient;
    }

    static GameSettings fromArgs(String[] args) throws InvalidArgumentsException {
        String name = null, ip = null, runServer = null, runClient = null;
        try {
            for (int i = 0; i < args.length; i++) {
                String t = args[i].trim();
                if (runServer == null) {
                    if (t.equals("--server") || (t.equals("-s"))) {
                        runServer = "server";
                        continue;
                    }
                }
                if (runClient == null) {
                    if (t.equals("--client") || (t.equals("-c"))) {
                        runClient = "client";
                        continue;
                    }
                }
                if (ip == null) {
                    if (t.equals("--ip")) {
                        ip = args[++i].trim();
                        continue;
                    }
                }
                if (name == null) {
                    if (t.equals("--name")) {
                        name = args[++i].trim();
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidArgumentsException();
        }
        boolean shouldRunServer = (runServer != null);
        boolean shouldRunClient = (runClient != null);
        if (!shouldRunClient && !shouldRunServer) {
            shouldRunServer = JOptionPane.showConfirmDialog(null, "Run server?", "Server",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
            shouldRunClient = JOptionPane.showConfirmDialog(null, "Run client?", "Client",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        }

        if (name == null) {
            name = JOptionPane.showInputDialog("Enter username");
            /*
            Random random = new Random();
            name = random.ints('0', 'z' + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(20)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
             */
        }
        ip = ip == null ? JOptionPane.showInputDialog("Enter IP") : ip;

        System.out.printf("%s %s %b %b\n", name, ip, shouldRunServer, shouldRunClient);
        if (name == null || ip == null) throw new InvalidArgumentsException();
        return new GameSettings(name, ip, shouldRunServer, shouldRunClient);
    }

    public String getIp() {
        return ip;
    }

    public String getName() {
        return name;
    }

    public boolean shouldRunServer() {
        return runServer;
    }

    public boolean shouldRunClient() {
        return runClient;
    }

    static class InvalidArgumentsException extends Exception {

    }
}
