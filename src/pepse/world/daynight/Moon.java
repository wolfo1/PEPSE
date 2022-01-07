package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.util.Vector2;

public class Moon {
    // assets
    private static final String MOON_IMAGE_PATH = "src/assets/moon.png";
    private static final String MOON_TAG = "moon";
    // constants
    private static final Vector2 DIMENSIONS = new Vector2(250, 250);
    // the sun will revolve around the center of the screen + this offset
    private static final Vector2 CENTER_OFFSET = new Vector2(0, 200);
    // adds 100 to the horizontal axis of the circle around the center of the screen.
    private static final int OVAL_A_OFFSET = 100;

    /**
     * A method that creates a moon object.
     * @param gameObjects  The collection of all participating game objects.
     * @param layer The number of the layer to which the created sun should be added.
     * @param windowDimensions The dimensions of the windows.
     * @param cycleLength The amount of seconds it should take the created game object to complete a full cycle.
     * @return A new game object representing the sun.
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,
                                    float cycleLength, ImageReader imageReader){
        // creates moon object and adds its to the game
        GameObject moon = new GameObject(Vector2.ZERO, DIMENSIONS, imageReader.readImage(MOON_IMAGE_PATH, true));
        moon.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(moon, layer);
        moon.setTag(MOON_TAG);
        Vector2 central = windowDimensions.mult(0.5f).add(CENTER_OFFSET);
        // oval trajectory of the moon around the central point, adjusted to revolve counter to the sun.
        new Transition<>(
                moon,
                angle -> {
                    float cos = (float) Math.cos(Math.toRadians(angle));
                    float sin = (float) Math.sin(Math.toRadians(angle));
                    moon.setCenter(new Vector2((central.x() + OVAL_A_OFFSET) * cos + central.x(),central.y() * sin + central.y()));
                },
                80f, // cycle of 360, adjusted to start in a position according to night cycle
                470f,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null);
        return moon;
    } // end of method create

} // end of class Moon
