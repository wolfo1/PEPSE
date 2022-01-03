package pepse.world.phenomenon;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;


public class Rain extends GameObject {
    private static final String RAIN_TAG = "rain";
    private static final String[] RAIN_ANIMATION = {"assets/rain1.png", "assets/rain2.png", "assets/rain3.png",
                                                    "assets/rain4.png", "assets/rain5.png"};
    private static final String RAIN_SOUND = "assets/rain.wav";

    public static boolean isInstantiated = false;

    private final Sound rainSound;
    private final int duration;
    private final GameObjectCollection gameObjects;
    private final int layer;
    private int count = 0;

    public Rain(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, int duration,
                GameObjectCollection gameObjects, int layer, SoundReader soundReader) {
        super(topLeftCorner, dimensions, renderable);
        this.duration = duration;
        rainSound = soundReader.readSound(RAIN_SOUND);
        rainSound.playLooped();
        this.gameObjects = gameObjects;
        this.layer = layer;
    }

    public static GameObject create(GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,
                                    ImageReader imageReader, SoundReader soundReader, int duration){
        // only 1 instance of rain can be present
        if (isInstantiated)
            return null;
        Renderable renderable = new AnimationRenderable(RAIN_ANIMATION, imageReader, true, 0.1);
        GameObject rain = new Rain(Vector2.ZERO, windowDimensions, renderable, duration, gameObjects, layer, soundReader);
        rain.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        rain.renderer().setOpaqueness(0.2f);
        gameObjects.addGameObject(rain, layer);
        rain.setTag(RAIN_TAG);
        Rain.isInstantiated = true;
        return rain;
    } // end of method create

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (count > duration) {
            rainSound.stopAllOccurences();
            gameObjects.removeGameObject(this, layer);
            isInstantiated = false;
        }
        else
            count++;
    }
}
