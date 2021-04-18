package com.mrshamshir.a2dhelicoptergame;

import android.graphics.Bitmap;
import android.graphics.Canvas;


public class Background {
    private Bitmap resource;
    private int x, dx;

    public Background(Bitmap res) {
        this.resource = res;
        this.dx = GamePanel.MOVESPEED;
    }

    public void update() {
        x += dx;
        if (x < -GamePanel.WIDTH) {
            x = 0;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(resource, x, 0, null);
        if (x < 0) {
            canvas.drawBitmap(resource, x + GamePanel.WIDTH, 0, null);
        }
    }

}