package com.example.androidcw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class EnemySprite {
    private int x, y;
    private int height, width;
    private Bitmap b;

    private int currentFrame = 0;
    private int rows, columns;

    /**
     * Draws sprites for enemies
     * @param spriteSheet
     * @param sRows
     * @param sColumns
     */
    public EnemySprite(Bitmap spriteSheet, int sRows, int sColumns) {
        b = spriteSheet;
        rows = sRows;
        columns = sColumns;
        height = b.getHeight() /rows; //SpriteSheet divide height and width by how many rows and columns there are.
        width = b.getWidth() /columns;
    }

    /**
     * This will draw a cut out piece of the sprite sheet
     * @param c
     */
    public void onDraw(Canvas c, Enemy e) {
        x = e.getX();
        y = e.getY();

        int srcY = 0 * height;
        int srcX = currentFrame * width;
        Rect src = new Rect(srcX,srcY, srcX + width, srcY + height); //Cuts out part of rectangle equal to the height and width.
        Rect dst = new Rect(x, y, x+width, y+height);//This is where it is drawn
        c.drawBitmap(b, src, dst, null);
    }

    /**
     * The class will go through the sheet, cutting out and displaying the next frame
     */
    public void updateFrame() {
        currentFrame = ++currentFrame %columns;
    }

}
