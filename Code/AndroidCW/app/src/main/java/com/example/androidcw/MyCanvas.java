package com.example.androidcw;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.System.exit;

/**
 * NOT LONGER IN USE - REPLACED WITH GAMEVIEW
 */
public class MyCanvas extends View {

    private Paint paint;
    private Rect rect;
    private Link player;
    private Bitmap linkDown1, linkDown2, sword, heart, halfHeart, emptyHeart, dungeonTile, blockTile, gargLeft, gargRight, stairs;
    private int screenWidth, tileWidth;
    private int arenaAcross, arenaDown;
    private int[][] levelInfo; //2D array to store level info
    private String map = "lvl2.txt";
    ArrayList<Block> blockList;
    private boolean setter;
    private int counter;

    public MyCanvas(Context context) {
        super(context);

        arenaAcross = 8;
        arenaDown = 11;
        screenWidth = getScreenWidth();
        tileWidth = getScreenWidth()/arenaAcross;
        player = new Link((screenWidth/2)-tileWidth/2, tileWidth*2, tileWidth);
        blockList = new ArrayList<Block>();
        setter = false;
        blockToArray();

        bitMapSetup();
    }


    @Override
    protected void onDraw(Canvas canvas){

        super.onDraw(canvas);
        printArea(canvas);
        printHP(canvas);
        placeTiles(canvas);
        drawBlocks(canvas);
        checkCollision();
        checkBlockCollision();
        canvas.drawBitmap(sword, ((tileWidth*arenaAcross)/2) - (sword.getWidth()/2), (tileWidth*arenaDown)+tileWidth+(tileWidth/2), new Paint());
        drawLink(canvas);


        invalidate(); //Refreshes canvas
    }

    protected void drawLink(Canvas canvas){
        canvas.drawBitmap(linkDown1, player.getX(), player.getY(), new Paint());
        }



    protected void checkCollision(){
        if (player.getX() > screenWidth-tileWidth){
            player.updateX(screenWidth-tileWidth);
        }
        if (player.getX() < 0){
            player.updateX(0);
        }
        if (player.getY() > (tileWidth*arenaDown)){
            player.updateY((tileWidth*arenaDown));
        }
        if (player.getY()<tileWidth){
            player.updateY(tileWidth);
        }

    }

    protected void checkBlockCollision(){
        for(Block b: blockList){
            if(player.getX()<b.getX()+tileWidth && player.getX()+tileWidth > b.getX() && player.getY()<b.getY()+tileWidth && player.getY()+tileWidth > b.getY() && b.getbType() < 4){
                player.updateY(player.getOldY());
                player.updateX(player.getOldX());
            }
        }
    }

    protected void blockToArray(){
        int[][] place = fileToArray(map);

        int xOffset = 0;
        int yOffset = tileWidth;
        for(int y = 0; y<arenaDown; y++){
            for(int x = 0; x<arenaAcross; x++){
                if(place[x][y] == 1){
                    blockList.add(new Block(xOffset, yOffset, 1, tileWidth));
                }
                if(place[x][y] == 2){
                    blockList.add(new Block(xOffset, yOffset, 2, tileWidth));
                }
                if(place[x][y] == 3){
                   blockList.add(new Block(xOffset, yOffset, 3, tileWidth));
                }
                if(place[x][y] == 4){
                    blockList.add(new Block(xOffset, yOffset, 4, tileWidth));
                }
                xOffset += tileWidth;
            }
            xOffset = 0;
            yOffset += tileWidth;
        }
    }


    public int getTileWidth() {
        return tileWidth;
    }

    protected int[][] fileToArray(String map){

        String text = "";
        String[] lines = new String[11];
        int count = 0;
        int[][] split2d = new int[8][11];
        String[] split = new String[8];

        try{
            InputStream is = getContext().getAssets().open(map);
            BufferedReader bfr = new BufferedReader(new InputStreamReader(is));

            if(is != null){
                while((text = bfr.readLine()) != null){
                    lines[count] = text;
                    count++;
                }
                is.close();
            }

            for(int y = 0; y<11; y++){
                split = lines[y].split(",");
                for(int x = 0; x<8; x++){
                    split2d[x][y] = Integer.parseInt(split[x]);
                }
            }

            bfr.close();

        }catch(IOException ex){
            ex.printStackTrace();
        }

        return split2d;
    }


