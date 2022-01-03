package pepse.world.phenomenon;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Avatar;

public class Explosion extends GameObject{
    private static final String[] ANIMATION_PATH = {"assets/explosion1.png", "assets/explosion2.png",
            "assets/explosion3.png", "assets/explosion4.png", "assets/explosion5.png",
            "assets/explosion6.png", "assets/explosion7.png"};
    private static final String SOUND_PATH = "assets/explosion.wav";
    private static final double TIME_BETWEEN_CLIPS = 0.1;
    private static final int EXPLOSION_TIME = 28; // in update frames
    private final GameObjectCollection gameObjects;
    private final int layer;

    private int count = 0;

    public Explosion(Vector2 dimensions, Renderable renderable, GameObjectCollection gameObjects, int layer, SoundReader soundReader) {
        super(Vector2.ZERO, dimensions, renderable);
        this.gameObjects = gameObjects;
        this.layer = layer;
        soundReader.readSound(SOUND_PATH).play();
    }

    public static GameObject create(GameObjectCollection gameObjects, Vector2 location, int explosionRadius,
                                    ImageReader imageReader, int layer, SoundReader soundReader) {
        Renderable renderable = new AnimationRenderable(ANIMATION_PATH, imageReader, true, TIME_BETWEEN_CLIPS);
        Explosion explosion = new Explosion(new Vector2(explosionRadius, explosionRadius), renderable, gameObjects, layer, soundReader);
        gameObjects.addGameObject(explosion, layer);
        explosion.setCenter(location);
        return explosion;
    }

    @Override
    public boolean shouldCollideWith(GameObject other) {
        return (!other.getTag().equals(Avatar.AVATAR_TAG));
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        gameObjects.removeGameObject(other, layer);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        count++;
        if (count > EXPLOSION_TIME)
            gameObjects.removeGameObject(this, layer);
    }
}
