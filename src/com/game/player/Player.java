package com.game.player;

import com.game.DebugUtil;
import com.game.bullet.BouncyBullet;
import com.game.bullet.Bullet;
import com.game.bullet.Shooter;
import com.game.engine.Dyable;
import com.game.engine.Game;
import com.game.engine.GameObject;
import com.game.engine.Util;
import com.game.engine.collision.Collidable;
import com.game.engine.collision.Collider;
import com.game.engine.math.StackCooldown;
import com.game.engine.math.Transform;
import com.game.engine.math.Vector2D;
import com.game.net.packets.Packet;
import com.game.net.packets.Serializable;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.nio.ByteBuffer;
import java.util.Random;

public class Player extends GameObject implements Shooter, Dyable, Collidable {
    private double health;
    private final double maxHealth;
    private final static int SIZE = 32;
    protected PlayerController pc;
//    private final static SpriteSheet SPRITE_SHEET = new SpriteSheet("graphics/PlayerSpriteSheet.png", 36, 36);

    //DRAWING COLLISION
    protected ColorPreset colors;
    private BodyPart[] bodyParts;
    private Collider collider;

    //HUD
    private BodyPart lifeHud;
    private static Vector2D hudOffset = Vector2D.DOWN.asMag(2 * SIZE);

    private static Vector2D textOffset = new Vector2D(-0.75, -.5).mul(SIZE);
    static double textSize = 0.01 * SIZE;

    private static Vector2D lifeOffset = new Vector2D(0.56, -0.125).mul(SIZE);
    private static Vector2D lifeSize = new Vector2D(0.93, 0.23).mul(SIZE);

    //SHOOTING
    private Vector2D bulletShootingOffset = Vector2D.RIGHT.asMag(3 / 4. * SIZE);
    private final static double COOLDOWN = 0.2;
    private StackCooldown bulletCd;

    //POWERUPS
    private double[] moveMatrix = new double[]{-0.07, 2, 1};
    public int powerupsApplied;
//    Sprite hud = Sprite.fromSpriteSheet(SPRITE_SHEET, 0, 2);
protected String name;
//    private static Sprite[] bulletSprites = Sprite.arrayFromSpriteSheet(SPRITE_SHEET, 30, 36);
//    private static Sprite[] bulletCdSprites = Sprite.arrayFromSpriteSheet(SPRITE_SHEET, 40, 47);

    public Player(Vector2D pos, Vector2D vel, PlayerController pc, ID id, ColorPreset c, String name) {
        super(pos, vel.asMag(1), id);
        this.pc = pc;
        this.colors = c;
        this.createBodyParts();
        powerupsApplied = 0;
        bulletCd = new StackCooldown(COOLDOWN, 5, 20 * COOLDOWN);
        maxHealth = 100;
        health = maxHealth;
        this.name = name;
    }

    public Player(Vector2D pos, KeySet ks, ID id, ColorPreset cp, String name){
        this(pos, Vector2D.DOWN, new PlayerController(Game.getHandler(), ks), id, cp, name);
    }

    private Player(Vector2D pos, PlayerController pc, ID id, ColorPreset cp) {
        this(pos, Vector2D.DOWN, pc, id, cp, id.toString());
    }

    public static void fromPreset(Preset p) {
        new Player(p.startPos, new PlayerController(Game.getHandler(), p.ks), p.id, p.cols);
    }

    private void createBodyParts() {
        bodyParts = new BodyPart[]{
                new BodyPart(this, new Rectangle(-SIZE / 2, -SIZE / 2, SIZE, SIZE), colors.getPrimary()), //Body
                new BodyPart(this, new Rectangle(0, -SIZE / 4, SIZE, SIZE / 2), colors.getSecondary()),//Nose
                new BodyPart(this, new Ellipse2D.Double(-SIZE / 3., -SIZE / 3., SIZE * 2. / 3., SIZE * 2. / 3.), colors.getSecondary()),
                new BodyPart(this, new Ellipse2D.Double(-1, -1, 2, 2), Color.black)};
        collider = new Collider(this, new Rectangle(-SIZE / 2, -SIZE / 2, SIZE, SIZE));
        lifeHud = new BodyPart(this, new Rectangle(-SIZE, 0, 2 * SIZE, SIZE / 2), new Color(0x00B220));
    }

