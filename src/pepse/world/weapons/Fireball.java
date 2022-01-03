package pepse.world.weapons;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.rendering.AnimationRenderable;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.phenomenon.Explosion;

import java.util.Objects;
import java.util.function.Consumer;

public class Fireball extends Projectile{
    private static final int EXPLOSION_RADIUS = 100;
    private static final String[] FIREBALL_IMAGE_PATH = {"assets/fireball1.png", "assets/fireball2.png"};
    private static final String FIREBALL_SOUND_PATH = "assets/fireball.wav";
    private static final double TIME_BETWEEN_CLIPS = 0.1;
    private static final float ACCELERATION_X = 300;
    private static final Vector2 DIMENSIONS = new Vector2(70, 30);
    private static final int TRAVEL_DISTANCE = 1000;

    private static boolean isInstantiated = false;

    private final float startLocation;

    public Fireball(Vector2 topLeftCorner, GameObjectCollection gameObjects, int layer, ImageReader imageReader, SoundReader soundReader) {
        super(topLeftCorner, DIMENSIONS, new AnimationRenderable(FIREBALL_IMAGE_PATH, imageReader, true, TIME_BETWEEN_CLIPS),
                gameObjects, ACCELERATION_X);
        Fireball.isInstantiated = true;
        this.startLocation = topLeftCorner.x();
        soundReader.readSound(FIREBALL_SOUND_PATH).play();
        Consumer<Vector2> hitEffect = hitLocation -> {
            gameObjects.removeGameObject(this, layer);
            Explosion.create(gameObjects, hitLocation, EXPLOSION_RADIUS, imageReader, layer, soundReader);
            Fireball.isInstantiated = false;
            };
        this.setHitEffect(hitEffect);
    }

    public static Fireball create(Vector2 topLeftCorner, GameObjectCollection gameObjects ,
                                  int layer, ImageReader imageReader, SoundReader soundReader) {
        if (!Fireball.isInstantiated) {
            Fireball fireball = new Fireball(topLeftCorner, gameObjects, layer, imageReader, soundReader);
            gameObjects.addGameObject(fireball);
            return fireball;
        }
        else
            return null;
    }

    @Override
    public boolean shouldCollideWith(GameObject other) {
        return !Objects.equals(other.getTag(), Avatar.AVATAR_TAG);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (Math.abs(this.getCenter().x() - startLocation) > TRAVEL_DISTANCE) {
            this.runHitEffect(this.getCenter());
        }
    }
}
