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


    private static final int SEED = 100;
    private WindowController windowController;
    private Vector2 windowDimensions;
    private static final int NIGHT_CYCLE = 30;
    private static final float MIN_GAP = 50;
    private static final Color SUN_HALO_COLOR = new Color(255, 0, 0, 20);
    private static final Color MOON_HALO_COLOR = new Color(255, 255, 255, 80);

    //layers
    private static final int SKY_LAYER = Layer.BACKGROUND;
    private static final int SUN_LAYER = Layer.BACKGROUND + 1;
    private static final int SUN_HALO_LAYER = Layer.BACKGROUND + 2;
    private static final int MOON_LAYER = Layer.BACKGROUND + 3;
    private static final int MOON_HALO_LAYER = Layer.BACKGROUND + 4;
    private static final int GROUND_LAYER = Layer.STATIC_OBJECTS;
    private static final int LOWER_GROUND_LAYER = Layer.DEFAULT -10 ;
    private static final int TRUNK_LAYER = Layer.DEFAULT - 8;
    private static final int LEAVES_LAYER = Layer.DEFAULT - 7;
    private static final int AVATAR_LAYER = Layer.DEFAULT;
    private static final int NIGHT_LAYER = Layer.FOREGROUND;

    //tags
    private static final String trunkTag = "trunk";
    private static final String leafTag = "leaf";
    private static final String groundTag = "ground";
    private static final String lowerGroundTag = "lower ground";

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
    private static final int extendBy = 50 * Block.SIZE;;
    private Terrain terrain;


    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        this.windowController = windowController;
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowDimensions = this.windowController.getWindowDimensions(); // gets window dimensions
        //create sky
        Sky.create( gameObjects(), windowDimensions , SKY_LAYER);
        // create terrain
        this.terrain = new Terrain(this.gameObjects(), GROUND_LAYER, windowDimensions);
        // choose seeds
        Random random = new Random();
        int seed = random.nextInt(SEED);
        // create trees
        this.tree = new Tree(this.gameObjects(), terrain, seed, TRUNK_LAYER, LEAVES_LAYER,  trunkTag,  leafTag,  groundTag);
        // create night
        this.night = Night.create(gameObjects(), NIGHT_LAYER, windowDimensions, NIGHT_CYCLE);
        // create sun
        this.sun = Sun.create(gameObjects(), SUN_LAYER, windowDimensions, NIGHT_CYCLE);
        // create halo
        this.sunHalo = SunHalo.create(gameObjects(), SUN_HALO_LAYER, sun, SUN_HALO_COLOR);
        // create moon
        this.moon = Moon.create(gameObjects(), MOON_LAYER, windowDimensions, NIGHT_CYCLE, imageReader);
        // create moon halo
        this.moonHalo = SunHalo.create(gameObjects(), MOON_HALO_LAYER, moon, MOON_HALO_COLOR);
        // create avatar
        this.avatar = Avatar.create(gameObjects(), AVATAR_LAYER, windowDimensions.mult(0.5f), inputListener, imageReader);
        this.avatar.setSounds(soundReader);
        // create camera
        this.camera = new Camera(this.avatar, Vector2.ZERO, windowDimensions, windowDimensions);
        setCamera(camera);
        // create world
        initialWorld();
        // Leaf and block colliding
        gameObjects().layers().shouldLayersCollide(LEAVES_LAYER, GROUND_LAYER, true);
    }// overrides initializeGame

    /**
     * Updates the frame
     * @param deltaTime Current time.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        //the most right x coordinate
        float rightXCoordinate = camera.screenToWorldCoords(windowDimensions).x();
        // the most left x coordinate
        float leftXCoordinate = camera.screenToWorldCoords(windowDimensions).x() - windowDimensions.x();
        // checks if I need to extend to right
        if (rightXCoordinate >= this.rightPointer)
            extendRight(this.rightPointer, rightXCoordinate + extendBy);
        // checks if I need to extend to left
        if (leftXCoordinate <= this.leftPointer)
            extendLeft(leftXCoordinate + MIN_GAP, this.leftPointer - extendBy);
    } //end of update

    private void buildWorld(int start, int end){
        this.terrain.createInRange(start, end);
        this.tree.createInRange(start, end);
    } // end of build world

    // builds initial world
    private void initialWorld() {
        float rightXCoordinate = camera.screenToWorldCoords(windowDimensions).x();
        float leftXCoordinate = camera.screenToWorldCoords(windowDimensions).x() - windowDimensions.x();
        this.leftPointer = (int) (Math.floor(leftXCoordinate / Block.SIZE) * Block.SIZE) - extendBy;
        this.rightPointer = (int) (Math.floor(rightXCoordinate / Block.SIZE) * Block.SIZE) + extendBy;
        buildWorld(this.leftPointer, this.rightPointer);
    } //build initial world

    // extends the world to the right
    private void extendRight(float start, float end){
        int normalizeStart = (int) (Math.floor(start / Block.SIZE) * Block.SIZE); // normalize start position
        int normalizeEnd = (int) (Math.floor(end / Block.SIZE) * Block.SIZE); // normalize end position
        // extend right
        buildWorld(normalizeStart, normalizeEnd);
        // remove irrelevant objects from right
        for (GameObject obj : gameObjects()){
            if (obj.getCenter().x() < leftPointer)
                removeObjects(obj);
        } //end of for loop
        this.rightPointer = normalizeEnd; //update right pointer
        this.leftPointer += (normalizeEnd - normalizeStart); //update left pointer
    } // end of extendRight method

    // extends the world to the left
    private void extendLeft(float start, float end){
        int normalizeStart = (int) (Math.floor(start / Block.SIZE) * Block.SIZE); // normalize start position
        int normalizeEnd = (int) (Math.floor(end / Block.SIZE) * Block.SIZE); // normalize end position
        // extend left
        buildWorld(normalizeStart, normalizeEnd);
        // remove irrelevant objects from right
        for (GameObject obj : gameObjects()){
            if (obj.getCenter().x() > this.rightPointer)
                removeObjects(obj);
        }// end of for loop
        this.leftPointer = normalizeEnd; //update left pointer
        this.rightPointer -= (normalizeStart - normalizeEnd); //update right pointer
    } // end of extend left method

    // removes the objects
    private void removeObjects(GameObject obj){
        // remove ground
        if (obj.getTag() .equals(groundTag))
            gameObjects().removeGameObject(obj, GROUND_LAYER);
        // remove tree trunk
        else if (obj.getTag().equals(trunkTag))
             gameObjects().removeGameObject(obj, TRUNK_LAYER);
        // remove leaves
        else if (obj.getTag().equals(leafTag))
             gameObjects().removeGameObject(obj, LEAVES_LAYER);
        // remove bottom bricks
        else if (obj.getTag().equals(lowerGroundTag))
            gameObjects().removeGameObject(obj, LOWER_GROUND_LAYER);
    } // end of method remove objects

    /**
     * Runs the entire simulation.
     * @param args This argument should not be used.
     */
    public static void main(String[] args){
    new PepseGameManager().run();
    } // end of main

} // end of PepseGameManager
