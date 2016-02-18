package br.com.uern.les.sosmovel.controllers;

/**
 * Created by davia on 12/01/2016.
 */
public class Ponto3D {
    private float  x, y, z;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public Ponto3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Ponto3D(){

    }
}
