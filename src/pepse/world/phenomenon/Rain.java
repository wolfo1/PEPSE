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

/**
 * singleton class Rain representing rain in the game
 */
public class Rain extends GameObject {
    // constants
    private static final String RAIN_TAG = "rain";
    private static final float RAIN_OPAQ = 0.2f;
    // assets
    private static final String[] RAIN_ANIMATION = {"src/assets/rain1.png", "src/assets/rain2.png"};
    // add this to add more frames to animation "src/assets/rain3.png", "src/assets/rain4.png", "src/assets/rain5.png".
    private static final double TIME_BETWEEN_CLIPS = 0.1;
    private static final String RAIN_SOUND = "src/assets/rain.wav";
    // singleton object
    private static Rain rain;
    private static boolean isRaining = false;
    // fields
    private final Sound rainSound;
    private final GameObjectCollection gameObjects;
    private final int layer;

    /**
     * default c'tor
     *
     * @param topLeftCorner location
     * @param dimensions    dimensions vector
     * @param renderable    animation
     * @param gameObjects   collection to stop the rain
     * @param layer         layer to remove rain from
     * @param soundReader   SoundReader
     */
    private Rain(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, GameObjectCollection gameObjects,
                 int layer, SoundReader soundReader) {
        super(topLeftCorner, dimensions, renderable);
        // read sound
        rainSound = soundReader.readSound(RAIN_SOUND);
        this.gameObjects = gameObjects;
        this.layer = layer;
    }

    /**
     * creates Rain and adds it to collection
     *
     * @param gameObjects      collection
     * @param layer            layer to place rain at
     * @param windowDimensions rain fits the whole screen
     * @param imageReader      to read animations
     * @param soundReader      to read sounds
     */
    public static void create(GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,
                                    ImageReader imageReader, SoundReader soundReader) {
        // read image
        Renderable renderable = new AnimationRenderable(RAIN_ANIMATION, imageReader, true, TIME_BETWEEN_CLIPS);
        // create rain, set it in CAMERA coordinate space.
        rain = new Rain(Vector2.ZERO, windowDimensions, renderable, gameObjects, layer, soundReader);
        rain.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        rain.renderer().setOpaqueness(RAIN_OPAQ);
        rain.setTag(RAIN_TAG);
    } // end of method create

    /**
     * starts raining for X seconds
     * @param duration duration in seconds
     */
    public static void startRain(int duration) {
        if (!isRaining) {
            rain.gameObjects.addGameObject(rain, rain.layer);
            rain.rainSound.playLooped();
            isRaining = true;
            // remove Rain after duration, stop sound and set Instantiated to false.
            new ScheduledTask(rain, duration, false, () -> {
                isRaining = false;
                rain.rainSound.stopAllOccurences();
                rain.gameObjects.removeGameObject(rain, rain.layer);
            });
        }
    }
    /**
     * stops raining prematurely, for any reason.
     */
    public static void stopRain() {
        isRaining = false;
        rain.rainSound.stopAllOccurences();
        rain.gameObjects.removeGameObject(rain, rain.layer);
    }
}
