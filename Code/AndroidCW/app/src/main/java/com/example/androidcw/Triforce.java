package com.example.androidcw;

public class Triforce extends Collectible {

    /**
     * Triforce Collectible
     * @param cX
     * @param cY
     */
    public Triforce(int cX, int cY) {
        super(cX, cY);
        x = cX;
        y = cY;
        type = 1;
        collected = false;
    }

    @Override
    public void onPlayerTouch(GameView view, Link l) {
        view.setGameWon(true);
        collected = true;
    }
}
