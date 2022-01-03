package pepse.world.weapons;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.function.Consumer;

public abstract class Projectile extends GameObject {
    private final GameObjectCollection gameObjects;
    private Consumer<Vector2> hitEffect;

    public Projectile(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                      GameObjectCollection gameObjects, float accelerationX, boolean direction) {
        super(topLeftCorner, dimensions, renderable);
        this.gameObjects = gameObjects;
        if (direction) {
            accelerationX = -accelerationX;
            renderer().setIsFlippedHorizontally(true);
        }
        this.setVelocity(new Vector2(accelerationX, 0));
    }

    public GameObjectCollection getGameObjects() { return this.gameObjects; }

    public void setHitEffect(Consumer<Vector2> hitEffect) { this.hitEffect = hitEffect; }

    public void runHitEffect(Vector2 location) { this.hitEffect.accept(location); }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        hitEffect.accept(this.getCenter());
    }
}
