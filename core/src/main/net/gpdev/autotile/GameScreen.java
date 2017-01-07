package net.gpdev.autotile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends ScreenAdapter {

    private static final int MAP_WIDTH = 16;
    private static final int MAP_HEIGHT = 12;
    private static final String PROMPT_TEXT = "Click anywhere to generate a new map";
    private static final Color PROMPT_COLOR = Color.CORAL;
    private static final float PROMPT_FADE_IN = 2f;
    private static final float PROMPT_FADE_OUT = 4f;

    private TiledMap map;
    private OrthographicCamera camera;
    private OrthographicCamera guiCam;
    private Viewport viewport;
    private ScreenViewport screenViewport;
    private OrthogonalTiledMapRenderer renderer;
    private AutoTiler autoTiler;
    private BitmapFont font;
    private GlyphLayout layout = new GlyphLayout();
    private SpriteBatch batch;
    private float elapsedTime = 0;

    @Override
    public void show() {
        super.show();

        // Setup camera
        camera = new OrthographicCamera();
        viewport = new FitViewport(MAP_WIDTH, MAP_HEIGHT, camera);

        // Setup GUI camera
        guiCam = new OrthographicCamera();
        screenViewport = new ScreenViewport(guiCam);
        guiCam.setToOrtho(false);

        // Setup font rendering
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("arial-15.fnt"), false);
        font.setColor(PROMPT_COLOR);
        layout.setText(font, PROMPT_TEXT);

        // Auto generate a new map
        autoTiler = new AutoTiler(MAP_WIDTH, MAP_HEIGHT, Gdx.files.internal("tileset.json"));
        map = autoTiler.generateMap();

        // Setup map renderer
        final float unitScale = 1f / Math.max(autoTiler.getTileWidth(), autoTiler.getTileHeight());
        renderer = new OrthogonalTiledMapRenderer(map, unitScale);

        // Setup input processor
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Generate a new procedural map on touch event
                map = autoTiler.generateMap();
                elapsedTime = 0;
                return true;
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
        screenViewport.update(width, height);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render map
        viewport.apply(true);
        renderer.setView(camera);
        renderer.render();

        elapsedTime += delta;

        // Render text prompt
        screenViewport.apply(true);
        batch.setProjectionMatrix(guiCam.combined);
        batch.begin();
        font.setColor(PROMPT_COLOR.r, PROMPT_COLOR.g, PROMPT_COLOR.b,
                (elapsedTime - PROMPT_FADE_IN) % PROMPT_FADE_OUT);
        font.draw(batch, PROMPT_TEXT,
                (screenViewport.getScreenWidth() - layout.width) / 2.0f,
                screenViewport.getScreenHeight() - layout.height);
        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        map.dispose();
        font.dispose();
        batch.dispose();
    }
}
