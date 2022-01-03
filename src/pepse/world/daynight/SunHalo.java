package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class SunHalo {
    private static final String HALO_TAG = "Halo";
    private static final Vector2 HALO_DIMENSIONS = new Vector2(300, 300);
    // there could be several halos for different objects, so a running index to differentiate them
    private static int haloCount = 0;

    /**
     * A method that creates a SunHalo object.
     * @param gameObjects The collection of all participating game objects.
     * @param layer  The number of the layer to which the created halo should be added.
     * @param sun A game object representing the sun (it will be followed by the created game object).
     * @param color The color of the halo.
     * @return A new game object representing the sun's halo.
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer, GameObject sun, Color color){
        OvalRenderable renderable = new OvalRenderable(color);
        GameObject halo = new GameObject(Vector2.ZERO, HALO_DIMENSIONS, renderable);
        gameObjects.addGameObject(halo, layer);
        halo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        halo.setTag(HALO_TAG + haloCount);
        haloCount ++;
        // move the halo according to the given object's location
        halo.addComponent(deltaTime -> halo.setCenter(sun.getCenter()));
        return halo;
    } // end of method create

} // end of class SunHalo
