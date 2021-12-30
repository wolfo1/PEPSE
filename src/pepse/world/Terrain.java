package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;

public class Terrain {
    private GameObjectCollection gameObjects;
    private int groundLayer;
    private static float groundHeightAtX0;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 20;

    /**
     * Constructs a terrain
     * @param gameObjects The collection of all participating game objects.
     * @param groundLayer  The number of the layer to which the created ground objects should be added.
     * @param windowDimensions  The dimensions of the windows.
     */
    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions){
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.groundHeightAtX0 = windowDimensions.y();
        this.groundHeightAtX0 = windowDimensions.y() * 2 / 3;
    } // end of constructor

    /**
     * This method return the ground height at a given location.
     * @param minX The lower bound of the given range (will be rounded to a multiple of Block.SIZE).
     * @param maxX The upper bound of the given range (will be rounded to a multiple of Block.SIZE).
     */
    public void createInRange(int minX, int maxX){
        Renderable ground = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
        for (int i = (int)((minX / Block.SIZE) * Block.SIZE); i < maxX; i += Block.SIZE){
            for (int j = 0; j < TERRAIN_DEPTH; j++) {
                Block block = new Block(new Vector2(i, groundHeightAt(i) + j*Block.SIZE), ground);
                block.setTag("block");  // sets tag
                gameObjects.addGameObject(block, groundLayer); // adds to gameObjects
            } // end of inner for loop
        } // end of outer for loop
    } // end of method createInRange

    /**
     * This method return the ground height at a given location.
     * @param x A number
     * @return The ground height at the given location
     */
    public float groundHeightAt(float x){
        return (int)((groundHeightAtX0 + (float) Math.sin(x/5) * Block.SIZE * 2)/30) * 30;
    } // end of method groundHeightAt

} // end of class Terrain