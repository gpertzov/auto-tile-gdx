package net.gpdev.autotile;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

public class AutoTiler {
    private final int mapWidth;
    private final int mapHeight;
    private final int tileWidth;
    private final int tileHeight;
    private final FileHandle tilesFileHandle;
    private final Random random;

    private TiledMapTileSet tileSet;
    private TiledMap map;
    private TiledMapTileLayer mapLayer;


    public AutoTiler(final int mapWidth,
                     final int mapHeight,
                     final int tileWidth,
                     final int tileHeight,
                     final FileHandle tilesFileHandle ) {

        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tilesFileHandle = tilesFileHandle;
        this.random = new Random();
    }

    public TiledMap generateMap() {
        if (map == null) {
            initMap();
        }

        // Iterate on map cells
        for (int row = 0; row < mapHeight; row++) {
            for (int col = 0; col < mapWidth; col++) {
                // Generate tile
                final int randomTileId = random.nextInt(tileSet.size()) + 1;
                final TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(tileSet.getTile(randomTileId));
                mapLayer.setCell(col, row, cell);
            }
        }

        return map;
    }

    private void initMap() {
        // Load tiles texture
        final Texture tilesTexture = new Texture(tilesFileHandle);
        final Array<Texture> textures = Array.with(tilesTexture);
        final TextureRegion[][] splitTiles = TextureRegion.split(tilesTexture, tileWidth, tileHeight);

        // Create tileset
        tileSet = new TiledMapTileSet();
        int tid = 0;
        for (int i = 0; i < splitTiles.length; i++) {
            for (int j = 0; j < splitTiles[i].length; j++) {
                tileSet.putTile(++tid, new StaticTiledMapTile(splitTiles[i][j]));
            }
        }

        // Create an empty map
        map = new TiledMap();
        map.setOwnedResources(textures);
        mapLayer = new TiledMapTileLayer(mapWidth, mapHeight, tileWidth, tileHeight);
        map.getLayers().add(mapLayer);
    }
}
