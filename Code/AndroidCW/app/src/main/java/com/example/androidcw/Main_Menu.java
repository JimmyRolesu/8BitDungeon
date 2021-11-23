package com.example.androidcw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.content.Intent;
import android.view.View.OnClickListener;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

public class Main_Menu extends AppCompatActivity {

    ImageView bg;
    AnimationDrawable anime;
    MediaPlayer bgm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__menu);
        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);

        bg = (ImageView) findViewById(R.id.bg);
        if (bg == null) throw new AssertionError();
        bg.setBackgroundResource(R.drawable.waterfall_animation);

        anime = (AnimationDrawable) bg.getBackground();
        anime.start();

        bgm = MediaPlayer.create(this, R.raw.intro);
        bgm.start();
        bgm.setLooping(true);
        //bgm.setVolume(0.05f, 0.05f);

        ImageView startBtn = (ImageView) findViewById(R.id.startGame);
        startBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mainIntent = new Intent(Main_Menu.this, MainActivity.class);
                startActivity(mainIntent);

            }
        });

        ImageView h2pBtn = (ImageView) findViewById(R.id.h2pGame);
        h2pBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent infoIntent = new Intent(Main_Menu.this, info.class);
                startActivity(infoIntent);

            }
        });

    }

    @Override
    public void onBackPressed(){ //User can't use android back button

    }

    @Override
    protected void onPause() { //Pauses bgm if playing
        super.onPause();

        if (bgm != null) {
            bgm.pause();
        }
    }

    @Override
    protected void onResume() { //Resumes bgm
        super.onResume();
        if (bgm != null) {
            bgm.start();
        }
    }
}
