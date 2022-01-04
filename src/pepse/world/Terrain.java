package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.PerlinNoise;
import pepse.util.ColorSupplier;

import java.awt.*;

public class Terrain {
    // fields
    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private static float groundHeightAtX0;
    // colour
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    //constants
    private static final float DEPTH = 7 ;
    private static final float TWO_THIRDS = 2/3f;
    private static final int TWO = 2;
    private static final int TERRAIN_DEPTH = 20;
    private static final int LOWER_GROUND_LAYER = Layer.DEFAULT - 10;
    private final PerlinNoise perlinNoise;
    // tags
    private static final String groundTag =  "ground";
    private static final String lowerGroundTag = "lower ground";

    /**
     * Constructs a terrain
     * @param gameObjects The collection of all participating game objects.
     * @param groundLayer  The number of the layer to which the created ground objects should be added.
     * @param windowDimensions  The dimensions of the windows.
     */
    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed){
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.groundHeightAtX0 = windowDimensions.y() * TWO_THIRDS;
        perlinNoise = new PerlinNoise();
        perlinNoise.setSeed(seed);
    } // end of constructor

    /**
     * This method return the ground height at a given location.
     * @param minX The lower bound of the given range (will be rounded to a multiple of Block.SIZE).
     * @param maxX The upper bound of the given range (will be rounded to a multiple of Block.SIZE).
     */
    public void createInRange(int minX, int maxX){
        if(minX > maxX){
            int temp = minX;
            minX = maxX;
            maxX = temp;
        } // end of if
        for (int i = ((minX / Block.SIZE) * Block.SIZE); i < maxX; i += Block.SIZE){
            for (int j = 0; j < TERRAIN_DEPTH; j++) {
                Renderable ground = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                Block block = new Block(new Vector2(i, groundHeightAt(i) + j*Block.SIZE), ground);
                if (j < TWO) {
                    gameObjects.addGameObject(block, groundLayer); // adds to gameObjects
                    block.setTag(groundTag);  // sets tag
                } // end of if
                else {
                    gameObjects.addGameObject(block, LOWER_GROUND_LAYER); // adds to gameObjects
                    block.setTag(lowerGroundTag); // adds to gameObjects
                } // end of else
            } // end of inner for loop
        } // end of outer for loop
    } // end of method createInRange

    /**
     * This method return the ground height at a given location.
     * @param x A number
     * @return The ground height at the given location
     */
    public float groundHeightAt(float x){
        return (int)((this.groundHeightAtX0 +(float)( perlinNoise.noise(x) * Block.SIZE) )/Block.SIZE) * Block.SIZE;
    } // end of method groundHeightAt

} // end of class Terrain