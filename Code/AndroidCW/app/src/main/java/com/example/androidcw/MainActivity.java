package com.example.androidcw;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnTouchListener {


    private MediaPlayer bgm, sfx, gameOver, gameWon, fivePoints;
    private Sensor sensor;
    private SensorManager sm;
    //private Link player;
    private int tileWidth;
    //private RelativeLayout relativeLayout;
    //private Dungeon dungeon1 =  new Dungeon(this,);
    private GameView v;
    private boolean played; //Has the gameover bgm already played?



    @SuppressLint({"SourceLockedOrientationActivity", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);

        sm = (SensorManager)getSystemService(SENSOR_SERVICE); //Initialise accelerometer
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);

        tileWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        bgm = MediaPlayer.create(this, R.raw.dungeon);
        sfx = MediaPlayer.create(this, R.raw.chest);
        bgm.start();
        sfx.start();
        bgm.setLooping(true);

        played = false;

        v = new GameView(this, this); //Makes the Game View
        v.setOnTouchListener(this);
        setContentView(v); //Sets view to game view
    }

    /**
     * Plays an easter egg sound effect
     */
    public void playFivePoints(){
        if(!played){
            bgm.stop();
            fivePoints = MediaPlayer.create(this, R.raw.fivepoints);
            fivePoints.start();
        }
        played = true;
    }

    /**
     * Plays game over music
     */
    public void playGameOverBGM(){
        if(!played){
            bgm.stop();
            gameOver = MediaPlayer.create(this, R.raw.gameover);
            gameOver.start();
        }
        played = true;
    }

    /**
     * Plays game won music
     */
    public void playGameWonBGM(){
        if(!played){
            bgm.stop();
            gameWon = MediaPlayer.create(this, R.raw.triforceget);
            gameWon.start();
        }
        played = true;
    }

    @Override
    protected void onPause() { //Pauses bgm if playing
        super.onPause();

        if (bgm != null) {
            bgm.pause();
        }

        if(v.isGameOver() && gameOver != null){
            gameOver.pause();
        }

        if(v.isGameWon() && gameWon != null){
            gameWon.pause();
        }

        sm.unregisterListener(this);

        v.pause();
    }

    @Override
    protected void onResume() { //Resumes bgm
        super.onResume();
        if (bgm != null) {
            bgm.start();
        }

        if(v.isGameOver() && gameOver != null){
            gameOver.start();
        }

        if(v.isGameWon() && gameWon != null){
            gameWon.start();
        }

        sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        v.resume();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        v.getCoordinates(event.values[0], event.values[1]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getY() > tileWidth*12 && event.getY() < Resources.getSystem().getDisplayMetrics().heightPixels){
            sfx.start();
        }
        //sfx.start();
        return true;
    }
}
