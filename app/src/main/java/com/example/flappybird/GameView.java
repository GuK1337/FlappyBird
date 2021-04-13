package com.example.flappybird;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowMetrics;

import androidx.core.content.res.ResourcesCompat;

import java.io.File;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class GameView extends View {
    private int viewWidth;
    private int viewHeight;
    Handler handler;
    Runnable runnable;
    final int update_ms = 25;
    Display display;
    Point point;
    int dWidth,dHeight,birdFrame;
    Rect rect,rect2,rect3,rect4;
    Bitmap bg;
    Bitmap [] bird;
    Bitmap topTube, bottomTube;
    int velocity = 0,gravity = 3;
    int birdX,birdY;
    boolean gameState = false;
    int gap = 400;
    int minTubeOffset, maxTubeOffset;
    int numberOfTubes = 4;
    int distanceBetweenTubes;
    int[] tubeX = new int[numberOfTubes];
    int[] topTubeY = new int[numberOfTubes];
    Random random;
    int tubeVelocity = 10;
    int points = 0;
    Context contextApp;
    int heightBtns;
    Paint p;
    public GameView(Context context){
        super(context);
        contextApp = context;
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
        heightBtns = getHeightButtons();
        point = new Point();
        display.getSize(point);
        dWidth = point.x;
        dHeight = point.y + heightBtns;
        rect = new Rect(0,0,dWidth,dHeight);
        bird = new Bitmap[2];
        bg = BitmapFactory.decodeResource(getResources(),R.drawable.bg);
        if(dHeight<dWidth){
            bg = BitmapFactory.decodeResource(getResources(),R.drawable.bg2);
        }
        bird[0] = BitmapFactory.decodeResource(getResources(),R.drawable.bird);
        bird[1] = BitmapFactory.decodeResource(getResources(),R.drawable.bird2);
        topTube = BitmapFactory.decodeResource(getResources(),R.drawable.toptube);
        bottomTube = BitmapFactory.decodeResource(getResources(),R.drawable.bottomtube);
        birdX = dWidth/2 - bird[0].getWidth()/2;
        birdY = dHeight/2 - bird[0].getHeight()/2;
        distanceBetweenTubes = ((dWidth*3/4)/10) * 10;
        minTubeOffset = gap/2;
        maxTubeOffset = dHeight - minTubeOffset - gap;
        random = new Random();
        for (int i = 0;i<numberOfTubes;i++){
            tubeX[i] = dWidth + i*distanceBetweenTubes;
            topTubeY[i] = minTubeOffset + random.nextInt(maxTubeOffset - minTubeOffset + 1);
        }
        p = new Paint();
        Typeface pixelFont = ResourcesCompat.getFont(contextApp, R.font.pixel_font);
        p.setTypeface(pixelFont);
        p.setAntiAlias(true);
        p.setTextSize(65.0f);
        p.setColor(Color.BLACK);


    }
    @Override
    protected void onDraw(Canvas canvas){

        super.onDraw(canvas);
        canvas.drawBitmap(bg,null,rect,null);

        rect2 = new Rect(birdX,birdY,birdX + bird[0].getWidth()+50,birdY + bird[0].getHeight()+30);
        if(birdFrame == 0 && gameState){
            birdFrame = 1;
        }
        else {
            birdFrame = 0;
        }
        if(gameState) {
            if (birdY < dHeight - bird[0].getHeight() - 40 || velocity < 0) {
                velocity += gravity;
                birdY += velocity;
            }
            for (int i = 0;i<numberOfTubes;i++){
                tubeX[i]-=tubeVelocity;
                if(tubeX[i]<-200){
                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    topTubeY[i] = minTubeOffset + random.nextInt(maxTubeOffset - minTubeOffset + 1);
                }
                rect3 = new Rect(tubeX[i],topTubeY[i] - topTube.getHeight()-dHeight/3,tubeX[i] + 200,topTubeY[i]);
                rect4 = new Rect(tubeX[i],topTubeY[i] + gap,tubeX[i] + 200,topTubeY[i] +gap + bottomTube.getHeight()+dHeight/3);
                canvas.drawBitmap(topTube,null,rect3,null);
                canvas.drawBitmap(bottomTube,null,rect4,null);
                if (birdX + bird[0].getWidth()+50 > tubeX[i] && birdX < tubeX[i] + 200 && (birdY < topTubeY[i] || birdY + bird[0].getHeight()+30 >topTubeY[i] + gap)){
                    gameState = false;
                    Intent intent = new Intent(contextApp, MainActivity2.class);
                    intent.putExtra("Points", points);
                    contextApp.startActivity(intent);
                    Game activity = (Game) contextApp;
                    activity.finish();
                }
                else {
                    if ((birdX + (bird[0].getWidth()+50)/2)/10 == (tubeX[i] + 100)/10){
                        points++;
                    }
                }
            }
        }
        canvas.drawBitmap(bird[birdFrame],null,rect2,null);
        canvas.drawText(getResources().getString(R.string.points) + " " + points, 50, 100, p);
        handler.postDelayed(runnable,update_ms);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN){
            if (birdY>0) {
                velocity = -30;
            }
            gameState = true;
        }
        return true;
    }
    private int getHeightButtons(){
        Resources resources = contextApp.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
