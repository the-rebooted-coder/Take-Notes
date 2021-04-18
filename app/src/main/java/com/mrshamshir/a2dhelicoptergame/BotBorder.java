package com.mrshamshir.a2dhelicoptergame;

import android.graphics.Bitmap;
import android.graphics.Canvas;


public class BotBorder extends GameObject {
    private Bitmap resource;

    public BotBorder(Bitmap res, int x, int y) {
        super.height = 200;
        super.width = 20;
        super.x = x;
        super.y = y;
        super.dx = GamePanel.MOVESPEED;
        this.resource = Bitmap.createBitmap(res, 0, 0, width, height);
    }

    public void update() {
        x += dx;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(resource, x, y, null);
    }
}
