package com.example.androidcw;

public class Rupee extends Collectible {

    protected int value;

    /**
     * A treasure that can be collected
     * @param cX
     * @param cY
     */
    public Rupee(int cX, int cY) {
        super(cX, cY);
        x = cX;
        y = cY;
        type = 2;
        collected = false;

        value = 1;
    }

    @Override
    public void onPlayerTouch(GameView view, Link l) {
        if(!collected){
            l.setNoRupees(l.getNoRupees()+1); //Updates rupee count
            view.playGetRupee();
            collected = true;
        }
    }

}
