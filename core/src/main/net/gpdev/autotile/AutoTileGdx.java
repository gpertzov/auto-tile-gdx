package net.gpdev.autotile;

import com.badlogic.gdx.Game;

public class AutoTileGdx extends Game {

    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
