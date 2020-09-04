package com.game.player;

import com.game.DebugUtil;
import com.game.bullet.BouncyBullet;
import com.game.bullet.Shooter;
import com.game.engine.Dyable;
import com.game.engine.Game;
import com.game.engine.GameObject;
import com.game.engine.Util;
import com.game.engine.collision.Collidable;
import com.game.engine.collision.Collider;
import com.game.engine.graphics.Sprite;
import com.game.engine.graphics.SpriteSheet;
import com.game.engine.math.StackCooldown;
import com.game.engine.math.Vector2D;
import javafx.scene.text.TextAlignment;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

public class Player extends GameObject implements Shooter, Dyable, Collidable {
    private double health;
    private final double maxHealth;
    final static int SIZE = 32;
    PlayerController pc;
    private final static SpriteSheet SPRITE_SHEET = new SpriteSheet("graphics/PlayerSpriteSheet.png", 36, 36);

    //DRAWING COLLISION
    private Color[] colors;
    private BodyPart[] bodyParts;
    private Collider collider;

    //HUD
    private BodyPart lifeHud;
    private static Vector2D hudOffset = Vector2D.DOWN.copy().normalize(2 * SIZE);

    private static Vector2D textOffset = new Vector2D(-0.75, -.5).mul(SIZE);
    static double textSize = 0.01 * SIZE;

    private static Vector2D lifeOffset = new Vector2D(0.56, -0.125).mul(SIZE);
    private static Vector2D lifeSize = new Vector2D(0.93, 0.23).mul(SIZE);

    //SHOOTING
    private Vector2D bulletShootingOffset = Vector2D.RIGHT.copy().normalize(3 / 4. * SIZE);
    final static double COOLDOWN = 0.2;
    StackCooldown bulletCd;

    //POWERUPS
    double[] moveMatrix = new double[]{-0.07, 2, 1};
    public int powerupsApplied;
    Sprite hud = Sprite.fromSpriteSheet(SPRITE_SHEET, 0, 2);
    String name;
    private static Sprite[] bulletSprites = Sprite.arrayFromSpriteSheet(SPRITE_SHEET, 30, 36);
    private static Sprite[] bulletCdSprites = Sprite.arrayFromSpriteSheet(SPRITE_SHEET, 40, 47);

    public Player(Vector2D pos, KeySet ks, ID id, Color[] c, String name) {
        super(pos, Vector2D.DOWN, id);
        this.pc = new PlayerController(this, ks);
        this.colors = c;
        this.createBodyParts();
        powerupsApplied = 0;
        bulletCd = new StackCooldown(COOLDOWN, 5, 20 * COOLDOWN);
        maxHealth = 100;
        health = maxHealth;
        this.name = name;
    }

    public Player(Vector2D pos, KeySet ks, ID id, Color[] c) {
        this(pos, ks, id, c, id.toString());
    }

    public static void fromPreset(Preset p) {
        new Player(p.startPos, p.ks, p.id, p.cols);
    }

    private void createBodyParts() {
        bodyParts = new BodyPart[]{
                new BodyPart(this, new Rectangle(-SIZE / 2, -SIZE / 2, SIZE, SIZE), colors[0]), //Body
                new BodyPart(this, new Rectangle(0, -SIZE / 4, SIZE, SIZE / 2), colors[1]),//Nose
                new BodyPart(this, new Ellipse2D.Double(-SIZE / 3., -SIZE / 3., SIZE * 2. / 3., SIZE * 2. / 3.), colors[1]),
                new BodyPart(this, new Ellipse2D.Double(-1, -1, 2, 2), Color.black)};
        collider = new Collider(this, new Rectangle(-SIZE / 2, -SIZE / 2, SIZE, SIZE));
        lifeHud = new BodyPart(this, new Rectangle(-SIZE, 0, 2 * SIZE, SIZE / 2), new Color(0x00B220));
    }

    public void tick() {
        //Moving
        this.move(pc.keySet.pressedKeys);
        //transform.position.maxClamp(Game.WIDTH - SIZE/2., Game.HEIGHT - SIZE/2.);
        /*if (this.getId() == ID.Player1) {
        return;}*/
        //Shooting
        if (pc.keySet.pressedKeys[4] && this.canShoot()) {
            this.shoot();
        }
    }

    private void drawBody(Graphics2D g) {
        for (BodyPart bodyPart : bodyParts) {
            bodyPart.fill(g);
        }
    }

