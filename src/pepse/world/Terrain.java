package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.util.Vector2;

public class Terrain {
    /**
     * Constructs a terrain
     * @param gameObjects The collection of all participating game objects.
     * @param groundLayer  The number of the layer to which the created ground objects should be added.
     * @param windowDimensions  The dimensions of the windows.
     * @param seed  A seed for a random number generator.
     */
    public Terrain (GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed){
        //empty constructor
    } // end of constructor

    /**
     * This method return the ground height at a given location.
     * @param minX The lower bound of the given range (will be rounded to a multiple of Block.SIZE).
     * @param maxX The upper bound of the given range (will be rounded to a multiple of Block.SIZE).
     */
    public void createInRange(int minX, int maxX){
    } // end of method createInRange

    /**
     * RThis method return the ground height at a given location.
     * @param x A number
     * @return The ground height at the given location
     */
    public float groundHeightAt(float x){
        return 1;
    } // end of method groundHeightAt
} // end of class Terrain
