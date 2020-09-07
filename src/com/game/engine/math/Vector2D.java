package com.game.engine.math;


import com.game.engine.Util;
import com.game.net.packets.Serializable;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.nio.ByteBuffer;

@SuppressWarnings("ALL")
public class Vector2D extends Point2D.Double implements Serializable<Vector2D> {
    public double x, y;

    final public static Vector2D DOWN = new Vector2D(0, -1);
    final public static Vector2D UP = new Vector2D(0, 1);
    final public static Vector2D RIGHT = new Vector2D(1, 0);
    final public static Vector2D ZERO = new Vector2D(0, 0);
    final public static Vector2D IDENTITY = new Vector2D(1, 1);

    public Vector2D(double x, double y) {
        super(x, y);
        this.x = x;
        this.y = y;
    }

    public Vector2D(Vector2D cloneSrc) {
        this(cloneSrc.x, cloneSrc.y);
    }

    public Vector2D(double[] coords) {
        this(coords[0], coords[1]);
    }

    public Vector2D(float[] coords) {
        this(coords[0], coords[1]);
    }

    public Vector2D(Point2D p2d) {
        this(p2d.getX(), p2d.getY());
    }

    public Vector2D() {
        this(0, 0);
    }

    public boolean insideBox(double x, double y, double w, double h) {
        return this.x > x && this.x < x + w && this.y > y && this.y < y + h;
    }

    public Vector2D normalize() {
        return asMag(1);
    }

    public Vector2D asMag(double newMag) {
        return mul(newMag/mag());
    }

    public Vector2D copy() {
        return new Vector2D(this.x, this.y);
    }

    public Vector2D transform(AffineTransform at) {
        Point2D p2d = at.transform(this, null);
        return new Vector2D(p2d);
    }

    public Vector2D translate(double x, double y) {
        return new Vector2D(new Vector2D(x, y));
    }

    public Vector2D scale(double dx, double dy) {
        return new Vector2D(this.x * dx, this.y * dy);
    }

    public Vector2D scale(double k) {
        return mul(k);
    }

    public void rotate(double a) {
        setAngle(this.angle() + a);
    }

    public double dist(Vector2D other) {
        return this.sub(other).mag();
    }

    public String coordsString() {
        return String.format("%.2f, %.2f", x, y);
    }

    public String toString() {
        return String.format("V2D(%s, r=%.2f, \u03C6=%.2fr)", coordsString(), mag(), angle());
    }

    public Vector2D reflect(Vector2D normal) {
        return this.copy().sub(normal.copy().mul(2 * this.dot(normal)));
    }

    public Vector2D perpendicularR() {
        return fromPol(this.angle() - Math.PI / 2);
    }

    public Vector2D restrict(boolean[] exclude) {
        Vector2D v2dR = this.copy();
        if (exclude[0]) { //X+
            v2dR.x = Math.min(0, v2dR.x);
        }
        if (exclude[1]) {//Y+
            v2dR.y = Math.min(0, v2dR.y);
        }
        if (exclude[2]) { //X-
            v2dR.x = Math.max(0, v2dR.x);
        }
        if (exclude[3]) {//Y-
            v2dR.y = Math.max(0, v2dR.y);
        }
        return v2dR;
    }

    public Vector2D add(Vector2D other) {
        return new Vector2D(this.x + other.x, this.y + other.y);
    }

    public double mag() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }


    public Vector2D sub(Vector2D other) {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }

    public Vector2D mul(double factor) {
        return new Vector2D(this.x * factor, this.y * factor);
    }

    public double angle() {
        double angle = Math.atan2(this.y, this.x);
        return Util.modClamp(angle, 2 * Math.PI);
    }

    public double dot(Vector2D other) {
        return this.x * other.x + this.y * other.y;
    }

    public void setAngle(double phi) {
        phi = Util.modClamp(phi, 2 * Math.PI);
        double r = this.mag();
        this.x = Math.cos(phi) * r;
        this.y = Math.sin(phi) * r;
    }

    public static Vector2D fromPol(double phi) {
        return fromPol(phi, 1);
    }

    public static Vector2D fromPol(double phi, double mag) {
        return new Vector2D(Math.cos(phi) * mag, Math.sin(phi) * mag);
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    @Override
    public void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2D mirrorVector(Vector2D d, Vector2D normal) {
        return d.sub(normal.mul(2 * normal.dot(d)));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vector2D) {
            Vector2D othr = (Vector2D) obj;
            return othr.x == this.x && othr.y == this.y;
        } else return false;
    }

    @Override
    public byte[] toByteCode() {
        byte[] ret = new byte[16];
        ByteBuffer bb = ByteBuffer.wrap(ret);
        bb.putDouble(this.x).putDouble(this.y);
        return ret;
    }


    @Override
    public Vector2D fromByteCode(ByteBuffer data) throws InvalidFormatException {
        if (data.remaining() < getNumberOfBytes()) throw new InvalidFormatException(this, data.toString());
        this.x = data.getDouble();
        this.y = data.getDouble();
        return this;
    }

    @Override
    public int getNumberOfBytes() {
        return 2 * java.lang.Double.BYTES;
    }
}