    public void drawLife(Graphics2D g) {
        AffineTransform hudTransform = new AffineTransform();
        Vector2D hudPos = (Vector2D) transform.position.copy().add(hudOffset);
        hudTransform.translate(hudPos.x, hudPos.y);
        hudTransform.scale(SIZE / 20., SIZE / 20.);
        hud.draw(g, hudTransform);
        g.setColor(lifeHud.c);
        ((Rectangle) lifeHud.s).setSize((int) (lifeSize.x * (this.health / this.maxHealth)), (int) lifeSize.y);

        AffineTransform lifeTransform = new AffineTransform(hudTransform);
        lifeTransform.translate(lifeOffset.x, lifeOffset.y);
        g.fill(lifeTransform.createTransformedShape(lifeHud.s));

        //Bullets
        int timesLeft = bulletCd.getTimesLeft();
        if (timesLeft > 0) {
            Sprite currBulletSprite = bulletSprites[timesLeft];
            currBulletSprite.draw(g, hudTransform);
        } else {
            int n = (int) ((bulletCdSprites.length - 1) * (1 - bulletCd.timeLeft() / bulletCd.getBetweenStacksMax()));
            bulletCdSprites[n].draw(g, hudTransform);
        }
        AffineTransform textTransform = new AffineTransform(hudTransform);
        textTransform.translate(textOffset.x, textOffset.y);
        Game.font.draw(g, this.name, textTransform, Player.textSize, TextAlignment.LEFT);
    }

    public void paint(Graphics2D g) {
        drawBody(g);
        //drawLife(g); //TODO UnComment
    }


    @Override
    public void gizmosPaint(Graphics2D g) {
        Vector2D off = (Vector2D) transform.position.copy().add(Vector2D.UP);
        g.drawString(transform.position.toString(), (int) off.x, (int) off.y);
        Vector2D shootingOff = this.getBulletOrigin();
        g.fill(new Ellipse2D.Double(shootingOff.x, shootingOff.y, 3, 3));
        DebugUtil.drawVector(g, transform.position, transform.velocity, 2 * SIZE);
    }

    public void move(boolean[] keysDown) {
        /*Velocity serves as direction vector*/
        boolean[] cantMove = getCantMoveArray();

        /*if (this.getId() == ID.Player1) {
            Vector2D ur = (new Vector2D(keysDown[1] ? 1 : 0, keysDown[0] ? 1 : 0));
            Vector2D dl = (new Vector2D(keysDown[3] ? -1 : 0, keysDown[2] ? -1 : 0));
            if(keysDown[4]){
                lifeSize.x-=0.1;
            }
            Player.lifeOffset.add(ur).add(dl);
            System.out.println(Player.lifeOffset + "\n" + Player.lifeSize);
            return;
        }*/

        if (keysDown[1]) {
            transform.velocity.setAngle(transform.velocity.phi - moveMatrix[0]);
        }
        if (keysDown[3]) {
            transform.velocity.setAngle(transform.velocity.phi + moveMatrix[0]);
        }
        if (keysDown[2]) {
            Vector2D addition = transform.velocity.copy().normalize(-moveMatrix[2]).restrict(cantMove);
            transform.position.add(addition);
        }
        if (keysDown[0]) {
            Vector2D addition = transform.velocity.normalize(moveMatrix[1]).restrict(cantMove);
            transform.position.add(addition);
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
        this.bulletShootingOffset.setAngle(this.transform.velocity.phi);
        return (Vector2D) transform.position.copy().add(bulletShootingOffset);
    }

    @Override
    public boolean canShoot() {
        return this.bulletCd.timedOut();
    }

    @Override
    public double getBulletSpawnAngle() {
        return transform.velocity.phi;
    }

    @Override
    public void shoot() {
        new BouncyBullet(this, 20);
        this.bulletCd.reset();
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

    public enum Preset {
        P1(ID.Player1, new Color(0xFF2A2F), new Color(0x44FF37), new Vector2D(-Game.WIDTH * 1. / 4., -Game.HEIGHT * 1. / 4.), KeySet.KEY_SET1),
        P2(ID.Player2, new Color(0x2638FF), new Color(0xFF8F4C), new Vector2D(0, Game.HEIGHT * 1. / 4.), KeySet.KEY_SET2),
        P3(ID.Player3, new Color(0x8A2CFF), new Color(0xFFFE4E), new Vector2D(Game.WIDTH * 1. / 4., -Game.HEIGHT * 1. / 4.), KeySet.KEY_SET3);
        public final Color[] cols;
        public final ID id;
        public final KeySet ks;
        public final Vector2D startPos;

        Preset(ID id, Color col1, Color col2, Vector2D startPos, KeySet ks) {
            this.id = id;
            this.cols = new Color[]{col1, col2};
            this.startPos = startPos;
            this.ks = ks;
        }
    }

    static class BodyPart {
        private final GameObject go;
        private final Shape s;
        private final Color c;

        public BodyPart(GameObject go, Shape s, Color c) {
            this.s = s;
            this.c = c;
            this.go = go;
        }

        public void draw(Graphics2D g) {
            g.setColor(c);
            g.draw(go.getTransform().getAffineTransform().createTransformedShape(s));
        }

        public void fill(Graphics2D g) {
            g.setColor(c);
            g.fill(go.getTransform().getAffineTransform().createTransformedShape(s));
        }
    }
}
