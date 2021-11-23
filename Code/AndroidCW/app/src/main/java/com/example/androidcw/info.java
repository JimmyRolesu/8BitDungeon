package com.example.androidcw;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

public class info extends AppCompatActivity {

    MediaPlayer bgm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);

        bgm = MediaPlayer.create(this, R.raw.fairyfountain);
        bgm.start();
        bgm.setLooping(true);
        //bgm.setVolume(0.05f, 0.05f);
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
