package com.mrshamshir.a2dhelicoptergame;

import android.graphics.Bitmap;
import android.graphics.Canvas;



public class Explosion {
    private int x;
    private int y;
    private int row;
    private Animation animation = new Animation();

    public Explosion(Bitmap res, int x, int y, int width, int height, int numFrames) {
        this.x = x;
        this.y = y;
        Bitmap[] image = new Bitmap[numFrames];

        for (int i = 0; i < image.length; i++) {
            if (i % 5 == 0 && i > 0) row++;
            image[i] = Bitmap.createBitmap(res, (i - (5 * row)) * width, row * height, width, height);

        }
        animation.setFrames(image);
        animation.setDelay(10);
    }

    public void update() {
        if (!animation.isPlayedOnce()) {
            animation.update();
        }
    }

    public void draw(Canvas canvas) {
        if ( !animation.isPlayedOnce()){
            canvas.drawBitmap(animation.getImage() , x, y, null);
        }
    }

}
