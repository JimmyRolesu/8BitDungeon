package com.example.androidcw;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.ArrayList;

public class Link {
    private int hp;
    private int maxHp;
    private int x, oldX;
    private int y, oldY;
    private int width;
    private int direction; //0=down 1=up 2=left 3=right
    private int xSpeed, ySpeed;
    private boolean invincible;
    private int noRupees;


    /**
     * An object for the player controlled character that is drawn to the canvas
     * @param playerX
     * @param playerY
     * @param pWidth
     */
    public Link(int playerX, int playerY, int pWidth) {
        invincible = true;
        noRupees = 0;
        maxHp = 6;
        hp = maxHp;
        x = playerX;
        y = playerY;
        oldX = x;
        oldY = y;
        width = pWidth;
        direction = 0;
        xSpeed = ySpeed = 0;
    }

    public int getNoRupees() {
        return noRupees;
    }

    /**
     * Sets the new rupee count
     * @param newRupee
     */
    public void setNoRupees(int newRupee) {
        if (newRupee >255){
            newRupee = 255;
        }
        noRupees = newRupee;
    }

    public void setInvincible(){
        invincible = false;
    }

    public boolean isInvincible(){
        return invincible;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getOldX(){
        return oldX;
    }

    public int getOldY(){
        return oldY;
    }

    private void setOldX(int i){
        oldX = i;
    }

    private void setOldY(int i){
        oldY = i;
    }

    public int getWidth(){
        return width;
    }

    public int getHp(){
        return hp;
    }

    public int getMaxHp(){
        return maxHp;
    }

    public void updateY(int newY){
        y = newY;
    }

    public void updateX(int newX){
        x = newX;
    }

    public void subtractHp(int d){
        hp += d;
    }

    public int getDirection(){
        return direction;
    }

    public void setDirection(int i){
        direction = i;
    }

    public int getxSpeed() {
        return xSpeed;
    }
    public int getySpeed(){
        return ySpeed;
    }

    /**
     * Sets Link's coordinates based on accelerometer data
     * @param x
     * @param y
     */
    public void setLinkCoordinates(float x, float y){
        setOldX(getX());
        setOldY(getY());

        xSpeed = Math.round(x);
        ySpeed = Math.round(y);


        //Limits player directions to 4 ways of movement
        if(Math.abs(y)>Math.abs(x)){
            x = 0;
        }
        else if(Math.abs(x)>Math.abs(y)){
            y=0;
        }

        if (Math.abs(x) < 1){
            xSpeed=0;
            x = 0;
        }
        else if(Math.abs(y) < 1){
            ySpeed=0;
            y = 0;
        }

        //Limits the speed of the player so tilting the phone past a point doesn't increase speed.
        if(y>2){
            y = 2;
        }
        else if(y<-2){
            y = -2;
        }

        if(x>2){
            x = 2;
        }
        else if(x<-2){
            x = -2;
        }

        int newY = Math.round(y)*2;
        int newX = - Math.round(x)*2;
        updateY(getY()+newY);
        updateX(getX()+newX);

        if(y>=0 && x>=0 && y>x){
            setDirection(0); //Down
        }
        if(y>=0 && x>=0 && x>y){
            setDirection(2); //Right
        }

        if(y<=0 && x<=0 && x>y){
            setDirection(1); //Up
        }

        if(y<=0 && x<=0 && y>x){
            setDirection(3); //Left
        }
    }

    /**
     * Checks if colliding with edges of dungeon
     * @param screenWidth
     * @param tileWidth
     * @param arenaDown
     */
    public void checkCollision(int screenWidth, int tileWidth, int arenaDown){
        if (getX() > screenWidth-tileWidth){
            updateX(screenWidth-tileWidth);
        }
        if (getX() < 0){
            updateX(0);
        }
        if (getY() > (tileWidth*arenaDown)){
            updateY((tileWidth*arenaDown));
        }
        if (getY()<tileWidth){
            updateY(tileWidth);
        }

    }

    /**
     * Check collisions with blocks
     * @param map
     * @param blockList1
     * @param blockList2
     * @param blockList3
     * @param v
     * @param tileWidth
     */
    public void checkBlockCollisions(String map, ArrayList<Block> blockList1, ArrayList<Block> blockList2, ArrayList<Block> blockList3, GameView v, int tileWidth){
        //If statements check what map is currently loaded

        if(map.equals("lvl1.txt")){
            for(Block b: blockList1){
                int i = b.checkCollision(this, tileWidth);
                if(i == 2){//If touching a stair block
                    v.updateMap();
                }
            }
        }
        if(map.equals("lvl2.txt")){
            for(Block b: blockList2){
                int i = b.checkCollision(this, tileWidth);
                if(i == 2){
                    v.updateMap();
                }
            }
        }
        if(map.equals("lvl3.txt")){
            for(Block b: blockList3){
                int i = b.checkCollision(this, tileWidth);
                if(i == 2){
                    v.updateMap();
                }
            }
        }
    }

    /**
     * Checks if player is colliding with an enemy
     * @param e1
     * @param e2
     * @param e3
     * @param tileWidth
     * @param context
     * @param map
     */
    public void checkEnemyCollisions(ArrayList<Enemy> e1, ArrayList<Enemy> e2, ArrayList<Enemy> e3, int tileWidth, Context context, String map) {
        if(!isInvincible()){
            if(map.equals("lvl1.txt")){
                for(Enemy e: e1){
                    if(getX()<e.getX()+tileWidth && getX()+tileWidth > e.getX() && getY()<e.getY()+tileWidth && getY()+tileWidth > e.getY()){

                        if(direction == 0){ //Down
                            y -= tileWidth;
                        }
                        if(direction == 1){ //Up
                            y += tileWidth;
                        }
                        if(direction == 2){ //Right
                            x += tileWidth;
                        }
                        if(direction == 3){ //Left
                            x-= tileWidth;
                        }

                        hp = hp - e.getDamage();
                        invincible = true;

                        MediaPlayer hit = MediaPlayer.create(context, R.raw.hit);
                        hit.start();
                    }
                }
            }
            if(map.equals("lvl2.txt")){
                for(Enemy e: e2){
                    if(getX()<e.getX()+tileWidth && getX()+tileWidth > e.getX() && getY()<e.getY()+tileWidth && getY()+tileWidth > e.getY()){

                        if(direction == 0){ //Down
                            y -= tileWidth;
                        }
                        if(direction == 1){ //Up
                            y += tileWidth;
                        }
                        if(direction == 2){ //Right
                            x += tileWidth;
                        }
                        if(direction == 3){ //Left
                            x-= tileWidth;
                        }

                        hp = hp - e.getDamage();
                        invincible = true;

                        MediaPlayer hit = MediaPlayer.create(context, R.raw.hit);
                        hit.start();
                    }
                }
            }
            if(map.equals("lvl3.txt")){
                for(Enemy e: e3){
                    if(getX()<e.getX()+tileWidth && getX()+tileWidth > e.getX() && getY()<e.getY()+tileWidth && getY()+tileWidth > e.getY()){

                        if(direction == 0){ //Down
                            y -= tileWidth;
                        }
                        if(direction == 1){ //Up
                            y += tileWidth;
                        }
                        if(direction == 2){ //Right
                            x += tileWidth;
                        }
                        if(direction == 3){ //Left
                            x-= tileWidth;
                        }

                        hp = hp - e.getDamage();
                        invincible = true;

                        MediaPlayer hit = MediaPlayer.create(context, R.raw.hit);
                        hit.start();
                    }
                }
            }
        }


    }

    /**
     * NOT IN USE
     * @param e
     * @param tileWidth
     * @param context
     */
    private void doHit(Enemy e, int tileWidth, Context context){
        if(direction == 0){ //Down
            y -= tileWidth;
        }
        if(direction == 1){ //Up
            y += tileWidth;
        }
        if(direction == 2){ //Right
            x += tileWidth;
        }
        if(direction == 3){ //Left
            x-= tileWidth;
        }

        hp = hp - e.getDamage();

        //MediaPlayer hit = MediaPlayer.create(context, R.raw.hit);
        //hit.start();
    }

    /**
     * Check if colliding with a collectible
     * @param c1
     * @param c2
     * @param c3
     * @param tileWidth
     * @param context
     * @param map
     * @param v
     */
    public void checkCollectibleCollisons(ArrayList<Collectible> c1, ArrayList<Collectible> c2, ArrayList<Collectible> c3, int tileWidth, Context context, String map, GameView v){
        //If statements check what map is currently loaded

        if(map.equals("lvl1.txt")){
            for(Collectible c: c1){
                if(getX()<c.getX()+tileWidth-(tileWidth/8) && getX()+tileWidth-(tileWidth/8) > c.getX() && getY()<c.getY()+tileWidth-(tileWidth/8) && getY()+tileWidth-(tileWidth/8) > c.getY() && !c.isCollected()){

                c.onPlayerTouch(v, this);
                }
            }
        }
        if(map.equals("lvl2.txt")){
            for(Collectible c: c2){
                if(getX()<c.getX()+tileWidth-(tileWidth/8) && getX()+tileWidth-(tileWidth/8) > c.getX() && getY()<c.getY()+tileWidth-(tileWidth/8) && getY()+tileWidth-(tileWidth/8) > c.getY() && !c.isCollected()){

                    c.onPlayerTouch(v, this);
                }
            }
        }
        if(map.equals("lvl3.txt")){
            for(Collectible c: c3){
                if(getX()<c.getX()+tileWidth-(tileWidth/8) && getX()+tileWidth-(tileWidth/8) > c.getX() && getY()<c.getY()+tileWidth-(tileWidth/8) && getY()+tileWidth-(tileWidth/8) > c.getY() && !c.isCollected()){

                    c.onPlayerTouch(v, this);
                }
            }
        }
    }

    /**
     * Returns a true or false value depending if the player still has health or not
     * @return TRUE if hp>0 or FALSE if hp=0
     */
    public boolean checkAlive(){
        if(hp>0){
            return true;
        }
        else{
            return false;
        }
    }
}
