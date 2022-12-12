package com.fmi.example.piefall;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

public class Bomb{
    Bitmap bomb[] = new Bitmap[12];
    int bombFrame = 0;
    int bombX, bombY, bombVelocity;
    Random random = new Random();

    public Bomb(Context context){
        bomb[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.bomb1);
        bomb[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.bomb2);
        bomb[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.bomb3);
        bomb[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.bomb4);
        bomb[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.bomb5);
        bomb[5] = BitmapFactory.decodeResource(context.getResources(), R.drawable.bomb6);
        bomb[6] = BitmapFactory.decodeResource(context.getResources(), R.drawable.bomb7);
        bomb[7] = BitmapFactory.decodeResource(context.getResources(), R.drawable.bomb8);
        bomb[8] = BitmapFactory.decodeResource(context.getResources(), R.drawable.bomb9);
        bomb[9] = BitmapFactory.decodeResource(context.getResources(), R.drawable.bomb10);
        bomb[10] = BitmapFactory.decodeResource(context.getResources(), R.drawable.bomb11);
        bomb[11] = BitmapFactory.decodeResource(context.getResources(), R.drawable.bomb12);
        resetPosition();
    }

    public Bitmap getBomb(int bombFrame){
        return bomb[bombFrame];
    }

    public int getBombWidth(){
        return bomb[0].getWidth();
    }

    public int getBombHeight(){
        return bomb[0].getHeight();
    }

    public void resetPosition(){
        bombX = random.nextInt(GameView.dWidth - getBombWidth());
        bombY = -300 + random.nextInt(100) * -1;
        bombVelocity = 30 + random.nextInt(16);
    }
}
