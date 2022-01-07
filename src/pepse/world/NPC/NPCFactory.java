package pepse.world.NPC;

import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.WindowController;
import pepse.world.Avatar;
import pepse.world.Terrain;

import java.util.Random;

/**
 * class in charge of creating NPCs (non-playable-character).
 */
public class NPCFactory {
    // constants
    private static final int NUM_OF_TYPES = 1;
    // fields
    private final Random rand;
    private final Avatar avatar;
    private final GameObjectCollection gameObjects;
    private final ImageReader imageReader;
    private final SoundReader soundReader;
    private final int enemiesLayer;
    private final WindowController windowController;
    private final Terrain terrain;
    private final String enemyTag;

    /**
     * default c'tor
     * @param seed main random seed of the world
     * @param avatar the avatar, for enemy AI
     * @param gameObjects game object collection
     * @param imageReader Image Reader
     * @param soundReader Sound Reader
     * @param enemiesLayer layer to place enemies on
     * @param windowController WindowController
     * @param terrain terrain to calculate groundHeight
     * @param enemyTag Enemies tag
     */
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

    /**
     * method creates a random enemy and puts it in xLocation
     * @param xLocation location to place enemy
     * @return created Enemy
     */
    public Enemy createEnemy(float xLocation) {
        // modular, to support adding more types of enemies later. currently, only 1.
        switch (rand.nextInt(NUM_OF_TYPES)) {
            // create skeleton
            case 0:
                return Skeleton.create(xLocation, avatar, gameObjects, imageReader, soundReader,
                        windowController.getWindowDimensions(), terrain, enemiesLayer, enemyTag);
            default:
                return null;
        }
    }
}
