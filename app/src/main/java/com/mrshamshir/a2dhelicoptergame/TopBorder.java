package com.mrshamshir.a2dhelicoptergame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class TopBorder extends GameObject {

    private Bitmap resource;

    public TopBorder(Bitmap res, int x, int y, int h) {
        super.height = h;
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
        try {
            canvas.drawBitmap(resource, x, y, null);

        } catch (Exception e) {
        }
    }

}