    /**
     * This reads a map file and prints out the blocks to the canvas,
     * @param c
     */
    protected void drawBlocks(Canvas c){
        int[][] placement = fileToArray(map);

        int xOffset = 0;
        int yOffset = dungeonTile.getWidth();
        for(int y = 0; y<arenaDown; y++){
            for(int x = 0; x<arenaAcross; x++){
                if(placement[x][y] == 1){
                    c.drawBitmap(blockTile, xOffset,yOffset,new Paint());
                }
                if(placement[x][y] == 2){
                    c.drawBitmap(gargLeft, xOffset,yOffset,new Paint());
                }
                if(placement[x][y] == 3){
                    c.drawBitmap(gargRight, xOffset,yOffset,new Paint());
                }
                if(placement[x][y] == 4){
                    c.drawBitmap(stairs, xOffset,yOffset,new Paint());
                }
                xOffset += dungeonTile.getWidth();
            }
            xOffset = 0;
            yOffset += dungeonTile.getWidth();
        }
    }

    /**
     * Adds and scales all bitmaps to be drawn onto the canvas
     */
    protected void bitMapSetup(){
        linkDown1 = BitmapFactory.decodeResource(getResources(), R.drawable.downlink1);
        linkDown1 = linkDown1.createScaledBitmap(linkDown1, tileWidth, (tileWidth/60)*64, false);

        linkDown2 = BitmapFactory.decodeResource(getResources(), R.drawable.downlink2);
        linkDown2 = linkDown2.createScaledBitmap(linkDown2, tileWidth, (tileWidth/56)*64, false);

        heart = BitmapFactory.decodeResource(getResources(),R.drawable.full);
        halfHeart = BitmapFactory.decodeResource(getResources(),R.drawable.half);
        emptyHeart = BitmapFactory.decodeResource(getResources(),R.drawable.empty);

        sword = BitmapFactory.decodeResource(getResources(), R.drawable.whitesword);
        sword = sword.createScaledBitmap(sword, sword.getWidth(), sword.getHeight(), false);

        dungeonTile = BitmapFactory.decodeResource(getResources(), R.drawable.dungeontile);
        dungeonTile = dungeonTile.createScaledBitmap(dungeonTile, tileWidth, tileWidth, false);

        blockTile = BitmapFactory.decodeResource(getResources(), R.drawable.dungeonblock); //id 1
        blockTile = blockTile.createScaledBitmap(blockTile, tileWidth, tileWidth, false);

        gargLeft = BitmapFactory.decodeResource(getResources(), R.drawable.gargleft); //id 2
        gargLeft = blockTile.createScaledBitmap(gargLeft, tileWidth, tileWidth, false);

        gargRight = BitmapFactory.decodeResource(getResources(), R.drawable.gargright); //id 3
        gargRight = blockTile.createScaledBitmap(gargRight, tileWidth, tileWidth, false);

        stairs = BitmapFactory.decodeResource(getResources(), R.drawable.stairs); //id 4
        stairs = stairs.createScaledBitmap(stairs, tileWidth, tileWidth, false);
    }

    protected static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /**
     * Draws a set of tiles on the canvas representing the floor in the game.
     * @param c
     */
    protected void placeTiles(Canvas c){

        int xOffset = 0;
        int yOffset = dungeonTile.getWidth();
        for(int y = 0; y<arenaDown; y++){
            for(int x = 0; x<arenaAcross; x++){
                c.drawBitmap(dungeonTile, xOffset,yOffset,new Paint());
                xOffset += dungeonTile.getWidth();
            }
            xOffset = 0;
            yOffset += dungeonTile.getWidth();
        }
    }

    /**
     * Prints a background the size of the arena in a solid colour
     * @param c
     */
    protected void printArea(Canvas c){
        Rect bg = new Rect();
        bg.set(0, (tileWidth), (tileWidth*arenaAcross),(tileWidth*arenaDown));
        Paint peachFill = new Paint();
        peachFill.setColor(Color.parseColor("#008088"));
        peachFill.setStyle(Paint.Style.FILL);

        c.drawRect(bg, peachFill);
    }

    /**
     * Prints the players remaining HP out to the top left of the screen. One heart represents 2hp, a half heart represents 1hp and a blank heart represents 0hp. The number of hearts displayed will be equal to the max hp divided by two.
     * @param c
     */
    protected void printHP(Canvas c){
        int hp = player.getHp();
        int noFull = hp/2; //Number of full hearts
        int noHalf = hp%2; //Number of half hearts
        int noEmpty = player.getMaxHp()/2 - (noFull+noHalf);

        int offset = 20; //Places hearts away from one another
        //System.out.println(noFull+" "+noHalf+" "+noEmpty);

        for(int i=0; i<noFull; i++){
            c.drawBitmap(heart, offset, 20, new Paint());
            offset = offset + heart.getWidth() + 10;
        }

        for(int i=0; i<noHalf; i++){
            c.drawBitmap(halfHeart, offset, 20, new Paint());
            offset = offset + heart.getWidth() + 10;
        }

        for(int i=0; i<noEmpty; i++){
            c.drawBitmap(emptyHeart, offset, 20, new Paint());
            offset = offset + heart.getWidth() + 10;
        }
    }
}
