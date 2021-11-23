package com.example.androidcw;

import android.content.res.Resources;

public class Block {

    protected int bX, bY, bWidth, bType;

    public Block(int x, int y, int type, int width){
        bX = x;
        bY = y;
        bType = type;
        bWidth = width;
    }

    public int getX(){
        return bX;
    }

    public int getY(){
        return bY;
    }

    public int getbType() {
        return bType;
    }

    public int getWidth() {
        return bWidth;
    }

    /**
     * Checks whether the player is colliding with a block or not and returns an int value that represents the type of block it hit (or a value of 0 if it did not hit a block).
     * @param player
     * @param tileWidth
     * @return
     */
    public int checkCollision(Link player, int tileWidth){
        if(player.getX()<bX+tileWidth && player.getX()+tileWidth > bX && player.getY()<bY+tileWidth && player.getY()+tileWidth > bY && bType < 4){ //If the player coordinates are touching a block they can't walk past.
            player.updateY(player.getOldY());
            player.updateX(player.getOldX());
            return 1;
        }
        else if(player.getX()<bX+tileWidth && player.getX()+tileWidth > bX && player.getY()<bY+tileWidth && player.getY()+tileWidth > bY && bType == 4) { //If the player is touching the stairs go to the next level.
            return 2;
        }
        else return 0;
    }

    public int checkCollision(Enemy enemy, int tileWidth){
        if(enemy.getX()<bX+tileWidth && enemy.getX()+tileWidth > bX && enemy.getY()<bY+tileWidth && enemy.getY()+tileWidth > bY && bType < 4){ //If the player coordinates are touching a block they can't walk past.
            enemy.setY(enemy.getOldY());
            enemy.setX(enemy.getOldX());
            return 1;
        }
        else return 0;
    }
}
