package com.mrshamshir.a2dhelicoptergame;

import android.graphics.Rect;

public abstract class GameObject {
    protected int x;
    protected int y;
    protected int dx;
    protected int dy;
    protected int width;
    protected int height;

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHeight() {
        return height;
    }

    public Rect getRectangle() {
        return new Rect(x, y, x + width, y + height);
    }

}
