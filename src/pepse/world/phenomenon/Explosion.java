package pepse.world.phenomenon;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.NPC.Enemy;

/**
 * Explosion is a phenomenon in the world which destroys everything it touches. Disappears after the explosion
 * is done.
 */
public class Explosion extends GameObject{
    // constants
    private static final int EXPLOSION_TIME = 20; // in update frames
    public static final String EXPLOSION_TAG = "explosion";
    private static final int EXPLOSIONS_DAMAGE = 3;
    // assets
    private static final String[] ANIMATION_PATH = {"src/assets/explosion1.png", "src/assets/explosion2.png",
            "src/assets/explosion3.png", "src/assets/explosion4.png", "src/assets/explosion5.png",
            "src/assets/explosion6.png", "src/assets/explosion7.png"};
    private static final double TIME_BETWEEN_CLIPS = 0.1;
    private static final String SOUND_PATH = "src/assets/explosion.wav";
    // static fields
    private static Renderable explosionAnimation;
    private static Sound explosionSound;
    // fields
    private final GameObjectCollection gameObjects;
    private final int layer;
    private int count = 0;

    /**
     * default c'tor
     * @param dimensions dimensions of the explosion, Vector 2.
     * @param gameObjects collection of game objects
     * @param layer the layer the explosion is at
     * @param soundReader read sounds.
     */
    public Explosion(Vector2 dimensions, GameObjectCollection gameObjects, int layer, SoundReader soundReader) {
        super(Vector2.ZERO, dimensions, explosionAnimation);
        this.gameObjects = gameObjects;
        this.layer = layer;
        this.setTag(EXPLOSION_TAG);
        // play explosion sound once
        explosionSound.play();
    }

    /**
     * Creates a GameObject of type Explosion, adds its to the game at a given layer and location.
     * @param gameObjects gameObject Collection - to add the explosion.
     * @param location location to place the explosion at.
     * @param explosionRadius radius (dimensions) of the explosion - in Int.
     * @param imageReader - Image Reader to create explosion animation.
     * @param layer - the layer to place the explosion at.
     * @param soundReader - Sound Reader to create explosion sound.
     * @return GameoOject explosion
     */
    public static GameObject create(GameObjectCollection gameObjects, Vector2 location, int explosionRadius,
                                    ImageReader imageReader, int layer, SoundReader soundReader) {
        // create the renderable animation of the explosion
        Explosion explosion = new Explosion(new Vector2(explosionRadius, explosionRadius), gameObjects, layer, soundReader);
        gameObjects.addGameObject(explosion, layer);
        explosion.setCenter(location);
        return explosion;
    }

    /**
     * initialize static assets.
     * @param imageReader Read images.
     * @param soundReader Read sounds.
     */
    public static void initAssets(ImageReader imageReader, SoundReader soundReader) {
        explosionAnimation = new AnimationRenderable(ANIMATION_PATH, imageReader, true, TIME_BETWEEN_CLIPS);
        explosionSound = soundReader.readSound(SOUND_PATH);
    }
    /**
     * explosion will not destroy the avatar or another explosion.
     * @param other game object collided with explosion
     * @return true if not avatar, false if avatar.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        return (!other.getTag().equals(Avatar.AVATAR_TAG) && !other.getTag().equals(EXPLOSION_TAG));
    }

    /**
     * explosion removes every object it touches from the game.
     * @param other game object to remove
     * @param collision collision information
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        // if object is an enemy, hit it with damage. else, remove object from game.
        if (other instanceof Enemy) {
            Enemy enemy;
            enemy = (Enemy) other;
            enemy.damageEnemy(EXPLOSIONS_DAMAGE);
        } else {
            for (int i = 0; i <= 10; i++) {
                if (gameObjects.removeGameObject(other, layer + i)) {
                    break;
                }
            }
        }
    }

    /**
     * removes the explosion from the game after the animation stops/
     * @param deltaTime game time
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        count++;
        // remove explosion from the game after animation has finished.
        if (count > EXPLOSION_TIME)
            gameObjects.removeGameObject(this, layer);
    }
}
