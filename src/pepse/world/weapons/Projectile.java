package pepse.world.weapons;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.function.Consumer;

/**
 * abstract class of a Projectile in the game.
 * A projectile gets an acceleration in a given direction, and can set a callback Consumer to be called
 * when the projectile hits something.
 */
public abstract class Projectile extends GameObject {
    protected final GameObjectCollection gameObjects;
    // the hit effect that happens when the projectile hits something.
    private Consumer<Vector2> hitEffect = null;

    /**
     * default c'tor
     * @param topLeftCorner starting location of the projectile
     * @param dimensions dimensions vector
     * @param renderable image of the projectile. Should point to the right!
     * @param gameObjects gameobjectcollection
     * @param accelerationX float, acceleration in the X-axis.
     * @param direction boolean, the direction the projectile is going,
     *                 true is left, false is right. (according to renderer().isFlippedHorizontally of avatar).
     */
    public Projectile(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                      GameObjectCollection gameObjects, float accelerationX, boolean direction) {
        super(topLeftCorner, dimensions, renderable);
        this.gameObjects = gameObjects;
        // if direction is left, flip the image and acceleration.
        if (direction) {
            accelerationX = -accelerationX;
            renderer().setIsFlippedHorizontally(true);
        }
        this.setVelocity(new Vector2(accelerationX, 0));
    }

    /**
     * get a Consumer that takes a Vector2 and does an effect.
     * @param hitEffect Consumer<Vector2>
     */
    protected void setHitEffect(Consumer<Vector2> hitEffect) { this.hitEffect = hitEffect; }

    /**
     * run the hit effect if it's not null according to given location vector.
     * @param location location of the hit
     */
    protected void runHitEffect(Vector2 location) {
        if (this.hitEffect != null)
            this.hitEffect.accept(location);
    }

    /**
     * on collision, call hitEffect on the center of the projectile
     * @param other object being hit
     * @param collision collision information
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        runHitEffect(this.getCenter());
    }
}
