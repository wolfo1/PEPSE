package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class Sky {

    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");

    /**
     * A method that creates a sky game object.
     * @param gameObjects The collection of all participating game objects.
     * @param windowDimensions The number of the layer to which the created game object should be added.
     * @param skyLayer The number of the layer to which the created sky should be added.
     * @return A new game object representing the sky.
     */
    public static GameObject create(GameObjectCollection gameObjects, Vector2 windowDimensions, int skyLayer){
        GameObject sky = new GameObject(
                Vector2.ZERO, windowDimensions,
                new RectangleRenderable(BASIC_SKY_COLOR));

        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sky, skyLayer);
        sky.setTag("sky");
        return sky;

    } // end of method create

} // end of class Sky
