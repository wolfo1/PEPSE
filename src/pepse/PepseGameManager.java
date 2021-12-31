package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Moon;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;

import java.awt.*;
import java.util.Random;

public class PepseGameManager extends GameManager {

    private static final int TRUNK_LAYER = 5;
    private static final int LEAVES_LAYER = 15;
    private static final int GROUND_LAYER = 0 ;
    private static final int NIGHT_CYCLE = 80;
    private static final Color SUN_HALO_COLOR = new Color(255, 0, 0, 20);
    private static final Color MOON_HALO_COLOR = new Color(255, 255, 255, 80);
    private static final int BLOCKS = 100;
    private final String trunkTag = "trunk";
    private final String leafTag = "leaf";
    private final String groundTag = "ground";


    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        Vector2 windowDimensions = windowController.getWindowDimensions(); // gets window dimensions
         //create sky
        Sky.create( gameObjects(), windowDimensions , Layer.BACKGROUND);
        // create terrain
        Terrain terrain = new Terrain(this.gameObjects(), Layer.STATIC_OBJECTS, windowDimensions);
        terrain.createInRange(0, (int)(BLOCKS * Block.SIZE));
        // choose seeds
        Random random = new Random();
        int seed = random.nextInt(100);
        // create trees
        Tree tree = new Tree(this.gameObjects(), terrain, seed, TRUNK_LAYER, LEAVES_LAYER,  trunkTag,  leafTag,  groundTag);
        tree.createInRange(0, (int)windowDimensions.x());
        GameObject night = Night.create(gameObjects(), Layer.FOREGROUND, windowDimensions, NIGHT_CYCLE);
        // create sun
        GameObject sun = Sun.create(gameObjects(), Layer.BACKGROUND, windowDimensions, NIGHT_CYCLE);
        // create halo
        GameObject sunHalo = SunHalo.create(gameObjects(), Layer.BACKGROUND + 1, sun, SUN_HALO_COLOR);
        // create moon
        GameObject moon = Moon.create(gameObjects(), Layer.BACKGROUND, windowDimensions, NIGHT_CYCLE, imageReader);
        // create moon halo
        GameObject moonHalo = SunHalo.create(gameObjects(), Layer.BACKGROUND + 1, moon, MOON_HALO_COLOR);



        // Leaf and block colliding
        gameObjects().layers().shouldLayersCollide(LEAVES_LAYER, GROUND_LAYER, true);
    } // overrides initializeGame


    /**
     * Runs the entire simulation.
     * @param args This argument should not be used.
     */
    public static void main(String[] args){
        new PepseGameManager().run();
    } // end of main

} // end of PepseGameManager
