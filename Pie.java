package com.fmi.example.piefall;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

public class Pie{
    Context context;
    Bitmap image;

    int pieX, pieY, pieVelocity;

    Random random = new Random();
    int[] sprites = {R.drawable.pie1, R.drawable.pie2};

    Pie(Context context){
        this.context = context;

        image = BitmapFactory.decodeResource
                (context.getResources(), sprites[random.nextInt(sprites.length)]);
        resetPosition();
    }


    public int getPieWidth(){
        return image.getWidth();
    }

    public int getPieHeight(){
        return image.getHeight();
    }

    public void resetPosition(){
        pieX = random.nextInt(GameView.dWidth - getPieWidth());
        pieY = -50 + random.nextInt(50) * -1;
        pieVelocity = 20;
        image = BitmapFactory.decodeResource(context.getResources(), sprites[random.nextInt(sprites.length)]);
    }
}
