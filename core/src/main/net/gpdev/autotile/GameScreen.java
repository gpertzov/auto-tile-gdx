package net.gpdev.autotile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class GameScreen extends ScreenAdapter {

    private static final int MAP_WIDTH = 16;
    private static final int MAP_HEIGHT = 12;
    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;
    private static final float MAP_UNIT_SCALE = 1 / 32f;

    private TiledMap map;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer renderer;

    @Override
    public void show() {
        super.show();

        // Setup camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, MAP_WIDTH, MAP_HEIGHT);

        // Auto generate a new map
        final AutoTiler autoTiler = new AutoTiler(MAP_WIDTH, MAP_HEIGHT, TILE_WIDTH, TILE_HEIGHT,
                Gdx.files.internal("RPGTiles.png"));
        map = autoTiler.generateMap();
        renderer = new OrthogonalTiledMapRenderer(map, MAP_UNIT_SCALE);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        renderer.setView(camera);
        renderer.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        map.dispose();
    }
}
