package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.world.Avatar;
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


    private WindowController windowController;
    private Vector2 windowDimensions;
    private static final int NIGHT_CYCLE = 30;
    private static final Color SUN_HALO_COLOR = new Color(255, 0, 0, 20);
    private static final Color MOON_HALO_COLOR = new Color(255, 255, 255, 80);
    private static final int BLOCKS = 100;

    //layers
    private static int TRUNK_LAYER = 5;
    private static int LEAVES_LAYER = 15;
    private static int GROUND_LAYER = 0 ;

    //tags
    private String trunkTag = "trunk";
    private String leafTag = "leaf";
    private String groundTag = "ground";

    // game objects
    private Tree tree;
    private GameObject night;
    private GameObject sun;
    private GameObject sunHalo;
    private GameObject moon;
    private GameObject moonHalo;
    private Avatar avatar;
    private Camera camera;

    // camera
    private int leftPointer;
    private int rightPointer;
    private int extendBy = 5 * Block.SIZE;;
    private Terrain terrain;


    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        this.windowController = windowController;
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowDimensions = this.windowController.getWindowDimensions(); // gets window dimensions
        //create sky
        Sky.create( gameObjects(), windowDimensions , Layer.BACKGROUND);
        // create terrain
        this.terrain = new Terrain(this.gameObjects(), Layer.STATIC_OBJECTS, windowDimensions);
        terrain.createInRange(0, (int)(BLOCKS * Block.SIZE));
        // choose seeds
        Random random = new Random();
        int seed = random.nextInt(100);
        // create trees
        this.tree = new Tree(this.gameObjects(), terrain, seed, TRUNK_LAYER, LEAVES_LAYER,  trunkTag,  leafTag,  groundTag);
        tree.createInRange(0, (int)windowDimensions.x());
        // create night
        this.night = Night.create(gameObjects(), Layer.FOREGROUND, windowDimensions, NIGHT_CYCLE);
        // create sun
        this.sun = Sun.create(gameObjects(), Layer.BACKGROUND, windowDimensions, NIGHT_CYCLE);
        // create halo
        this.sunHalo = SunHalo.create(gameObjects(), Layer.BACKGROUND + 1, sun, SUN_HALO_COLOR);
        // create moon
        this.moon = Moon.create(gameObjects(), Layer.BACKGROUND, windowDimensions, NIGHT_CYCLE, imageReader);
        // create moon halo
        this.moonHalo = SunHalo.create(gameObjects(), Layer.BACKGROUND + 1, moon, MOON_HALO_COLOR);
        // create avatar
        this.avatar = Avatar.create(gameObjects(), Layer.DEFAULT, windowDimensions.mult(0.5f), inputListener, imageReader);
        // create camera
        this.camera = new Camera(this.avatar, Vector2.ZERO, windowDimensions, windowDimensions);
        setCamera(camera);
        // Leaf and block colliding
        gameObjects().layers().shouldLayersCollide(LEAVES_LAYER, GROUND_LAYER, true);

        }// overrides initializeGame

//    @Override
//    public void update(float deltaTime) {
//        super.update(deltaTime);
//        //the most right x coordinate
//        float rightXCoordinate = camera.screenToWorldCoords(windowDimensions).x();
//        // the most left x coordinate
//        float leftXCoordinate = camera.screenToWorldCoords(windowDimensions).x() - windowDimensions.x();
//        // checks if I need to extend to right
//        float end;
//        if (rightXCoordinate >= rightPointer) {
//            end = rightXCoordinate + extendBy;
//            extendRight(rightPointer, end);
//        } // end of if rightXCoordinate >= rightPointer
//        // checks if I need to extend to left
//        if (leftXCoordinate <= leftPointer) {
//            end = rightXCoordinate + extendBy;
//            extendLeft(leftXCoordinate, end);
//        } // end of if leftXCoordinate <>>= leftPointer
//    } //end of update

    private void extendRight(float start, float end){
        int normalizeStart = (int) (Math.floor(start / Block.SIZE) * Block.SIZE); // normalize start position
        int normalizeEnd = (int) (Math.floor(end / Block.SIZE) * Block.SIZE); // normalize end position
        // build world to right
        this.terrain.createInRange(normalizeStart, normalizeEnd);
        this.tree.createInRange(normalizeStart, normalizeEnd);
        // remove world from left
        for (GameObject obj : gameObjects()){
            if (obj.getCenter().x() < leftPointer)
                removeObjects(obj);
        } //end of for loop
        rightPointer = normalizeEnd; //update right pointer
       leftPointer = leftPointer + normalizeEnd - normalizeStart; //update left pointer
    } // end of extendRight method

    private void extendLeft(float start, float end){
        int normalizeStart = (int) (Math.floor(start / Block.SIZE) * Block.SIZE); // normalize start position
        int normalizeEnd = (int) (Math.floor(end / Block.SIZE) * Block.SIZE); // normalize end position
        // build world to left
        this.terrain.createInRange(normalizeStart, normalizeEnd);
        this.tree.createInRange(normalizeStart, normalizeEnd);
        // remove world from right
        for (GameObject obj : gameObjects()){
            if (obj.getCenter().x() > rightPointer)
                removeObjects(obj);
        }// end of for loop
        leftPointer = normalizeEnd; //update left pointer
        rightPointer = rightPointer - normalizeStart + normalizeEnd; //update right pointer
    } // end of extend left method

    private void removeObjects(GameObject obj){
        // remove ground
        if (obj.getTag().equals(groundTag))
            gameObjects().removeGameObject(obj, GROUND_LAYER);
        // remove tree trunk
        if (obj.getTag().equals(trunkTag))
             gameObjects().removeGameObject(obj, TRUNK_LAYER);
        // remove leaves
        if (obj.getTag().equals(leafTag))
             gameObjects().removeGameObject(obj, LEAVES_LAYER);
    } // end of method remove objects

    /**
     * Runs the entire simulation.
     * @param args This argument should not be used.
     */
    public static void main(String[] args){
    new PepseGameManager().run();
    } // end of main

} // end of PepseGameManager