    public void tick() {
        //Moving
        boolean[] pressedKeys = pc.getPressedKeys();
        this.move(pressedKeys);
        //transform.position.maxClamp(Game.WIDTH - SIZE/2., Game.HEIGHT - SIZE/2.);
        /*if (this.getId() == ID.Player1) {
        return;}*/
        //Shooting
        if (pressedKeys[4] && this.canShoot()) {
            this.shoot();
        }
    }

    private void drawBody(Graphics2D g) {
        for (BodyPart bodyPart : bodyParts) {
            bodyPart.fill(g);
        }
    }
//
//    public void drawLife(Graphics2D g) {
//        AffineTransform hudTransform = new AffineTransform();
//        Vector2D hudPos = transform.position.add(hudOffset);
//        hudTransform.translate(hudPos.x, hudPos.y);
//        hudTransform.scale(SIZE / 20., SIZE / 20.);
//        hud.draw(g, hudTransform);
//        g.setColor(lifeHud.c);
//        ((Rectangle) lifeHud.s).setSize((int) (lifeSize.x * (this.health / this.maxHealth)), (int) lifeSize.y);
//
//        AffineTransform lifeTransform = new AffineTransform(hudTransform);
//        lifeTransform.translate(lifeOffset.x, lifeOffset.y);
//        g.fill(lifeTransform.createTransformedShape(lifeHud.s));
//
//        //Bullets
//        int timesLeft = bulletCd.getTimesLeft();
//        if (timesLeft > 0) {
//            Sprite currBulletSprite = bulletSprites[timesLeft];
//            currBulletSprite.draw(g, hudTransform);
//        } else {
//            int n = (int) ((bulletCdSprites.length - 1) * (1 - bulletCd.timeLeft() / bulletCd.getBetweenStacksMax()));
//            bulletCdSprites[n].draw(g, hudTransform);
//        }
//        AffineTransform textTransform = new AffineTransform(hudTransform);
//        textTransform.translate(textOffset.x, textOffset.y);
//        Game.font.draw(g, this.name, textTransform, Player.textSize, TextAlignment.LEFT);
//    }

    public void paint(Graphics2D g) {
        drawBody(g);
        //drawLife(g); //TODO UnComment
    }


    @Override
    public void gizmosPaint(Graphics2D g) {
        Vector2D off = transform.position.add(Vector2D.UP.copy().mul(2));
        g.drawString(transform.position.toString()+" "+this.name, (int) off.x, (int) off.y);
        Vector2D shootingOff = this.getBulletOrigin();
        g.fill(new Ellipse2D.Double(shootingOff.x, shootingOff.y, 3, 3));
        DebugUtil.drawVector(g, transform.position, transform.velocity, 2 * SIZE);
    }

    protected void move(boolean[] keysDown) {
        /*Velocity serves as direction vector*/
        boolean[] cantMove = getCantMoveArray();


        if (keysDown[1]) {
            transform.setVelocity(
                    Vector2D.fromPol(transform.velocity.angle() - moveMatrix[0])
            );
        }
        if (keysDown[3]) {
            transform.setVelocity(
                    Vector2D.fromPol(transform.velocity.angle() + moveMatrix[0])
            );
        }
        if (keysDown[2]) {
            transform.move(transform.velocity.asMag(-moveMatrix[2]).restrict(cantMove));
        }
        if (keysDown[0]) {
            transform.move(transform.velocity.asMag(moveMatrix[1]).restrict(cantMove));
        }
    }

    private boolean[] getCantMoveArray() {
        int colliderNumber = Game.getHandler().getScreen().test(this);
        int vertical = colliderNumber % 3;
        int horizontal = (colliderNumber - vertical) / 3;

        return new boolean[]{
                vertical == 2,
                horizontal == 1,
                vertical == 1,
                horizontal == 2
        };
    }

    @Override
    public double getHealth() {
        return this.health;
    }

    @Override
    public double getMaxHealth() {
        return this.maxHealth;
    }

