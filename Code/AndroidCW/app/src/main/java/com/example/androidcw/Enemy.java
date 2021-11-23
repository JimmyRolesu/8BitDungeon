package com.example.androidcw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Random;

/**
 * An abstract class for computer controlled enemy units
 */
public abstract class Enemy {

    protected int eX, eY, speed, damage; //Enemy x and y
    protected int direction; //0=down 1=up 2=left 3=right
    private static int enemyCount;
    protected int enemyID;
    protected int enemyType; //1 = keese //2 = stalfos

    /**
     * An AI that moves itself and can damage player
     * @param x
     * @param y
     */
    public Enemy(int x, int y){
        eX = x;
        eY = y;
        enemyID = enemyCount++;
        speed = 0;
        damage = 0;
        enemyType = 0;

    }

    public int getDirection() {
        return direction;
    }

    public int getEnemyType() {
        return enemyType;
    }

    public int getX(){
        return eX;
    }

    public int getY(){
        return eY;
    }

    /**
     * Perform movement calculations
     */
    protected abstract void calcMove();

    public void setX(int x){
        eX = x;
    }

    public void setY(int y){
        eY = y;
    }

    public int getDamage() {return damage;}

    /**
     * Checks of the enemy is colliding with the edges of the dungeon
     * @param screenWidth
     * @param tileWidth
     * @param arenaDown
     */
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

    /**
     * Sets a random direction for the enemy
     */
    protected void setDirection(){
        int max = 3;
        int min = 0;

        Random r = new Random();

        direction = r.nextInt((max-min)+1)+min;
    }

    /**
     * Sets direction to the given input (0 = down, 1 = up, 2 = left, 3 = right)
     * @param i (0 = down, 1 = up, 2 = left, 3 = right)
     */
    protected void setDirection(int i){
        if(i>3 || i<0){
            direction = direction; //If the direction is out of range, keep direction the same
        }
        else{
            direction = i;
        }
    }

    public abstract int getOldX();

    public abstract int getOldY();

    /**
     * Checks whether an enemy is colliding with a block or not and performs the relevant action
     * @param blockList1
     * @param blockList2
     * @param blockList3
     * @param screenWidth
     * @param tileWidth
     * @param arenaDown
     * @param map
     * @param v
     * @return
     */
    public abstract boolean checkBlockHit(ArrayList<Block> blockList1, ArrayList<Block> blockList2, ArrayList<Block> blockList3, int screenWidth, int tileWidth, int arenaDown, String map, GameView v);
}
