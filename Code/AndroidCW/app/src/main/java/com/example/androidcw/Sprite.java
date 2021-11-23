package com.example.androidcw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Sprite {
    private int x, y;
    private int height, width;
    private Bitmap b, ib;
    private Link l;
    private int currentFrame = 0;
    private int direction = 0;
    private int rows, columns;

    /**
     * A new sprite that displays the player. Takes a second sprite sheet to display invincibility
     * @param linkSpriteSheet
     * @param invincibleSpriteSheet
     * @param player
     * @param sRows
     * @param sColumns
     */
    public Sprite(Bitmap linkSpriteSheet,Bitmap invincibleSpriteSheet, Link player, int sRows, int sColumns) {
        b = linkSpriteSheet;
        ib = invincibleSpriteSheet;
        x = player.getX();
        y = player.getY();
        l = player;
        rows = sRows;
        columns = sColumns;
        height = b.getHeight() /rows; //SpriteSheet divide height and width by how many rows and columns there are.
        width = b.getWidth() /columns;
    }

    /**
     * This will draw a cut out piece of the sprite sheet
     * @param c
     */
    public void onDraw(Canvas c) {
        updateMove(); //Gets the players current x and y
        direction = l.getDirection(); //Gets the direction currently held by the player (used to determine the the spritesheet row)
        int srcY = direction * height;
        int srcX = currentFrame * width;
        Rect src = new Rect(srcX,srcY, srcX + width, srcY + height); //Cuts out part of rectangle equal to the height and width.
        Rect dst = new Rect(x, y, x+width, y+height);//This is where it is drawn

        if(l.isInvincible()){
            c.drawBitmap(ib, src, dst, null); //Use the invincible sheet if true
        }
        else{
            c.drawBitmap(b, src, dst, null); //Else, use the normal sheet
        }

    }

    /**
     * Update current frame
     */
    public void updateFrame() {
        currentFrame = ++currentFrame %2;
    }

    /**
     * Update where to draw frame
     */
    private void updateMove() {
        x = l.getX();
        y = l.getY();

    }
}
