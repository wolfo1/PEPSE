package pepse.world.NPC;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.gui.ImageReader;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Terrain;

public class Skeleton extends Enemy {
    // constants
    private static final int SKELETON_SIZE = 80;
    private static final int SKELETON_HP = 4;
    private static final float VELOCITY_X = 200;
    private static final float VELOCITY_Y = -400;
    private static final float GRAVITY = 600;
    private static final Vector2 BONES_DIMENSIONS = new Vector2(40, 40);
    private static final float BONES_DURATION = 30;
    // assets
    private static final String[] SKELETON_WALK = {"src/assets/skeletonWalk1.png", "src/assets/skeletonWalk2.png"};
    private static final double TIME_BETWEEN_WALK = 0.1;
    private static final String SKELETON_DEAD = "src/assets/skeletonDead.png";
    private static Renderable deadRender;
    // fields
    private final Terrain terrain;
    private final int layer;

    /**
     * creates an Enemy in the game, of type Skeleton, melee damage enemy.
     * @param topLeftCorner    top left corner of the enemy
     * @param renderable       renderable of the enemy.
     * @param avatar           the player character, for AI uses
     * @param gameObjects      game Object collection
     * @param terrain          terrain in the game, calculate if needs to jump
     */
    public Skeleton(Vector2 topLeftCorner, Renderable renderable, Avatar avatar,
                    GameObjectCollection gameObjects, Terrain terrain, int layer,
                    ImageReader imageReader) {
        super(topLeftCorner, Vector2.ONES.mult(SKELETON_SIZE), renderable, avatar, SKELETON_HP, gameObjects,
                imageReader);
        this.terrain = terrain;
        this.layer = layer;
        // add gravity, collide with ground.
        transform().setAccelerationY(GRAVITY);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
    }

    /**
     * Create skeleton at xLocation, adds it to the game.
     * @param xLocation location to place skeleton at
     * @param avatar avatar to follow
     * @param gameObjects Collection
     * @param imageReader ImageReader
     * @param terrain terrain to calculate terrain height
     * @param layer layer to place skeleton at
     * @param tag enemy tag
     * @return the skeleton
     */
    public static Enemy create(float xLocation, Avatar avatar, GameObjectCollection gameObjects,
                              ImageReader imageReader, Terrain terrain, int layer, String tag) {
        // read images for the animations
        Renderable walkRender = new AnimationRenderable(SKELETON_WALK, imageReader, true, TIME_BETWEEN_WALK);
        deadRender = imageReader.readImage(SKELETON_DEAD, true);
        // create skeleton
        Skeleton skeleton = new Skeleton(new Vector2(xLocation, avatar.getCenter().y() - 200),
                walkRender, avatar, gameObjects, terrain, layer, imageReader);
        gameObjects.addGameObject(skeleton, layer);
        skeleton.setTag(tag);
        // initialize health
        return skeleton;
    }

    /**
     * Skeleton AI is once seeing the avatar, always walk towards it.
     * @param deltaTime game time
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // check if skeleton is dead
        if (hpBar.getCurrHP() == 0)
            die();
        // check if skeleton crossed the terrain
        float groundHeight = terrain.groundHeightAt(this.getCenter().x()) + 10;
        if (getTopLeftCorner().y() - getDimensions().y() > groundHeight) {
            this.setTopLeftCorner(new Vector2(getTopLeftCorner().x(), (float) (Math.floor(groundHeight) - getDimensions().y())));
        }
        // walk toward the avatar
        float xVel = 0;
        float height = getTopLeftCorner().y() + getDimensions().y();
        // avatar is to the right
        if (getAvatarLocation().x() > this.getCenter().x()) {
            // walk right
            xVel += VELOCITY_X;
            renderer().setIsFlippedHorizontally(false);
            // if the ground is higher ahead to the right, jump
            float rightXedge = getTopLeftCorner().x() + getDimensions().x() - 10;
            for (int i = 0; i < 30; i++) {
                if (height < Math.floor(terrain.groundHeightAt(rightXedge + i)) && getVelocity().y() == 0)
                    transform().setVelocityY(VELOCITY_Y);
            }
        }
        // avatar is to the left
        else {
            xVel -= VELOCITY_X;
            renderer().setIsFlippedHorizontally(true);
            // if the ground is higher to the left, jump
            float leftXEdge = getTopLeftCorner().x() + 10;
            for (int i = 0; i < 50; i++) {
                if (height < Math.floor(terrain.groundHeightAt(leftXEdge - i)) && getVelocity().y() == 0)
                    transform().setVelocityY(VELOCITY_Y);
            }
        }
        // move in direction
        transform().setVelocityX(xVel);
    }

    /**
     * Method deletes the skeleton from the game, and creates a "dead skeleton" object instead, which will
     * live for a certain number of time.
     */
    public void die() {
        super.die();
        gameObjects.removeGameObject(this, layer);
        // create skeleton remains
        GameObject bones = new GameObject(this.getCenter(), BONES_DIMENSIONS, deadRender);
        gameObjects.addGameObject(bones, Layer.STATIC_OBJECTS);
        bones.transform().setAccelerationY(GRAVITY);
        bones.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        // delete bones after BONES_DURATION seconds.
        new ScheduledTask(
                bones,
                BONES_DURATION,
                false,
                () -> gameObjects.removeGameObject(bones, Layer.STATIC_OBJECTS)
        );
    }
}
