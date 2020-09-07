package com.game.engine;

import com.game.bullet.Bullet;
import com.game.engine.math.Cooldown;
import com.game.engine.math.Transform;
import com.game.engine.math.Vector2D;
import com.game.engine.math.raytracing.Ray;
import com.game.engine.math.raytracing.RayCastable;
import com.game.layers.CombinedLayerFilterIterator;
import com.game.layers.Layer;
import com.game.layers.LayerFilterIterator;
import com.game.net.NetPlayer;
import com.game.player.Player;
import com.game.Screen;
import com.game.powerups.HealPowerup;
import com.game.powerups.Powerup;
import com.game.powerups.SpeedPowerup;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Handler {
    private final Game game;
    private final ArrayList<GameObject> objects;
    private final ArrayList<Bullet> bullets;
    private final ArrayList<Powerup> powerups;
    private final ArrayList<RayCastable> rayCasters;

    private final Lock objectsLock = new ReentrantLock();
    private final Lock bulletsLock = new ReentrantLock();
    private final Lock powerupsLock = new ReentrantLock();

    private Screen screen;

    private Cooldown powerupSpawner;
    public final Random rand;

    private ExecutorService executor;

    public Handler(int w, int h, Game game, Random r) {
        this.game = game;
        this.objects = new ArrayList<>();
        this.bullets = new ArrayList<>();
        this.powerups = new ArrayList<>();
        this.rayCasters = new ArrayList<>();
        this.screen = new Screen(this, w, h);
        this.rand = r;
        this.powerupSpawner = new Cooldown(10);
        executor = Executors.newFixedThreadPool(2);
    }

    public Game getGame() {
        return this.game;
    }

    public void stopGame() {
        this.getGame().stop();
    }


    public synchronized void tick() {
        powerupsLock.lock();
        for (int PUIdx = powerups.size() - 1; PUIdx >= 0; PUIdx--) {
            Powerup p = powerups.get(PUIdx);
            powerups.get(PUIdx).tick();

            if (p.hasExpired()) {
                Powerup.takeAway(p, p.holder);
                p.onTimeout();
            }
            for (Iterator<GameObject> goIter = getIterator(); goIter.hasNext(); ) {
                GameObject go = goIter.next();
                if (go instanceof Player) {
                    if (p.canRecieve((Player) go) && !p.pickedUp()) {
                        if (go.doesCollide(p)) {
                            Powerup.give(p, (Player) go);
                            p.onPickup();
                        }
                    }
                }
            }
        }
        powerupsLock.unlock();

        bulletsLock.lock();
        for (int bulletIdx = bullets.size() - 1; bulletIdx >= 0; bulletIdx--) {
            Bullet b = bullets.get(bulletIdx);
            tickAndTrigger(b);
        }
        bulletsLock.unlock();

        objectsLock.lock();
        for (int gameObjectIdx = objects.size() - 1; gameObjectIdx >= 0; gameObjectIdx--) {
            GameObject go = objects.get(gameObjectIdx);
            tickAndTrigger(go);

            if (go instanceof Dyable) {
                if (((Dyable) go).isDead())
                    go.onDie();
            }
        }
        objectsLock.unlock();


        if (powerupSpawner.timedOut()) {
            int N = 2;
            switch (rand.nextInt(N)) {
                case 0:
                    this.addObject(new HealPowerup(rand));
                    break;
                case 1:
                    this.addObject(new SpeedPowerup(rand));
                    break;
            }
            powerupSpawner.reset();
        }
    }

    public void addKeyListener(KeyListener kl) {
        this.getGame().addKeyListener(kl);
    }

    public void paint(Graphics2D g) {
        objectsLock.lock();
        bulletsLock.lock();
        powerupsLock.lock();
        screen.paint(g);
        this.powerups.forEach((p) -> {
            if (!p.pickedUp())
                p.paint(g);
        });
        this.bullets.forEach((p) -> p.paint(g));
        this.objects.forEach(go -> {
            if (Layer.fitsMask(go, Layer.not(Layer.ENVIRONMENT))) {
                go.paint(g);
            }
        });
        objectsLock.unlock();
        bulletsLock.unlock();
        powerupsLock.unlock();
    }

    public void gizmosPaint(Graphics2D g) {
        objectsLock.lock();

        g.setColor(new Color(0xA4FF4AC7, true));
        screen.gizmosPaint(g);

        for (GameObject e : objects) {
            g.setColor(new Color(0xA4FF4AC7, true));
            e.gizmosPaint(g);
            e.colliderGizmosPaint(g);
        }

        for (RayCastable r : this.rayCasters) {
            g.setColor(new Color(0xA4FF2528, true));
            r.debugPaint(g);
            Ray hit = r.createEmptyRay();
            if (r.cast(hit, r.getLayerMask())) {
                g.setColor(new Color(0xA4FF4AC7, true));
                hit.debugPaint(g);
            }
        }
        objectsLock.unlock();
    }

    public ArrayList<GameObject> getGameObjectsById(GameObject.ID id) {
        ArrayList<GameObject> returnArrayList = new ArrayList<>();
        for (Iterator<GameObject> i = getIterator(); i.hasNext(); ) {
            GameObject go = i.next();
            if (go.getId() == id) {
                returnArrayList.add(go);
            }
        }
        return returnArrayList;
    }

    private Iterator<GameObject> getIterator() {
        return objects.iterator();
    }

    private Iterator<GameObject> getIterator(long layerMask) {
        return new LayerFilterIterator(objects.iterator(), layerMask);
    }

    private Iterator<Bullet> getBulletIterator() {
        return bullets.iterator();
    }

    private Iterator<Powerup> getPUIterator() {
        return powerups.iterator();
    }

    public void addObject(GameObject object) {
        executor.submit(() -> {
            switch (object.getId()) {
                case Bullet:
                    try {
                        bulletsLock.lock();
                        this.bullets.add((Bullet) object);
                    } finally {
                        bulletsLock.unlock();
                    }
                    break;
                case Powerup:
                    try {
                        powerupsLock.lock();
                        this.powerups.add((Powerup) object);
                    } finally {
                        powerupsLock.unlock();
                    }
                    break;
                case Wall:
                case Screen:
                default:
                    try {
                        objectsLock.lock();
                        this.objects.add(object);
                    } finally {
                        objectsLock.unlock();
                    }
                    break;
            }
        });
    }

    /*public void addBullet(Bullet object) {
        this.bullets.add(object); //TODO MANAGE EACH CLASS SEPARATELY
    }
    public void addPowerup(Powerup object) {
        this.powerups.add(object);
    }*/

    public void addRayCaster(RayCastable r) {
        this.rayCasters.add(r);
    }

    public void removeObject(GameObject object) {
        executor.submit(() -> {
            switch (object.getId()) {
                case Bullet:
                    this.bullets.remove(object);
                    break;
                case Powerup:
                    this.powerups.remove(object);
                    break;
                case Wall:
                default:
                    this.objects.remove(object);
                    break;
            }
        });
    }

    public static AffineTransform getDrawTransform() {
        AffineTransform at = new AffineTransform();
        at.translate(Game.WIDTH / 2., Game.HEIGHT / 2.);
        return at;
    }

    public static Vector2D transformVector(Vector2D v2d) {
        return v2d.translate(-Game.WIDTH / 2., -Game.HEIGHT / 2.);
    }

    public static ArrayList<GameObject> insideSphere(Transform go, double r) {
        ArrayList<GameObject> returnArrayList = new ArrayList<>();
        for (Iterator<GameObject> i = Game.getHandler().getIterator(); i.hasNext(); ) {
            GameObject go2 = i.next();
            Transform t2 = go2.getTransform();
            if (go.position.dist(t2.position) <= r) {
                returnArrayList.add(go2);
            }
        }
        return returnArrayList;
    }

    public int countActivePlayers() {
        int count = 0;
        for (Iterator<GameObject> i = Game.getHandler().getIterator(); i.hasNext(); ) {
            GameObject.ID thisId = i.next().getId();
            if (GameObject.ID.Player == thisId)
                count++;
        }
        return count;
    }

    public Iterator<GameObject> allGameObjects(long layerMask) {
        return new CombinedLayerFilterIterator(layerMask, this.objects.iterator(), this.getBulletIterator(), this.getPUIterator());
    }

    public Screen getScreen() {
        return screen;
    }

    private void tickAndTrigger(GameObject gameObject) {
        if (gameObject.getId() == GameObject.ID.Screen) {
            gameObject.tick();
            return;
        }
//before check
        boolean[] otherObjects = new boolean[objects.size()];
        int idx = 0;

        for (Iterator<GameObject> goIter = getIterator(); goIter.hasNext(); idx++)
            otherObjects[idx] = gameObject.doesCollide(goIter.next());
        int screenStatus = screen.test(gameObject);


        gameObject.tick();

        //After check

        ///Screen///
        int screenStatus2 = screen.test(gameObject);
        if (gameObject.isInsideScreen()) gameObject.onInsideScreen();
        else gameObject.onOutsideScreen();

        if (screenStatus2 != screenStatus) {
            int v1 = screenStatus % 3;
            int h1 = (screenStatus - v1) / 3;

            int v2 = screenStatus2 % 3;
            int h2 = (screenStatus2 - v2) / 3;

            if (screenStatus == 0) {
                gameObject.onScreenExit(h2, v2);
            } else if (screenStatus2 == 0) {
                gameObject.onScreenEntry(h1, v1);
            }
        }

        ///GameObjects///
        idx = 0;
        for (Iterator<GameObject> goIter = getIterator(); goIter.hasNext(); idx++) {
            GameObject testGameObject = goIter.next();
            boolean afterCollides = gameObject.doesCollide(testGameObject);
            boolean beforeCollides = otherObjects[idx];
            if (afterCollides && !beforeCollides) gameObject.onEnter(testGameObject);
            else if (!afterCollides && beforeCollides) gameObject.onExit(testGameObject);
            if (afterCollides)
                gameObject.onHit(testGameObject);
        }
    }

    public NetPlayer getNetPlayerByName(String name) {
        for (GameObject go : objects) {
            if (go instanceof NetPlayer) {
                NetPlayer otherNetPlayer = (NetPlayer) go;
                if (otherNetPlayer.getName().equals(name)) {
                    return otherNetPlayer;
                }
            }
        }
        return null;
    }
}