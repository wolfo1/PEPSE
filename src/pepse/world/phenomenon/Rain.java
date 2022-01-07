package pepse.world.phenomenon;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.ScheduledTask;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;


public class Rain extends GameObject {
    // constants
    private static final String RAIN_TAG = "rain";
    private static final float RAIN_OPAQ = 0.2f;
    // assets
    private static final String[] RAIN_ANIMATION = {"src/assets/rain1.png", "src/assets/rain2.png", "src/assets/rain3.png",
            "src/assets/rain4.png", "src/assets/rain5.png"};
    private static final String RAIN_SOUND = "src/assets/rain.wav";
    // static fields
    public static boolean isInstantiated = false;
    // fields
    private final Sound rainSound;

    /**
     * default c'tor
     *
     * @param topLeftCorner location
     * @param dimensions    dimensions vector
     * @param renderable    animation
     * @param duration      duration of rain
     * @param gameObjects   collection to stop the rain
     * @param layer         layer to remove rain from
     * @param soundReader   SoundReader
     */
    public Rain(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, int duration,
                GameObjectCollection gameObjects, int layer, SoundReader soundReader) {
        super(topLeftCorner, dimensions, renderable);
        // play sound
        rainSound = soundReader.readSound(RAIN_SOUND);
        rainSound.playLooped();
        // remove Rain after duration, stop sound and set Instantiated to false.
        new ScheduledTask(this, duration, false, () -> {
            rainSound.stopAllOccurences();
            gameObjects.removeGameObject(this, layer);
            Rain.isInstantiated = false;
        });
    }

    /**
     * creates Rain and adds it to collection
     *
     * @param gameObjects      collection
     * @param layer            layer to place rain at
     * @param windowDimensions rain fits the whole screen
     * @param imageReader      to read animations
     * @param soundReader      to read sounds
     * @param duration         duration of rain
     * @return GameObject rain
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,
                                    ImageReader imageReader, SoundReader soundReader, int duration) {
        // only 1 instance of rain can be present
        if (isInstantiated)
            return null;
        // read image
        Renderable renderable = new AnimationRenderable(RAIN_ANIMATION, imageReader, true, 0.1);
        // create rain, place it.
        GameObject rain = new Rain(Vector2.ZERO, windowDimensions, renderable, duration, gameObjects, layer, soundReader);
        rain.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        rain.renderer().setOpaqueness(RAIN_OPAQ);
        gameObjects.addGameObject(rain, layer);
        rain.setTag(RAIN_TAG);
        Rain.isInstantiated = true;
        return rain;
    } // end of method create
}
