package net.gpdev.autotile;

import com.badlogic.gdx.Game;

public class AutoTileGdx extends Game {

    private GameScreen gameScreen;

    @Override
    public void create() {
        gameScreen = new GameScreen();
        setScreen(gameScreen);
    }

    @Override
    public void dispose() {
        super.dispose();
        gameScreen.dispose();
    }
}
