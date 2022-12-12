package com.fmi.example.piefall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.os.Handler;
import android.view.ViewConfiguration;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends View {

    Bitmap background, ground;
    Bitmap player;
    Bitmap shield;
    Bitmap hearth;
    Rect rectBackground, rectGround;
    Context context;
    Handler handler;
    final int REFRESH_RATE = 30;
    Runnable runnable;
    Paint textPaint = new Paint();
    Paint healthPaint = new Paint();

    int piesEaten = 0;
    int piesForLife = 0;
    float TEXT_SIZE = 120;

    float playerX, playerY;

    int points = 0;
    int life = 3;

    static int dWidth, dHeight;

    Random random;

    int shieldX, shieldY, shieldVelocity;

    float oldX;
    float oldPlayerX;

    ArrayList<Bomb> bombs = new ArrayList<>();
    ArrayList<Explosion> explosions = new ArrayList<>();
    ArrayList<Pie> pies = new ArrayList<>();

    public GameView(Context context) {
        super(context);
        this.context = context;
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        ground = BitmapFactory.decodeResource(getResources(), R.drawable.ground);
        player = BitmapFactory.decodeResource(context.getResources(), R.drawable.player);
        shield = BitmapFactory.decodeResource(context.getResources(), R.drawable.shield);
        hearth = BitmapFactory.decodeResource(context.getResources(), R.drawable.hearth);

        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        dWidth = size.x;
        dHeight = size.y;
        rectBackground = new Rect(0, 0, dWidth, dHeight);
        rectGround = new Rect(0, dHeight - ground.getHeight(), dWidth, dHeight);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        textPaint.setColor(Color.rgb(250,128,5));
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        healthPaint.setColor(Color.rgb(102,205,2));
        healthPaint.setTextSize(TEXT_SIZE);
        healthPaint.setTextAlign(Paint.Align.RIGHT);

        random = new Random();
        playerX = dWidth / 2 - player.getWidth() / 2;
        playerY = dHeight - ground.getHeight() - player.getHeight();
        shieldX = 0;
        shieldY = (int)playerY - shield.getHeight();
        shieldVelocity = 20;

        for (int i = 0; i < 4; i++) {
            Bomb bomb = new Bomb(context);
            bombs.add(bomb);
        }

        for (int i = 0; i < 3; i++) {
            Pie pie = new Pie(context);
            pies.add(pie);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(background, null, rectBackground, null);
        canvas.drawBitmap(ground, null, rectGround, null);
        canvas.drawBitmap(player, playerX, playerY, null);
        canvas.drawBitmap(hearth,dWidth - hearth.getWidth() - 5, 30,null);

        for (int i = 0; i < bombs.size(); i++) {
            canvas.drawBitmap(bombs.get(i).getBomb(bombs.get(i).bombFrame), bombs.get(i).bombX, bombs.get(i).bombY, null);
            bombs.get(i).bombFrame++;
            if (bombs.get(i).bombFrame > 11) {
                bombs.get(i).bombFrame = 0;
            }
            bombs.get(i).bombY += bombs.get(i).bombVelocity;
            if (bombs.get(i).bombY + bombs.get(i).getBombHeight() >= dHeight - ground.getHeight()) {
                points += 10;
                Explosion explosion = new Explosion(context);
                explosion.explosionX = bombs.get(i).bombX;
                explosion.explosionY = bombs.get(i).bombY;
                explosions.add(explosion);
                bombs.get(i).resetPosition();
            }
        }

        for(int i = 0; i < bombs.size(); i++) {
            if (bombs.get(i).bombX + bombs.get(i).getBombWidth() > playerX
                    && bombs.get(i).bombX < playerX + player.getWidth()
                    && bombs.get(i).bombY + bombs.get(i).getBombWidth() > playerY
                    && bombs.get(i).bombY + bombs.get(i).getBombWidth() < playerY + player.getHeight()){
                life--;
                bombs.get(i).resetPosition();
                if (life == 0) {
                    Intent intent = new Intent(context, GameOver.class);
                    intent.putExtra("points", points);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
        }

        for (int i = 0; i < pies.size(); i++) {
            canvas.drawBitmap(pies.get(i).image, pies.get(i).pieX, pies.get(i).pieY, null);

            pies.get(i).pieY += pies.get(i).pieVelocity;

            if(random.nextInt(2) == 0){
                pies.get(i).pieX += 1;
            }else {
                pies.get(i).pieX -= 1;
            }

            if (pies.get(i).pieY + pies.get(i).getPieHeight() >= dHeight - ground.getHeight()) {
                if(points > 0)
                    points -= 10;

                pies.get(i).resetPosition();
            }
        }

        for(int i = 0; i < pies.size(); i++) {
            if (pies.get(i).pieX + pies.get(i).getPieWidth() >= playerX
                    && pies.get(i).pieX <= playerX + player.getWidth()
                    && pies.get(i).pieY + pies.get(i).getPieWidth() >= playerY
                    && pies.get(i).pieY + pies.get(i).getPieWidth() <= playerY + player.getHeight()){

                points += 20;

                if(life < 3)piesForLife++;

                piesEaten++;
                pies.get(i).resetPosition();
            }
        }

        if(piesForLife > 19 && life < 3) {
            piesForLife = 0;
            life++;
        }
        if(piesEaten > 29) {
            canvas.drawBitmap(shield, shieldX, shieldY, null);
            shieldY -= shieldVelocity;

            for (int i = 0; i < bombs.size(); i++) {
                if (bombs.get(i).bombY + bombs.get(i).getBombHeight() >= shieldY) {
                    points += 10;
                    Explosion explosion = new Explosion(context);
                    explosion.explosionX = bombs.get(i).bombX;
                    explosion.explosionY = bombs.get(i).bombY;
                    explosions.add(explosion);
                    bombs.get(i).resetPosition();
                }
            }

            if(shieldY + shield.getHeight() < 0){
                piesEaten = 0;
                shieldY = (int)playerY - shield.getHeight();
            }
        }

        for(int i=0; i<explosions.size(); i++){
            canvas.drawBitmap(explosions.get(i).getExplosion(explosions.get(i).explosionFrame), explosions.get(i).explosionX,
                    explosions.get(i).explosionY, null);

            explosions.get(i).explosionFrame++;
            if(explosions.get(i).explosionFrame > 3){
                explosions.remove(i);
            }
        }
        canvas.drawText("" + points, 20, TEXT_SIZE, textPaint);

        switch(life){
            case 3:
                healthPaint.setColor(Color.rgb(102,205,2));
                break;
            case 2:
                healthPaint.setColor(Color.YELLOW);
                break;
            default: healthPaint.setColor(Color.RED);
                break;
        }

        canvas.drawText("" + life, dWidth - TEXT_SIZE, TEXT_SIZE, healthPaint);

        handler.postDelayed(runnable, REFRESH_RATE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        if(touchY >= playerY){
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                {
                    oldX = event.getX();
                    oldPlayerX = playerX;
                    break;
                }
                case MotionEvent.ACTION_MOVE:
                {
                    float shift = oldX - touchX;
                    float newPlayerX = oldPlayerX - shift;
                    if(newPlayerX <= 0)
                        playerX = 0;
                    else if(newPlayerX >= dWidth - player.getWidth())
                        playerX = dWidth - player.getWidth();
                    else
                        playerX = newPlayerX;
                    break;
                }
            }
        }
        return true;
    }

}