    @Override
    public void setHealth(double newHealth) {
        this.health = newHealth;
        this.health = Util.clamp(health, 0, 100);
    }

    @Override
    public boolean isDead() {
        return this.health <= 0;
    }

    @Override
    public Vector2D getBulletOrigin() {
        //Offset angle correction
        this.bulletShootingOffset.setAngle(this.transform.velocity.angle());
        return transform.position.add(bulletShootingOffset);
    }

    @Override
    public boolean canShoot() {
        return this.bulletCd.timedOut();
    }

    @Override
    public double getBulletSpawnAngle() {
        return transform.velocity.angle();
    }

    @Override
    public Bullet shoot() {
        BouncyBullet b = new BouncyBullet(this);
        Game.getHandler().addObject(b);
        this.bulletCd.reset();
        return b;
    }

    @Override
    public void onDie() {
        Game.getHandler().removeObject(this);
    }

    /// collision
    @Override
    public void onScreenExit(int h, int v) {
        System.out.println("PLAYER EXIT " + h + " " + v);
    }

    @Override
    public Collider getCollider() {
        return this.collider;
    }

    public double[] getMoveMatrix() {
        return this.moveMatrix;
    }

    public void setMoveMatrix(double[] newMM) {
        this.moveMatrix = newMM;
    }

    public String getName() {
        return this.name;
    }

    public void setTransform(Transform t) {
        this.transform =t;
        t.resetChanges();
    }

    public ColorPreset getColors() {
        return this.colors;
    }


    public enum Preset {
        P1(ID.Player, ColorPreset.CP1, new Vector2D(-Game.WIDTH * 1. / 4., -Game.HEIGHT * 1. / 4.), KeySet.KEY_SET1),
        P2(ID.Player, ColorPreset.CP2, new Vector2D(0, Game.HEIGHT * 1. / 4.), KeySet.KEY_SET2),
        P3(ID.Player, ColorPreset.CP3, new Vector2D(Game.WIDTH * 1. / 4., -Game.HEIGHT * 1. / 4.), KeySet.KEY_SET3);
        public final ColorPreset cols;
        public final ID id;
        public final KeySet ks;
        public final Vector2D startPos;

        Preset(ID id, ColorPreset cp, Vector2D startPos, KeySet ks) {
            this.id = id;
            this.cols = cp;
            this.startPos = startPos;
            this.ks = ks;
        }
    }

    public enum ColorPreset implements Serializable<ColorPreset> {
        CP1(1, new Color(0xFF2A2F), new Color(0x44FF37)),
        CP2(2,new Color(0x2638FF), new Color(0xFF8F4C)),
        CP3(3,new Color(0x8A2CFF), new Color(0xFFFE4E));
        private static ColorPreset[] all = new ColorPreset[]{CP1, CP2, CP3};
        private byte index;
        private Color c1;
        private Color c2;

        ColorPreset(int idx,Color c1, Color c2){
            index = (byte)idx;
            this.c1 = c1;
            this.c2 = c2;
        }

        public static ColorPreset pickRandom(Random r){
            return all[r.nextInt(all.length)];
        }

        public Color getPrimary(){return this.c1;}
        public Color getSecondary(){return this.c2;}

        @Override
        public byte[] toByteCode() {
            return new byte[]{index};
        }

        @Override
        public ColorPreset fromByteCode(ByteBuffer data) throws Packet.InvalidPacketException {
            byte index = data.get();
            if(index >= all.length) index = 0;
            return all[index];
        }

        @Override
        public int getNumberOfBytes() {
            return 1;
        }
    }

    static class BodyPart {
        private final GameObject player;
        private final Shape s;
        private final Color c;

        BodyPart(GameObject player, Shape s, Color c) {
            this.s = s;
            this.c = c;
            this.player = player;
        }

        void draw(Graphics2D g) {
            g.setColor(c);
            g.draw(player.getTransform().getAffineTransform().createTransformedShape(s));
        }

        void fill(Graphics2D g) {
            g.setColor(c);
            g.fill(player.getTransform().getAffineTransform().createTransformedShape(s));
        }
    }
}
