package net.gpdev.autotile;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.gpdev.autotile.AutoTiler.TILE_BITS.*;

public class AutoTiler {

    private static final byte MATCH_ANY = 127;

    public enum TILE_BITS {
        TOP_LEFT(0), TOP_RIGHT(1), BOTTOM_LEFT(2), BOTTOM_RIGHT(3), TERRAIN_TYPE(4);

        private final int value;

        TILE_BITS(int value) {
            this.value = value;
        }

        public int id() {
            return value;
        }
    }


    private final int mapWidth;
    private final int mapHeight;
    private final int tileWidth;
    private final int tileHeight;
    private final int numTiles;
    private final FileHandle tilesFileHandle;
    private final Random random;

    private TiledMapTileSet tileSet;
    private TiledMap map;
    private TiledMapTileLayer mapLayer;


    public AutoTiler(final int mapWidth,
                     final int mapHeight,
                     final int tileWidth,
                     final int tileHeight,
                     final FileHandle tilesFileHandle,
                     final int numTiles) {

        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tilesFileHandle = tilesFileHandle;
        this.numTiles = numTiles;
        this.random = new Random();
    }

    public TiledMap generateMap() {
        if (map == null) {
            initMap();
        }

        // Iterate on map cells from bottom-left to top-right
        for (int row = 0; row < mapHeight; row++) {
            for (int col = 0; col < mapWidth; col++) {
                // Pick next tile
                final int tileId = pickTile(col, row);
                final TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(tileSet.getTile(tileId));
                mapLayer.setCell(col, row, cell);
            }
        }

        return map;
    }

    private int pickTile(final int col, final int row) {
        // Init all match mask elements to "dont-care"
        byte[] matchMask = new byte[]{MATCH_ANY, MATCH_ANY, MATCH_ANY, MATCH_ANY, MATCH_ANY};

        // Get Left Tile Id
        final int leftTileId = getTileId(col - 1, row);
        if (leftTileId >= 0) {
            // Extract tile bit codes
            final byte[] leftBits = getTileBits(leftTileId);

            // Update match mask
            matchMask[TERRAIN_TYPE.id()] = leftBits[TERRAIN_TYPE.id()];
            matchMask[TOP_LEFT.id()] = leftBits[TOP_RIGHT.id()];
            matchMask[BOTTOM_LEFT.id()] = leftBits[BOTTOM_RIGHT.id()];
        }

        // Get Bottom Tile Id
        final int bottTileId = getTileId(col, row - 1);
        if (bottTileId >= 0) {
            // Extract tile bit codes
            final byte[] bottBits = getTileBits(bottTileId);

            // Update match mask
            matchMask[TERRAIN_TYPE.id()] = bottBits[TERRAIN_TYPE.id()];
            matchMask[BOTTOM_LEFT.id()] = bottBits[TOP_LEFT.id()];
            matchMask[BOTTOM_RIGHT.id()] = bottBits[TOP_RIGHT.id()];
        }

        // Find all tiles that corner match left & bottom tiles
        final List<Integer> matchingTiles = findMatchingTiles(matchMask);

        // Pick one of the matching tiles
        final int selectedTile = random.nextInt(matchingTiles.size());

        return matchingTiles.get(selectedTile);
    }

    private List<Integer> findMatchingTiles(final byte[] mask) {
        final List<Integer> matchingTiles = new ArrayList<Integer>();

        final int numBits = mask.length;
        for (int i = 0; i < numTiles; i++) {
            final byte[] bits = getTileBits(i);
            int j = 0;
            for (; j < numBits; j++) {
                if (mask[j] != MATCH_ANY && mask[j] != bits[j]) {
                    break;
                }
            }
            if (j == numBits) {
                matchingTiles.add(i);
            }
        }

        return matchingTiles;
    }

    private int getTileId(final int col, final int row) {
        if (col < 0 || row < 0 || col >= mapWidth || row >= mapHeight) {
            return -1;
        }

        return mapLayer.getCell(col, row).getTile().getId();
    }

    private byte[] getTileBits(final int tileId) {
        return new byte[]{
                (byte) (tileId & 0x1),
                (byte) ((tileId & 0x2) >> 1),
                (byte) ((tileId & 0x4) >> 2),
                (byte) ((tileId & 0x8) >> 3),
                (byte) ((tileId & 0x10) >> 4)
        };
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
                final StaticTiledMapTile tile = new StaticTiledMapTile(splitTiles[i][j]);
                tile.setId(tid++);
                tileSet.putTile(tile.getId(), tile);
            }
        }

        // Create an empty map
        map = new TiledMap();
        map.setOwnedResources(textures);
        mapLayer = new TiledMapTileLayer(mapWidth, mapHeight, tileWidth, tileHeight);
        map.getLayers().add(mapLayer);
    }
}
