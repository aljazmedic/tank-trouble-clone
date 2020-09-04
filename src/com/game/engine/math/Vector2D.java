package com.game.engine.math;


import com.game.engine.Util;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class Vector2D extends Point2D.Double {
    public double x, y;
    public double r, phi;

    final public static Vector2D DOWN = new Vector2D(0, -1);
    final public static Vector2D UP = new Vector2D(0, 1);
    final public static Vector2D RIGHT = new Vector2D(1, 0);
    final public static Vector2D ZERO = new Vector2D(0, 0);
    final public static Vector2D IDENTITY = new Vector2D(1, 1);

    public Vector2D(double x, double y) {
        super(x, y);
        this.x = x;
        this.y = y;
        this.updateFromRec();
    }

    public Vector2D(double[] coords) {
        if (coords.length != 2) throw new Error("Invalid coords length");
        this.x = coords[0];
        this.y = coords[1];
        this.updateFromRec();
    }

    public Vector2D(float[] coords) {
        if (coords.length != 2) throw new Error("Invalid coords length");
        this.x = coords[0];
        this.y = coords[1];
        this.updateFromRec();
    }

    public Vector2D(Point2D p2d) {
        set(p2d.getX(), p2d.getY());
        this.updateFromRec();
    }

    protected Vector2D set(double x, double y) {
        this.x = x;
        this.y = y;
        this.updateFromRec();
        return this;
    }

    public boolean insideBox(double x, double y, double w, double h) {
        return this.x > x && this.x < x + w && this.y > y && this.y < y + h;
    }

    protected void updateFromPol() {
        this.x = Math.cos(phi) * r;
        this.y = Math.sin(phi) * r;
    }

    protected void updateFromRec() {
        this.r = this.mag();
        this.phi = this.angleBetweenRight();
    }

    public static Vector2D fromAngle(double phi) {
        return (Vector2D) Vector2D.UP.copy().setAngle(phi);
    }

    public Vector2D normalize() {
        return normalize(1);
    }

    public Vector2D normalize(double newMag) {
        return this.mul(newMag / this.mag());
    }

    public Vector2D copy() {
        return new Vector2D(this.x, this.y);
    }

    public Vector2D transform(AffineTransform at) {
        Point2D p2d = at.transform(this, null);
        return set(p2d.getX(), p2d.getY());
    }

    public Vector2D translate(double x, double y) {
        return add(new Vector2D(x, y));
    }

    public Vector2D scale(double x, double y) {
        return set(this.x * x, this.y * y);
    }

    public Vector2D rotate(double a) {
        return this.setAngle(this.phi + a);
    }

    public double dist(Vector2D other) {
        return this.copy().sub(other).mag();
    }

    public String coordsString() {
        return String.format("%.2f, %.2f", x, y);
    }

    public String toString() {
        return String.format("V2D(%s, r=%.2f, \u03C6=%.2fr)", coordsString(), r, this.phi);
    }

    public Vector2D reflect(Vector2D normal) {
        return this.copy().sub(normal.copy().mul(2 * this.dot(normal)));
    }

    public Vector2D perpendicularR() {
        return this.copy().setAngle(this.phi - Math.PI / 2);
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
            ;
        }
        return v2dR;
    }

    public Vector2D add(Vector2D other) {
        return set(this.x + other.x, this.y + other.y);
    }

    public double mag() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }


    public Vector2D sub(Vector2D other) {
        return set(this.x - other.x, this.y - other.y);
    }

    public Vector2D mul(double factor) {
        return set(this.x * factor, this.y * factor);
    }

    protected double angleBetweenRight() {
        double angle = Math.atan2(this.y, this.x);
        return Util.modClamp(angle, 2 * Math.PI);
    }

    public double dot(Vector2D other) {
        return this.x * other.x + this.y * other.y;
    }

    public Vector2D setAngle(double phi) {
        this.phi = Util.modClamp(phi, 2 * Math.PI);
        this.r = this.mag();
        updateFromPol();
        return this;
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
        set(x, y);
    }

    public static Vector2D mirrorVector(Vector2D d, Vector2D normal) {
        return d.copy().sub(normal.copy().normalize().mul(2 * normal.dot(d)));
    }
}
