package com.example.androidcw;

import android.view.View;

/**
 * An object that can be picked up by the player (and will do an action upon being pick up).
 */
public abstract class Collectible {

    protected int x, y;
    protected boolean collected;
    protected int type;

    /**
     * A collectible object
     * @param cX
     * @param cY
     */
    public Collectible(int cX, int cY){
        int x = cX;
        int y = cY;

    }

    /**
     * True if has been touched
     * @return
     */
    public boolean isCollected(){
        return collected;
    }

    public int getX(){
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    /**
     * Action when touched by player
     * @param view
     * @param l
     */
    public abstract void onPlayerTouch(GameView view, Link l);
}
