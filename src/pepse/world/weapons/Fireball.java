package pepse.world.weapons;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.rendering.AnimationRenderable;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.phenomenon.Explosion;

import java.util.function.Consumer;

/**
 * Fireball is a type of weapon which fires a blast of fire from the hands of the character.
 * when the fireball hits something, it creates an explosion (see Phenomenon.explosion).
 */
public class Fireball extends Projectile{
    // animation and sound related
    private static final String[] FIREBALL_IMAGE_PATH = {"assets/fireball1.png", "assets/fireball2.png"};
    private static final String FIREBALL_SOUND_PATH = "assets/fireball.wav";
    private static final double TIME_BETWEEN_CLIPS = 0.1;
    private static final Vector2 DIMENSIONS = new Vector2(70, 30);
    // acceleration, maximum travel distance (explodes after reaching it), and explosion radius.
    private static final float ACCELERATION_X = 300;
    private static final int MAX_TRAVEL_DISTANCE = 1000;
    private static final int EXPLOSION_RADIUS = 150;
    // only 1 fireball can be present at each moment.
    private static boolean isInstantiated = false;

    private final float startLocation;

    /**
     * default c'tor of fireball
     * @param topLeftCorner starting location of the fireball
     * @param direction boolean, the direction the projectile is going,
     *                  true is left, false is right. (according to renderer().isFlippedHorizontally of avatar).
     * @param gameObjects gameobjects collection in order to create explosion
     * @param layer the layer on which to create the explosion
     * @param imageReader ImageReader.
     * @param soundReader SoundReader.
     */
    public Fireball(Vector2 topLeftCorner, boolean direction, GameObjectCollection gameObjects, int layer,
                    ImageReader imageReader, SoundReader soundReader) {
        super(topLeftCorner, DIMENSIONS, new AnimationRenderable(FIREBALL_IMAGE_PATH, imageReader, true, TIME_BETWEEN_CLIPS),
                gameObjects, ACCELERATION_X, direction);
        Fireball.isInstantiated = true;
        // get starting location of the fireball to calculate maximum travel distance
        this.startLocation = topLeftCorner.x();
        soundReader.readSound(FIREBALL_SOUND_PATH).play();
        // hit effect callback, which creates an explosion & remove the fireball from the game.
        Consumer<Vector2> hitEffect = hitLocation -> {
            gameObjects.removeGameObject(this, layer);
            Explosion.create(gameObjects, hitLocation, EXPLOSION_RADIUS, imageReader, layer, soundReader);
            Fireball.isInstantiated = false;
        };
        this.setHitEffect(hitEffect);
    }

    /**
     * creates a fireball in a given location, direction, and layer.
     * only 1 fireball can be created on screen, if there is a fireball already return null.
     * @param topLeftCorner starting location of the fireball
     * @param direction boolean, the direction the projectile is going,
     *                 true is left, false is right. (according to renderer().isFlippedHorizontally of avatar).
     * @param gameObjects gameobjects collection in order to create explosion
     * @param layer the layer on which to create the explosion
     * @param imageReader ImageReader.
     * @param soundReader SoundReader.
     * @return
     */
    public static Fireball create(Vector2 topLeftCorner, boolean direction, GameObjectCollection gameObjects,
                                  int layer, ImageReader imageReader, SoundReader soundReader) {
        // create only if there are no fireballs in-game.
        if (!Fireball.isInstantiated) {
            Fireball fireball = new Fireball(topLeftCorner, direction, gameObjects, layer, imageReader, soundReader);
            gameObjects.addGameObject(fireball, layer);
            return fireball;
        }
        else
            return null;
    }

    /**
     * the fireball doesn't collide with the avatar or explosions.
     * @param other the object colliding with the fireball
     * @return true if object is not avatar or explosion, false otherwise.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        return (!other.getTag().equals(Avatar.AVATAR_TAG) && !other.getTag().equals(Explosion.EXPLOSION_TAG));
    }

    /**
     * checks if the fireball has traveled more than the maximum travel distance, and if so calls hitEffect.
     * @param deltaTime game time
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (Math.abs(this.getCenter().x() - startLocation) > MAX_TRAVEL_DISTANCE) {
            this.runHitEffect(this.getCenter());
        }
    }
}
