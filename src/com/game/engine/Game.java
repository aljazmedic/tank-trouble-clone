package com.game.engine;

import com.game.Hud;
import com.game.Mouse;
import com.game.engine.math.Vector2D;
import com.game.gfx.SpriteSheet;
import com.game.net.GameClient;
import com.game.net.GameServer;
import com.game.net.NetPlayer;
import com.game.player.KeySet;
import com.game.player.Player;
import com.game.player.PlayerController;
import com.sun.org.apache.xpath.internal.axes.LocPathIterator;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Game extends Canvas implements Runnable {
    //https://youtu.be/Urg8AEIVyWA?t=8m45s
    private static final long serialVersionUID = -240840600533728354L;
    public static ExecutorService logExecutor = Executors.newFixedThreadPool(1);
    private static Random rand = new Random();

    public static final int WIDTH = 800, HEIGHT = WIDTH / 16 * 9;
    private static Random random;
    private Thread thread;
    private boolean running = false;
    private static Handler handler;
    private Hud hud;
    public static Game instance;

    //private SpriteSheet tankSheet = new SpriteSheet("/graphics/Sprite_Sheet.png");

    public GameClient socketClient;
    private GameServer socketServer;
    private boolean runningServer = false;

    public NetPlayer player;
    public Window window;

    public static void main(String[] args) {
        GameSettings gs;
        try {
            gs = GameSettings.fromArgs(args);
        } catch (GameSettings.InvalidArgumentsException e) {
            System.out.println("Invalid arguments!");
            System.exit(0);
            return;
        }

        instance = new Game();


        if (gs.shouldRunServer()) {
            instance.runServer();
        }
        if(gs.shouldRunClient()){
            instance.init();
            instance.createLocalPlayer(gs.getName(), KeySet.KEY_SET2);

            instance.runClient(gs.getIp());
            instance.start();

        }
    }

    public Game() {
        handler = new Handler(WIDTH, HEIGHT, this, rand);
        random = new Random(34565434567876L);
    }

    public Random getRandom() {
        return random;
    }

//    public static Font font;

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    private void runServer() {
        this.runningServer = true;
        socketServer = new GameServer(this);
        runningServer = true;
        socketServer.start();
    }

    private synchronized void start() {
        thread = new Thread(this);
        running = true;
        thread.start();

        handler.addObject(player);
        player.craftLoginPacket().clientToServer(socketClient);

    }

    private void init() {
        Mouse mcl = new Mouse();
        this.addMouseListener(mcl);
        this.addMouseMotionListener(mcl);


        //hud = new Hud(handler.getActivePlayer(GameObject.ID.Player1), new Vector2D(-Game.WIDTH/2. + 16, Game.HEIGHT/2. -16));
        //new RayCaster(Vector2D.ZERO,Math.PI*3/2);
//        RayCaster rc = new RayCaster(Vector2D.ZERO, Layer.EVERYTHING, h -> h.allGameObjects(Layer.not(Layer.ENVIRONMENT)));
//        getHandler().addRayCaster(rc);

        window = new Window("Tank Trouble", this);
        window.requestFocus();
    }

    void runClient(String ip){
        socketClient = new GameClient(this, ip);
        socketClient.start();
    }

    private void createLocalPlayer(String name, KeySet ks) {

        PlayerController pc = new PlayerController(handler, ks);
        this.player = new NetPlayer(Vector2D.ZERO, Vector2D.DOWN,
                pc, GameObject.ID.Player,
                Player.ColorPreset.CP1
                , name, null, -1);
    }

    synchronized void stop() {
        try {
            thread.join();
            running = false;
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        window.setVisible(true);
        this.requestFocus();
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1_000_000_000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0, ticks = 0;
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                tick();
                ticks++;
                delta--;
            }
            if (running) {

                paint();
                frames++;
            }

            if (System.currentTimeMillis() - timer > 1000) {
                timer = timer + 1000;
                window.setTitle(String.format("FPS: % 3d ticks: % 3d%n - %s", frames, ticks, this.player.getName() + (runningServer ? " (SERVER)" : "")));
                frames = 0;
                ticks = 0;
            }
        }
        stop();
    }


    private void tick() {
        if(runningServer) socketServer.tick();
        handler.tick();
    }

    public void paint() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        this.paint(g);
        g.dispose();
        bs.show();
    }

    public static Handler getHandler() {
        return Game.handler;
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setTransform(Handler.getDrawTransform());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        handler.paint(g2d);
        handler.gizmosPaint(g2d);
//        font.draw(g2d, "TEST", new Vector2D(100, 100), 1);
        //font.draw(g2d, "TEST", new Vector2D(100, 100), 0.5, TextAlignment.CENTER);
        //font.draw(g2d, "TEST", new Vector2D(150, 150), 2);
        //hud.paint(g2d);
    }
}