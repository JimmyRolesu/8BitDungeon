package com.example.androidcw;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class GameView extends SurfaceView implements Runnable {

    Thread main = null;
    SurfaceHolder surfaceHolder;
    boolean running = false;
    private Link player;
    private float tempCoordinateX, tempCoordinateY; //Stores accelerometer data so the player coordinates only update with each game loop
    private Bitmap linkSpriteSheet, invinciLinkSpriteSheet, sword, heart, halfHeart, emptyHeart, dungeonTile, blockTile, gargLeft, gargRight, stairs, keeseSpriteSheet, stalfosSpriteSheet, triforceSpriteSheet, rupeeIcon, oneRupee, triforceGet;
    private int screenWidth, tileWidth;
    private int arenaAcross, arenaDown;
    private int[][] levelInfo; //2D array to store level info
    private String map;

    //Sets up a number of ArrayLists to store block, enemy and collectible data
    ArrayList<Block> blockList1, blockList2, blockList3;
    ArrayList<Enemy> enemyList1, enemyList2, enemyList3;
    ArrayList<Collectible> collectibleList1, collectibleList2, collectibleList3;

    //Sets up sprites
    Sprite linkSprite;
    EnemySprite keeseSprite, stalfosSprite;
    CollectibleSprite triforceSprite, oneRupeeSprite;

    private MediaPlayer sfx, rupeeGet;
    private boolean gameWon, gameOver; //Determin if the game is won or lost
    int linkAnimationCounter, enemyAnimationCounter, collectibleAnimationCounter, invincibleCounter; //Counter to change animation frames
    private MainActivity mA;
    private Context contextGV; //Context taken from the main activity

    private int heartOffset;


    /**
     * View to which the game is drawn
     * @param context
     * @param mainActivity
     */
    public GameView(Context context, MainActivity mainActivity) {
        super(context);
        contextGV = context; //Saves the context
        mA = mainActivity; //Saves the main activity so it can be used elsewhere

        gameWon = false;
        gameOver = false;

        arenaAcross = 8;//Sets areana sizes
        arenaDown = 11;

        map = "lvl1.txt"; //Set initial map

        screenWidth = getScreenWidth(); //Gets the width of the screen
        tileWidth = getScreenWidth()/arenaAcross; //Scales the size of the tile so they can fit on a phone screen, regardless of the resolution

        player = new Link((screenWidth/2)-tileWidth/2, tileWidth*2, tileWidth); //Sets initial start position for the player

        //Initialise all ArrayLists
        blockList1 = new ArrayList<Block>();
        blockList2 = new ArrayList<Block>();
        blockList3 = new ArrayList<Block>();

        enemyList1 = new ArrayList<>();
        enemyList2 = new ArrayList<>();
        enemyList3 = new ArrayList<>();

        collectibleList1 = new ArrayList<>();
        collectibleList2 = new ArrayList<>();
        collectibleList3 = new ArrayList<>();

        sfx = MediaPlayer.create(context, R.raw.chest); //Plays a sound effect when the activity starts

        //Loads maps into ArrayLists
        blockToArray();
        enemyToArray();
        collectableToArray();
        bitMapSetup();

        surfaceHolder = getHolder();

        //Set all counters initially to 0
        linkAnimationCounter = 0;
        enemyAnimationCounter = 0;
        collectibleAnimationCounter = 0;
        invincibleCounter = 0;

        //Initialise sprites for animation
        linkSprite=new Sprite(linkSpriteSheet, invinciLinkSpriteSheet, player, 4, 2);
        keeseSprite = new EnemySprite(keeseSpriteSheet,1, 2);
        stalfosSprite = new EnemySprite(stalfosSpriteSheet,1,2);
        triforceSprite = new CollectibleSprite(triforceSpriteSheet,1,2);
        oneRupeeSprite = new CollectibleSprite(oneRupee, 1, 2);

    }

    @Override
    public void run() {

        while(running){ //Draws to the canvas while the thread is running
            if(!surfaceHolder.getSurface().isValid()){ //If the surface is not valid
                continue; //Skip drawing to the surface view
            }
            if(!player.checkAlive()){ //If the player has lost all their lives then the game will stop drawing the dungeon and will instead draw the game over screen
                gameOver = true;
                mA.playGameOverBGM();
                Canvas c = surfaceHolder.lockCanvas();
                drawGameOver(c);
                surfaceHolder.unlockCanvasAndPost(c);

            }
            else if(gameWon){
                if(player.getNoRupees() == 5){
                    mA.playFivePoints(); //An easter egg plays if the player gets a score of 5...
                }
                else{
                    mA.playGameWonBGM(); //The Game Won Music plays after the triforce is collected
                }
                Canvas c = surfaceHolder.lockCanvas();
                drawGameWon(c); //Draws game won screen
                surfaceHolder.unlockCanvasAndPost(c);

            }
            else{
                setLinkCoordinates(tempCoordinateX, tempCoordinateY);
                updateEnemy();
                checkAllCollisions();
                Canvas c = surfaceHolder.lockCanvas(); //Start editing the surface view using the canvas
                drawGame(c);
                surfaceHolder.unlockCanvasAndPost(c);
                updateCounters();
            }



        }

    }

    /**
     * Pauses the game
     */
    public void pause(){
        running = false;
        while(true){
            try {
                main.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            break;
        }
        main = null; //Gets rid of thread.
    }

    /**
     * Resumes surface view in new thread
     */
    public void resume(){
        running = true;
        main = new Thread(this);//this -> looks for run method.
        main.start(); //Start thread
    }

    /**
     * Updates the various game counters, including those for animation and for player invincibility
     */
    protected void updateCounters(){

        if(!(player.getxSpeed() == 0 && player.getySpeed() == 0)){ //Only update sprite frame when moving the player
            linkAnimationCounter +=1;
            if(linkAnimationCounter>10){
                linkSprite.updateFrame(); //Updates Link's frames every 10 loops.
                linkAnimationCounter = 0;
            }
        }

        enemyAnimationCounter +=1;
        if(enemyAnimationCounter>10){
            keeseSprite.updateFrame();
            stalfosSprite.updateFrame();
            enemyAnimationCounter = 0;
        }

        collectibleAnimationCounter +=1;
        if(collectibleAnimationCounter>5){
            triforceSprite.updateFrame();
            oneRupeeSprite.updateFrame();
            collectibleAnimationCounter = 0;
        }

        if(player.isInvincible()){ //Only start counting once the player becomes invincible
            invincibleCounter+=1;
            if(invincibleCounter>100){ //After a set amount of ticks, reset the counter and make the player vincible again
                player.setInvincible();
                invincibleCounter = 0;
            }
        }

    }

    /**
     * Draws the game won screen after the player collects the triforce
     * @param c
     */
    protected void drawGameWon(Canvas c){
        String msg = "Game Complete, Thanks for Playing!"; //Message to be written
        String msg2 = "The amount of rupees you collected was: ";
        String msg3 = "" + player.getNoRupees(); //Prints player rupee total
        c.drawColor(Color.BLACK);
        Typeface typeface = Typeface.createFromAsset(contextGV.getAssets(), "eightbit.ttf"); //Adds a custom font to draw my message with
        Paint textPaint = new Paint();
        textPaint.setTypeface(typeface);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(getScreenWidth()/35); //Text scaled to fit screen
        c.drawText(msg, screenWidth/2 - textPaint.measureText(msg)/2, getScreenHeight()/8, textPaint); //Text is centered
        textPaint.setTextSize(screenWidth/45);
        c.drawText(msg2, screenWidth/2 - textPaint.measureText(msg2)/2, (getScreenHeight()*2)/8, textPaint);
        textPaint.setTextSize(getScreenWidth()/10);
        textPaint.setColor(Color.RED);
        c.drawText(msg3, screenWidth/2 -(textPaint.measureText(msg3)/2), (getScreenHeight()*3)/8, textPaint);

        c.drawBitmap(triforceGet, player.getX()-(tileWidth/2), player.getY()-(tileWidth), new Paint()); //Draws player holding Triforce
    }

    /**
     * Draws the game over screen after the player has run out of health
     */
    protected void drawGameOver(Canvas c){
        String msg = "Game Over";
        c.drawColor(Color.BLACK);
        Typeface typeface = Typeface.createFromAsset(contextGV.getAssets(), "eightbit.ttf");
        Paint textPaint = new Paint();
        textPaint.setTypeface(typeface);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(getScreenWidth()/25);
        c.drawText(msg, screenWidth/2 - textPaint.measureText(msg)/2, getScreenHeight()/2, textPaint);
    }




    protected boolean isGameOver(){
        return gameOver;
    }

    protected boolean isGameWon(){
        return gameWon;
    }

    public void setGameWon(boolean isGameWon){
        gameWon = isGameWon;
    }

    /**
     * Takes two coordinate values to set to the player (Link).
     * @param x
     * @param y
     */
    public void getCoordinates(float x, float y){
        tempCoordinateX = x;
        tempCoordinateY = y;
    }

    /**
     *Draws each part of the game screen onto the canvas, including the map, blocks, player and remaining health
     * @param c The current canvas
     */
    protected void drawGame(Canvas c){
        //All draw methods called to draw a frame
        c.drawColor(Color.BLACK);
        printArea(c);
        printHP(c);
        printRupeeCount(c);
        placeTiles(c);
        drawBlocks(c);
        drawCollectibles(c);
        c.drawBitmap(sword, ((tileWidth*arenaAcross)/2) - (sword.getWidth()/2), (tileWidth*arenaDown)+tileWidth+(tileWidth/2), new Paint());
        drawLink(c);
        drawEnemies(c);

    }

    /**
     * At each loop, the game will check to see if the player is colliding with any walls or blocks and will adjust the player's position accordingly
     */
    protected void checkAllCollisions(){
        checkCollision();
        checkBlockCollision();
        checkEnemyCollision();
        checkCollectibleCollision();


    }

    /*
    Changes the value of "map" so that a new map will be drawn. Also resets the player position.
     */
    protected void updateMap(){
        if(map.equals("lvl1.txt")){
            map = "lvl2.txt";
            player.updateX((screenWidth/2)-tileWidth/2);
            player.updateY((tileWidth*2));
            //blockToArray();
        }
        else if(map.equals("lvl2.txt")){
            map = "lvl3.txt";
            player.updateX((screenWidth/2)-tileWidth/2);
            player.updateY((tileWidth*2));
            //blockToArray();
        }
        else{
            map = "test.txt";
            player.updateX((screenWidth/2)-tileWidth/2);
            player.updateY((tileWidth*2));
            //blockToArray();
        }
    }


    /**
     * Draws the player to the canvas
     * @param canvas The current canvas
     */
    protected void drawLink(Canvas canvas){
        //canvas.drawBitmap(linkDown1, player.getX(), player.getY(), new Paint());
        linkSprite.onDraw(canvas);//Draws the player sprite


    }

    /**
     * Calculates new position of all enemies
     */
    protected void updateEnemy(){
        //If statements check what map is currently loaded

        if(map.equals("lvl1.txt")){
            for(Enemy e: enemyList1){
                if(e.enemyType == 1){
                    e.calcMove();
                    e.checkCollision(screenWidth,tileWidth,arenaDown);
                }
                else if(e.enemyType == 2){
                    e.calcMove();
                    e.checkCollision(screenWidth,tileWidth,arenaDown);
                    e.checkBlockHit(blockList1,blockList2,blockList3,screenWidth,tileWidth,arenaDown,map,this);
                }
            }
        }
        if(map.equals("lvl2.txt")){
            for(Enemy e: enemyList2){
                if(e.enemyType == 1){
                    e.calcMove();
                    e.checkCollision(screenWidth,tileWidth,arenaDown);
                }
                else if(e.enemyType == 2){
                    e.calcMove();
                    e.checkCollision(screenWidth,tileWidth,arenaDown);
                    e.checkBlockHit(blockList1,blockList2,blockList3,screenWidth,tileWidth,arenaDown,map,this);
                }

            }
        }
        if(map.equals("lvl3.txt")){
            for(Enemy e: enemyList3){
                if(e.enemyType == 1){
                    e.calcMove();
                    e.checkCollision(screenWidth,tileWidth,arenaDown);
                }
                else if(e.enemyType == 2){
                    e.calcMove();
                    e.checkCollision(screenWidth,tileWidth,arenaDown);
                    e.checkBlockHit(blockList1,blockList2,blockList3,screenWidth,tileWidth,arenaDown,map,this);
                }

            }
        }
    }

    /**
     * Draws enemies to the screen
     * @param canvas
     */
    protected void drawEnemies(Canvas canvas){
        //If statements check what map is currently loaded

        if(map.equals("lvl1.txt")){
            for(Enemy e: enemyList1){
                if(e.enemyType == 1){
                    keeseSprite.onDraw(canvas, e);
                    //e.onDraw(canvas, keese1);
                }
                else if (e.enemyType == 2){
                    stalfosSprite.onDraw(canvas, e);
                }
            }
        }
        if(map.equals("lvl2.txt")){
            for(Enemy e: enemyList2){
                if(e.enemyType == 1){
                    keeseSprite.onDraw(canvas, e);
                    //e.onDraw(canvas, keese1);
                }
                else if (e.enemyType == 2){
                    stalfosSprite.onDraw(canvas, e);
                }

            }
        }
        if(map.equals("lvl3.txt")){
            for(Enemy e: enemyList3){
                if(e.enemyType == 1){
                    keeseSprite.onDraw(canvas, e);
                    //e.onDraw(canvas, keese1);
                }
                else if (e.enemyType == 2){
                    stalfosSprite.onDraw(canvas, e);
                }

            }
        }
    }

    protected void drawCollectibles(Canvas canvas){
        //If statements check what map is currently loaded

        if(map.equals("lvl1.txt")){
            for(Collectible c: collectibleList1){
                if(c.type == 1 && !c.isCollected()){
                    triforceSprite.onDraw(canvas, c);
                }
                else if(c.type == 2 && !c.isCollected()){
                    oneRupeeSprite.onDraw(canvas, c);
                }
            }
        }
        if(map.equals("lvl2.txt")){
            for(Collectible c: collectibleList2){
                if(c.type == 1 && !c.isCollected()){
                    triforceSprite.onDraw(canvas, c);
                }
                else if(c.type == 2 && !c.isCollected()){
                    oneRupeeSprite.onDraw(canvas, c);
                }
            }
        }
        if(map.equals("lvl3.txt")){
            for(Collectible c: collectibleList3){
                if(c.type == 1 && !c.isCollected()){
                    triforceSprite.onDraw(canvas, c);
                }
                else if(c.type == 2 && !c.isCollected()){
                    oneRupeeSprite.onDraw(canvas, c);
                }
            }
        }
    }

    /**
     * Checks players collisions with collectibles
     */
    protected void checkCollectibleCollision(){
        player.checkCollectibleCollisons(collectibleList1, collectibleList2, collectibleList3, tileWidth, contextGV, map, this);
    }

    /**
     * Checks player collisions with enemy
     */
    protected void checkEnemyCollision(){
        player.checkEnemyCollisions(enemyList1, enemyList2, enemyList3, tileWidth, contextGV, map);
    }


    /**
     * Checks for any collisions between the player and the walls of the dungeon (but not the blocks).
     */
    protected void checkCollision(){
        player.checkCollision(screenWidth, tileWidth, arenaDown);
    }

    /**
     * Checks collisions between the player and the blocks on screen; using the id of the block to identify what action to take when a player hits it.
     */
    protected void checkBlockCollision(){
        player.checkBlockCollisions(map, blockList1, blockList2, blockList3, this, tileWidth);
    }

    /**
     * Adds Blocks to the blockList Array List.
     */
    protected void blockToArray(){
        //If statements check what map is currently loaded

        //Level 1
        int[][] place = fileToArray("lvl1.txt");

        int xOffset = 0;
        int yOffset = tileWidth;
        for(int y = 0; y<arenaDown; y++){
            for(int x = 0; x<arenaAcross; x++){
                if(place[x][y] == 1){
                    blockList1.add(new Block(xOffset, yOffset, 1, tileWidth));
                }
                if(place[x][y] == 2){
                    blockList1.add(new Block(xOffset, yOffset, 2, tileWidth));
                }
                if(place[x][y] == 3){
                    blockList1.add(new Block(xOffset, yOffset, 3, tileWidth));
                }
                if(place[x][y] == 4){
                    blockList1.add(new Block(xOffset, yOffset, 4, tileWidth));
                }
                xOffset += tileWidth;
            }
            xOffset = 0;
            yOffset += tileWidth;
        }

        //Level 2
        place = fileToArray("lvl2.txt");

        xOffset = 0;
        yOffset = tileWidth;
        for(int y = 0; y<arenaDown; y++){
            for(int x = 0; x<arenaAcross; x++){
                if(place[x][y] == 1){
                    blockList2.add(new Block(xOffset, yOffset, 1, tileWidth));
                }
                if(place[x][y] == 2){
                    blockList2.add(new Block(xOffset, yOffset, 2, tileWidth));
                }
                if(place[x][y] == 3){
                    blockList2.add(new Block(xOffset, yOffset, 3, tileWidth));
                }
                if(place[x][y] == 4){
                    blockList2.add(new Block(xOffset, yOffset, 4, tileWidth));
                }
                xOffset += tileWidth;
            }
            xOffset = 0;
            yOffset += tileWidth;
        }

        //Level 3
        place = fileToArray("lvl3.txt");

        xOffset = 0;
        yOffset = tileWidth;
        for(int y = 0; y<arenaDown; y++){
            for(int x = 0; x<arenaAcross; x++){
                if(place[x][y] == 1){
                    blockList3.add(new Block(xOffset, yOffset, 1, tileWidth));
                }
                if(place[x][y] == 2){
                    blockList3.add(new Block(xOffset, yOffset, 2, tileWidth));
                }
                if(place[x][y] == 3){
                    blockList3.add(new Block(xOffset, yOffset, 3, tileWidth));
                }
                if(place[x][y] == 4){
                    blockList3.add(new Block(xOffset, yOffset, 4, tileWidth));
                }
                xOffset += tileWidth;
            }
            xOffset = 0;
            yOffset += tileWidth;
        }
    }

    /**
     * Reads the map file and adds enemies to their own array. If the map has a 9 it will spawn a keese and an 8 will spawn a stalfos
     */
    protected void enemyToArray(){
        //If statements check what map is currently loaded

        //Level 1
        int[][] place = fileToArray("lvl1.txt");

        int xOffset = 0;
        int yOffset = tileWidth;
        for(int y = 0; y<arenaDown; y++){
            for(int x = 0; x<arenaAcross; x++){
                if(place[x][y] == 9){
                    enemyList1.add(new Keese(xOffset, yOffset));
                }
                if(place[x][y] == 8){
                    enemyList1.add(new Stalfos(xOffset, yOffset));
                }
                xOffset += tileWidth;
            }
            xOffset = 0;
            yOffset += tileWidth;
        }

        //Level 2
        place = fileToArray("lvl2.txt");

        xOffset = 0;
        yOffset = tileWidth;
        for(int y = 0; y<arenaDown; y++){
            for(int x = 0; x<arenaAcross; x++){
                if(place[x][y] == 9){
                    enemyList2.add(new Keese(xOffset, yOffset));
                }
                if(place[x][y] == 8){
                    enemyList2.add(new Stalfos(xOffset, yOffset));
                }
                xOffset += tileWidth;
            }
            xOffset = 0;
            yOffset += tileWidth;
        }

        //Level 3
        place = fileToArray("lvl3.txt");

        xOffset = 0;
        yOffset = tileWidth;
        for(int y = 0; y<arenaDown; y++){
            for(int x = 0; x<arenaAcross; x++){
                if(place[x][y] == 9){
                    enemyList3.add(new Keese(xOffset, yOffset));
                }
                if(place[x][y] == 8){
                    enemyList3.add(new Stalfos(xOffset, yOffset));
                }
                xOffset += tileWidth;
            }
            xOffset = 0;
            yOffset += tileWidth;
        }
    }

    /**
     * Adds all collectibles written on the map to the array
     */
    protected void collectableToArray(){
        //If statements check what map is currently loaded

        //Level 1
        int[][] place = fileToArray("lvl1.txt");

        int xOffset = 0;
        int yOffset = tileWidth;
        for(int y = 0; y<arenaDown; y++){
            for(int x = 0; x<arenaAcross; x++){
                if(place[x][y] == 5){
                    collectibleList1.add(new Triforce(xOffset, yOffset));
                }
                if(place[x][y] == 6){
                    collectibleList1.add(new Rupee(xOffset, yOffset));
                }

                xOffset += tileWidth;
            }
            xOffset = 0;
            yOffset += tileWidth;
        }

        //Level 2
        place = fileToArray("lvl2.txt");

        xOffset = 0;
        yOffset = tileWidth;
        for(int y = 0; y<arenaDown; y++){
            for(int x = 0; x<arenaAcross; x++){
                if(place[x][y] == 5){
                    collectibleList2.add(new Triforce(xOffset, yOffset));
                }
                if(place[x][y] == 6){
                    collectibleList2.add(new Rupee(xOffset, yOffset));
                }

                xOffset += tileWidth;
            }
            xOffset = 0;
            yOffset += tileWidth;
        }

        //Level 3
        place = fileToArray("lvl3.txt");

        xOffset = 0;
        yOffset = tileWidth;
        for(int y = 0; y<arenaDown; y++){
            for(int x = 0; x<arenaAcross; x++){
                if(place[x][y] == 5){
                    collectibleList3.add(new Triforce(xOffset, yOffset));
                }
                if(place[x][y] == 6){
                    collectibleList3.add(new Rupee(xOffset, yOffset));
                }

                xOffset += tileWidth;
            }
            xOffset = 0;
            yOffset += tileWidth;
        }
    }


    /**
     * Returns the tilewidth
     * @return Tile Width
     */
    public int getTileWidth() {
        return tileWidth;
    }

    /**
     * Sets the position on the player based on readings from the phone's accelerometer.
     * @param x Acceleration along the x-axis
     * @param y Acceleration along the y-axis
     */
    public void setLinkCoordinates(float x, float y){
        player.setLinkCoordinates(x,y);
    }


    /**
     * Reads the map file past to it and converts it into a 2d array.
     */
    @org.jetbrains.annotations.NotNull
    private int[][] fileToArray(String map){

        String text = "";
        String[] lines = new String[11];
        int count = 0;
        int[][] split2d = new int[8][11];
        String[] split = new String[8];

        try{
            InputStream is = getContext().getAssets().open(map); //File reader opens the current map and puts it into buffered reader
            BufferedReader bfr = new BufferedReader(new InputStreamReader(is));

            if(is != null){
                while((text = bfr.readLine()) != null){ //Puts each line of reader into a array
                    lines[count] = text;
                    count++;
                }
                is.close();
            }

            for(int y = 0; y<11; y++){ //Splits that array into a 2D array containing map information
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
     * Using the fileToArray function, read the map array and place blocks of the corresponding type in the correct position in the map.
     * @param c The current canvas
     */
    protected void drawBlocks(Canvas c){
        int[][] placement = fileToArray(map);

        int xOffset = 0;
        int yOffset = dungeonTile.getWidth();
        for(int y = 0; y<arenaDown; y++){ //Changes the row drawn to
            for(int x = 0; x<arenaAcross; x++){ //Changes column drawn to
                if(placement[x][y] == 1){ //Checks id to see which block to draw
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

                xOffset += dungeonTile.getWidth(); //So next block is drawn in correct position
            }
            xOffset = 0; //Reset x
            yOffset += dungeonTile.getWidth(); //Add to y
        }
    }

    /**
     * Adds and scales all bitmaps to be drawn onto the canvas
     */
    protected void bitMapSetup(){
        triforceGet = BitmapFactory.decodeResource(getResources(), R.drawable.triforceget); //Decodes image
        triforceGet = triforceGet.createScaledBitmap(triforceGet, tileWidth, tileWidth*2, false); //Scales to correct phone size

        stalfosSpriteSheet = BitmapFactory.decodeResource(getResources(),R.drawable.stalfosspritesheet);
        stalfosSpriteSheet = stalfosSpriteSheet.createScaledBitmap(stalfosSpriteSheet, tileWidth*2, tileWidth, false);

        oneRupee = BitmapFactory.decodeResource(getResources(), R.drawable.one);
        oneRupee = oneRupee.createScaledBitmap(oneRupee, (tileWidth-(tileWidth/8))*2, tileWidth-(tileWidth/8), false);

        triforceSpriteSheet = BitmapFactory.decodeResource(getResources(),R.drawable.tfsheet);
        triforceSpriteSheet = triforceSpriteSheet.createScaledBitmap(triforceSpriteSheet, tileWidth*2, tileWidth, false);

        keeseSpriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.keesesprites);
        keeseSpriteSheet = keeseSpriteSheet.createScaledBitmap(keeseSpriteSheet, tileWidth*2, tileWidth, false);

        linkSpriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.spritesheet);
        linkSpriteSheet = linkSpriteSheet.createScaledBitmap(linkSpriteSheet, (tileWidth*2),(tileWidth*4), false);

        invinciLinkSpriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.invinciblespritesheet);
        invinciLinkSpriteSheet = invinciLinkSpriteSheet.createScaledBitmap(invinciLinkSpriteSheet, (tileWidth*2),(tileWidth*4), false);

        heart = BitmapFactory.decodeResource(getResources(),R.drawable.full);
        heart = heart.createScaledBitmap(heart, (tileWidth/2), ((tileWidth/2)/28)*32, false);
        halfHeart = BitmapFactory.decodeResource(getResources(),R.drawable.half);
        halfHeart = halfHeart.createScaledBitmap(halfHeart, (tileWidth/2), ((tileWidth/2)/28)*32, false);
        emptyHeart = BitmapFactory.decodeResource(getResources(),R.drawable.empty);
        emptyHeart = emptyHeart.createScaledBitmap(emptyHeart, (tileWidth/2), ((tileWidth/2)/28)*32, false);

        rupeeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.rupeeicon);
        rupeeIcon = rupeeIcon.createScaledBitmap(rupeeIcon, tileWidth/2, tileWidth/2, false);

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

    /**
     * Returns the width (in pixels of the phones screen
     * @return
     */
    protected static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /**
     * Returns height of device screen
     * @return
     */
    protected static int getScreenHeight(){
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    /**
     * Places a floor tile bitmap across the dungeon. The tiles scale to the phone screen and should fit 8 tiles across and 11 down
     * @param c The current canvas
     */
    protected void placeTiles(Canvas c){

        int xOffset = 0;
        int yOffset = dungeonTile.getWidth();
        for(int y = 0; y<arenaDown; y++){ //Changes y
            for(int x = 0; x<arenaAcross; x++){ //Changes x
                c.drawBitmap(dungeonTile, xOffset,yOffset,new Paint());
                xOffset += dungeonTile.getWidth();
            }
            xOffset = 0;
            yOffset += dungeonTile.getWidth();
        }
    }

    /**
     * Prints a rect the size of the canvas to the arena
     * @param c The current canvas
     */
    protected void printArea(Canvas c){
        Rect bg = new Rect();
        bg.set(0, (tileWidth), (tileWidth*arenaAcross),(tileWidth*arenaDown));
        Paint fill = new Paint();
        fill.setColor(Color.parseColor("#008088"));
        fill.setStyle(Paint.Style.FILL);

        c.drawRect(bg, fill);
    }

    /**
     * Gets the remaining health of the player and prints it as heart bitmaps across the top of the screen. A full heart represents 2hp, a half heart represents 1hp and an empty heart represents 0hp
     * @param c The current canvas
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

        heartOffset = offset; //offset saved so rupee count doesn't overlap
    }

    /**
     * Prints the total number of rupees collected
     * @param c
     */
    protected void printRupeeCount(Canvas c){
        int offset = heartOffset + tileWidth*2;
        c.drawBitmap(rupeeIcon, offset, 20, new Paint());
        offset += tileWidth/2 + tileWidth/2;

        String msg = "x "+player.getNoRupees();
        Typeface typeface = Typeface.createFromAsset(contextGV.getAssets(), "eightbit.ttf");
        Paint textPaint = new Paint();
        textPaint.setTypeface(typeface);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(tileWidth/2);
        c.drawText(msg, offset, 20 + tileWidth/2, textPaint);

    }

    /**
     * Plays a get rupee sound effect
     */
    protected void playGetRupee(){
        rupeeGet = MediaPlayer.create(contextGV, R.raw.getrupee);
        rupeeGet.start();
    }



}



