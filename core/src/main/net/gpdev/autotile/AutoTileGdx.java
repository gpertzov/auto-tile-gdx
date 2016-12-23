package net.gpdev.autotile;

import com.badlogic.gdx.Game;

public class AutoTileGdx extends Game {

    private GameScreen screen;

    @Override
    public void create() {
        screen = new GameScreen();
        setScreen(screen);
    }

    @Override
    public void dispose() {
        super.dispose();
        screen.dispose();
    }
}
