package com.game.engine;

import com.game.Hud;
import com.game.Mouse;
import com.game.engine.graphics.Font;
import com.game.engine.graphics.SpriteSheet;
import com.game.engine.math.Vector2D;
import com.game.engine.math.raytracing.RayCaster;
import com.game.layers.Layer;
import com.game.player.Player;
import com.game.player.PlayerKeyListener;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Random;

public class Game extends Canvas implements Runnable {
    //https://youtu.be/Urg8AEIVyWA?t=8m45s
    private static final long serialVersionUID = -240840600533728354L;
    public static Random rand = new Random();

    public static final int WIDTH = 800, HEIGHT = WIDTH / 16 * 9;
    private Thread thread;
    private boolean running = false;
    private static Handler handler;
    private Hud hud;
    public static Game instance;


    public static void main(String[] args) {
        instance = new Game();
        instance.init();
        instance.start();
    }

    public Game() {
        handler = new Handler(WIDTH, HEIGHT, this, rand);
        Player.fromPreset(Player.Preset.P1);
        Player.fromPreset(Player.Preset.P2);
    }

    public static Font font;

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    public synchronized void start() {
        thread = new Thread(this);
        running = true;
        thread.start();
    }

    private void init() {
        this.addKeyListener(new PlayerKeyListener());
        Mouse mcl = new Mouse();
        this.addMouseListener(mcl);
        this.addMouseMotionListener(mcl);
        //Player.fromPreset(Player.Preset.P3);
        //hud = new Hud(handler.getActivePlayer(GameObject.ID.Player1), new Vector2D(-Game.WIDTH/2. + 16, Game.HEIGHT/2. -16));
        //new Wall(Vector2D.ZERO);
        //new RayCaster(Vector2D.ZERO,Math.PI*3/2);
        RayCaster rc = new RayCaster(Vector2D.ZERO, Layer.EVERYTHING, h -> h.allGameObjects(Layer.not(Layer.ENVIRONMENT)));
        font = Font.fromSpriteSheet(new SpriteSheet("graphics/Font.png", 30, 36));

        new Window("Tank Trouble", this);
    }

    public synchronized void stop() {
        try {
            thread.join();
            running = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        this.setVisible(true);
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
            if (running)
                paint();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer = timer + 1000;
                System.out.printf("FPS: %d ticks: %d%n", frames, ticks);
                frames = 0;
                ticks = 0;
            }
        }
        stop();
    }


    private void tick() {
        handler.tick();
        //hud.tick();
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
        font.draw(g2d, "TEST", new Vector2D(100, 100), 1);
        //font.draw(g2d, "TEST", new Vector2D(100, 100), 0.5, TextAlignment.CENTER);
        //font.draw(g2d, "TEST", new Vector2D(150, 150), 2);
        //hud.paint(g2d);
    }
}