package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;

public class Terrain1 {
    private GameObjectCollection gameObjects;
    private int groundLayer;
    private static float groundHeightAtX0;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);

    /**
     * Constructs a terrain
     * @param gameObjects The collection of all participating game objects.
     * @param groundLayer  The number of the layer to which the created ground objects should be added.
     * @param windowDimensions  The dimensions of the windows.
     * @param seed  A seed for a random number generator.
     */
    public Terrain1(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed){
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        groundHeightAtX0 = (2/3)* windowDimensions.x();
        GameObject terrain = new GameObject(
                Vector2.ZERO, new Vector2(300, 300) , new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
        terrain.setTag("ground");
        gameObjects.addGameObject(terrain, Layer.STATIC_OBJECTS);
        System.out.println("done");
    } // end of constructor

    /**
     * This method return the ground height at a given location.
     * @param minX The lower bound of the given range (will be rounded to a multiple of Block.SIZE).
     * @param maxX The upper bound of the given range (will be rounded to a multiple of Block.SIZE).
     */
    public void createInRange(int minX, int maxX){

    } // end of method createInRange

    /**
     * This method return the ground height at a given location.
     * @param x A number
     * @return The ground height at the given location
     */
    public float groundHeightAt(float x){
        return groundHeightAtX0;
    } // end of method groundHeightAt

} // end of class Terrain