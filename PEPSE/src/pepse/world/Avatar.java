package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.util.Vector2;

public class Avatar {
    public Avatar() {
        //empty constructor
    }  // end of constructor


    /**
     * Method that creates an Avatar
     * @param gameObjects The collection of all participating game objects.
     * @param layer The number of the layer to which the created avatar should be added.
     * @param topLeftCorner The location of the top-left corner of the created avatar.
     * @param inputListener Used for reading input from the user.
     * @param imageReader Used for reading images from disk or from within a jar.
     * @return A newly created representing the avatar.
     */
    public static Avatar create(GameObjectCollection gameObjects, int layer, Vector2 topLeftCorner, UserInputListener inputListener, ImageReader imageReader){
        return null;
    } // end of method create

} // end of class Avataer
