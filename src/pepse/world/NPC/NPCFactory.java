package pepse.world.NPC;

import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.WindowController;
import pepse.world.Avatar;
import pepse.world.Terrain;

import java.util.Random;

public class NPCFactory {
    private static final int NUM_OF_TYPES = 1;

    private final Random rand;
    private final Avatar avatar;
    private final GameObjectCollection gameObjects;
    private final ImageReader imageReader;
    private final SoundReader soundReader;
    private final int enemiesLayer;
    private final WindowController windowController;
    private final Terrain terrain;
    private final String enemyTag;

    public NPCFactory (int seed, Avatar avatar, GameObjectCollection gameObjects, ImageReader imageReader,
                       SoundReader soundReader, int enemiesLayer, WindowController windowController, Terrain terrain,
                       String enemyTag) {
        this.rand = new Random(seed);
        this.avatar = avatar;
        this.gameObjects = gameObjects;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        this.enemiesLayer = enemiesLayer;
        this.windowController = windowController;
        this.terrain = terrain;
        this.enemyTag = enemyTag;
    }

    public Enemy createEnemy(float xLocation) {
        switch (rand.nextInt(NUM_OF_TYPES)) {
            case 0:
                return Skeleton.create(xLocation, avatar, gameObjects, imageReader, soundReader,
                        windowController.getWindowDimensions(), terrain, enemiesLayer, enemyTag);
            default:
                return null;
        }
    }
}
