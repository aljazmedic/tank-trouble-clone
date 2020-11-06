package com.game.powerups;

import com.game.engine.Game;
import com.game.engine.GameObject;
import com.game.engine.collision.Collidable;
import com.game.engine.collision.Collider;
import com.game.engine.math.Cooldown;
import com.game.engine.math.Vector2D;
import com.game.net.packets.Packet;
import com.game.net.packets.Packet04PowerupSpawn;
import com.game.net.packets.Serializable;
import com.game.player.Player;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.nio.ByteBuffer;
import java.util.Random;

public abstract class Powerup extends GameObject implements Collidable {

    public static final int SIZE = 14;
    private Powerup.Type powerupType;
    public Player holder;
    protected Cooldown timer;
    protected Color color;
    protected Collider collider;

    public Powerup(Powerup.Type t, Vector2D pos, int time) {
        super(pos, ID.Powerup);
        powerupType = t;
        holder = null;
        timer = new Cooldown(time);
        color = Color.BLACK;
        collider = new Collider(this, new Shape[]{new Ellipse2D.Double(-SIZE / 2., -SIZE / 2., SIZE, SIZE)});
    }

    public Powerup(Powerup.Type t,Random rand, int time) {
        this(t, new Vector2D(
                (rand.nextDouble() * Game.WIDTH) - Game.WIDTH / 2.,
                (rand.nextDouble() * Game.HEIGHT) - Game.HEIGHT / 2.
        ), time);
    }

    public boolean hasExpired() {
        return this.pickedUp() && this.timer.timedOut();
    }


    public void onPickup() {
    }

    public void onTimeout() {
    }

    public boolean pickedUp() {
        return holder != null;
    }

    @Override
    public void tick() {
    }

    @Override
    public void paint(Graphics2D g) {
        Shape s = new Ellipse2D.Double(-SIZE / 2., -SIZE / 2., SIZE, SIZE);
        g.setColor(this.color);
        g.fill(transform.getAffineTransform().createTransformedShape(s));
    }

    @Override
    public Collider getCollider() {
        return this.collider;
    }

    public boolean canRecieve(Player player) {
        if (!(this instanceof SinglePowerupAAT)) return true;
        int consecutiveNum = ((SinglePowerupAAT) this).getConsecutiveNum();
        return (player.powerupsApplied & (1 << consecutiveNum)) == 0;
    }

    public static void give(Powerup p, Player player) {
        p.holder = player;
        p.timer.reset();
        if (!(p instanceof SinglePowerupAAT)) return;
        player.powerupsApplied |= 1 << ((SinglePowerupAAT) p).getConsecutiveNum();
    }

    public static void takeAway(Powerup p, Player player) {
        if (!(p instanceof SinglePowerupAAT)) return;
        player.powerupsApplied &= ~(1 << ((SinglePowerupAAT) p).getConsecutiveNum());
        Game.getHandler().removeObject(p);
    }

    public Packet04PowerupSpawn craftPacket() {
        return new Packet04PowerupSpawn(this.powerupType.getPowerupId(), this.transform);
    }

    public Type getPowerupType() {
        return powerupType;
    }


    public enum Type implements Serializable {
        HEAL("he"),
        SPEED("sp"),
        INVALID("ff");

        private final String powerupId;

        Type(String id) {
            this.powerupId = id;
        }

        public String getPowerupId() {
            return powerupId;
        }

        @Override
        public String toString() {
            return this.powerupId;
        }

        public static Type lookup(byte[] startBytes) {
            ByteBuffer bb = ByteBuffer.wrap(startBytes);
            return lookup("" + bb.getChar() + bb.getChar());
        }

        public static Type lookup(String id) {
            for (Type pt : Type.values()) {
                if (pt.getPowerupId().equals(id)) return pt;
            }
            return INVALID;
        }

        @Override
        public byte[] toByteCode() {
            byte[] o = new byte[getNumberOfBytes()];
            ByteBuffer bb = ByteBuffer.wrap(o);
            for (char c : this.powerupId.toCharArray()) {
                bb.putChar(c);
            }
            return o;
        }

        @Override
        public Serializable fromByteCode(ByteBuffer data) throws Packet.InvalidPacketException {
            return lookup(String.valueOf(data.getChar()) + data.getChar());
        }

        @Override
        public int getNumberOfBytes() {
            return 2 * Character.BYTES;
        }
    }
}
