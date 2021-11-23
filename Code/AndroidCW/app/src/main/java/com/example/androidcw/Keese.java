package com.example.androidcw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

/**
 * A flying bat-like enemy that moves in rapid random directions and can fly above blocks
 */
public class Keese extends Enemy {

    private int counter;


    /**
     * Bat enemy that flies over blocks
     * @param x
     * @param y
     */
    public Keese(int x, int y) {
        super(x, y);
        eX = x;
        eY = y;
        speed = 3;
        counter = 0;
        setDirection();
        damage = 1;
        enemyType = 1; //Id corresponds to Keese



    }

    @Override
    protected void calcMove() {
        counter++;
        if (counter > 3) {
            setDirection();
            counter = 0;
        }
        if(direction == 0){
            eY += speed;
        }
        else if(direction == 1 ){
            eY -= speed;
        }
        else if(direction == 2){
            eX -= speed;
        }
        else if(direction == 3){
            eX += speed;
        }
    }

    //Ignore for now
    @Override
    public int getOldX() {
        return 0;
    }

    @Override
    public int getOldY() {
        return 0;
    }


    //Ignore for now
    @Override
    public boolean checkBlockHit(ArrayList<Block> blockList1, ArrayList<Block> blockList2, ArrayList<Block> blockList3, int screenWidth, int tileWidth, int arenaDown, String map, GameView v) {
        return false;
    }
}
