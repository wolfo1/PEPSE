package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;

public class PepseGameManager extends GameManager {

    private WindowController windowController;
    private Vector2 windowDimensions;
    private static final int BlocksInSeason = 100;

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        this.windowController = windowController;
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowDimensions = this.windowController.getWindowDimensions(); // gets window dimensions
       //create sky
        Sky.create( gameObjects(), windowDimensions , Layer.BACKGROUND);
        // create terrain
        Terrain terrain = new Terrain(this.gameObjects(), Layer.STATIC_OBJECTS, windowDimensions);
        terrain.createInRange(0, (int)(BlocksInSeason * Block.SIZE));
        // choses seeds
        Random random = new Random();
        int seed = random.nextInt(100);
        // create trees
        Tree tree = new Tree(this.gameObjects(), terrain, seed, ROOT_LAYER, LEAVES_LAYER);
        tree.createInRange(0, (int)windowDimensions.x());
        // create night
        GameObject night = Night.create(gameObjects(), Layer.FOREGROUND, windowDimensions, 30);
        // create sun
        GameObject sun = Sun.create(gameObjects(), Layer.BACKGROUND, windowDimensions, 30);
    } // overrides initializeGame


    /**
     * Runs the entire simulation.
     * @param args This argument should not be used.
     */
    public static void main(String[] args){
    new PepseGameManager().run();
    } // end of main

} // end of PepseGameManager
