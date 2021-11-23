package com.example.androidcw;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;

/**
 * A skeleton that changes direction upon colliding with a block (and will also change direction after a set period of time without colliding).
 */
public class Stalfos extends Enemy {

    private int moveCounter;
    private int oldX, oldY;

    /**
     * Skeleton Enemy that moves randomly, can't move past blocks
     * @param x
     * @param y
     */
    public Stalfos(int x, int y) {
        super(x, y);
        oldX = oldY = 0;
        eX = x;
        eY = y;
        speed = 1;
        moveCounter = 0;
        setDirection();
        damage = 2;
        enemyType = 2; //Id corresponds to Stalfos

    }


    @Override
    public void checkCollision(int screenWidth, int tileWidth, int arenaDown){
        if (getX() > screenWidth-tileWidth){
            setX(screenWidth-tileWidth);
            setDirection();
        }
        if (getX() < 0){
            setX(0);
            setDirection();
        }
        if (getY() > (tileWidth*arenaDown)){
            setY((tileWidth*arenaDown));
            setDirection();
        }
        if (getY()<tileWidth){
            setY(tileWidth);
            setDirection();
        }

    }

    @Override
    public boolean checkBlockHit(ArrayList<Block> blockList1, ArrayList<Block> blockList2, ArrayList<Block> blockList3, int screenWidth, int tileWidth, int arenaDown, String map, GameView v){
        //If statements check what map is currently loaded

        boolean checker = false;
        if(map.equals("lvl1.txt")){
            for(Block b: blockList1){
                int i = b.checkCollision(this, tileWidth);
                if(i == 1){
                    checker = true;
                    setDirection();
                }
            }
        }
        if(map.equals("lvl2.txt")){
            for(Block b: blockList2){
                int i = b.checkCollision(this, tileWidth);
                if(i == 1){
                    checker = true;
                    setDirection();
                }
            }
        }
        if(map.equals("lvl3.txt")){
            for(Block b: blockList3){
                int i = b.checkCollision(this, tileWidth);
                if(i == 1){
                    checker = true;
                    setDirection();
                }
            }
        }
        return checker;
    }


    @Override
    protected void calcMove() {
        moveCounter +=1;
        if(moveCounter>500){ //Will set a new random direction after 500 loops
            setDirection();
            moveCounter = 0;
        }
        oldX = eX;
        oldY = eY;

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

    @Override
    public int getOldX(){
        return oldX;
    }

    @Override
    public int getOldY(){
        return oldY;
    }
}
