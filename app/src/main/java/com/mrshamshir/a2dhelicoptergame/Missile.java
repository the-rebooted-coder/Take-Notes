package com.mrshamshir.a2dhelicoptergame;


import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

public class Missile extends GameObject {

    private int speed;
    private Animation animation = new Animation();

    public Missile(Bitmap res, int x, int y, int w, int h, int s, int numFrames) {
        super.x = x;
        super.y = y;
        super.width = w;
        super.height = h;

        // set capped speed depending on current game score
        Random rand = new Random();
        this.speed = 7 + (int) (rand.nextDouble() * s / 30);
        if (speed > 40)
            speed = 40;

        Bitmap[] image = new Bitmap[numFrames];
        for (int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(res, 0, i * height, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(100 - speed);
    }


    public void update() {
        x -= speed;
        animation.update();
    }

    public void draw(Canvas canvas) {
        try {
            canvas.drawBitmap(animation.getImage(), x, y, null);
        } catch (Exception e) {
        }
    }

}
