package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.ui.HPBar;
import pepse.world.NPC.Enemy;
import pepse.world.weapons.Fireball;
import pepse.world.weapons.Projectile;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.awt.event.KeyEvent;

public class Avatar extends GameObject {
    // used for collide checks for other objects
    public static final String AVATAR_TAG = "avatar";
    // constants
    private static final int AVATAR_SIZE = 80;
    private static final Vector2 PROJECTILE_EXIT_LOCATION = new Vector2(-60, 0);
    private static final float VELOCITY_X = 400;
    private static final float VELOCITY_Y = -400;
    private static final float GRAVITY = 600;
    private static final int AVATAR_HP = 3;
    // assets
    private static final String JUMP_SOUND_PATH = "src/assets/jump.wav";
    private static final String FLIGHT_SOUND_PATH = "src/assets/fly.wav";
    private static final String[] WALK_PATH =  {"src/assets/walk1.png", "src/assets/walk2.png"};
    private static final double TIME_BETWEEN_WALK = 0.1;
    private static final String[] MODEL_PATH = {"src/assets/model1.png", "src/assets/model2.png"};
    private static final double TIME_BETWEEN_MODEL = 0.3;
    private static final String JUMP_PATH = "src/assets/jump.png";
    private static final String FLY_PATH = "src/assets/fly.png";
    private static final String GRAVE_PATH = "src/assets/grave.png";
    private static final Vector2 GRAVE_DIMENSIONS = new Vector2(130, 120);
    private static final float DEATH_DURATION = 5;
    private static final float MAX_SPEED = 300;
    private final Renderable walkAnimation;
    private final Renderable modelAnimation;
    private final Renderable jumpAnimation;
    private final Renderable flyAnimation;
    //fields
    private Terrain terrain;
    private int projectileLayer;
    private final int selfLayer;
    private final ImageReader imageReader;
    private final UserInputListener inputListener;
    private final GameObjectCollection gameObjects;
    private boolean isDead = false;
    private boolean inFlight = false;
    private final HPBar hpBar;
    private float energy = 100;
    // sound
    private SoundReader soundReader;
    private Sound jumpSound = null;
    private Sound flightSound = null;

    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener, ImageReader imageReader, GameObjectCollection gameObjects,
                  int layer) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
        this.imageReader = imageReader;
        this.gameObjects = gameObjects;
        this.modelAnimation = renderable;
        this.jumpAnimation = imageReader.readImage(JUMP_PATH, true);
        this.walkAnimation = new AnimationRenderable(WALK_PATH, imageReader, true, TIME_BETWEEN_WALK);
        this.flyAnimation = imageReader.readImage(FLY_PATH, true);
        this.selfLayer = layer;
        this.hpBar = new HPBar(this, AVATAR_HP, imageReader, gameObjects);
        gameObjects.addGameObject(hpBar, Layer.UI);
    }

    /**
     * Method that creates an Avatar
     * @param gameObjects The collection of all participating game objects.
     * @param layer The number of the layer to which the created avatar should be added.
     * @param topLeftCorner The location of the top-left corner of the created avatar.
     * @param inputListener Used for reading input from the user.
     * @param imageReader Used for reading images from disk or from within a jar.
     * @return A newly created representing the avatar.
     */
    public static Avatar create(GameObjectCollection gameObjects, int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener, ImageReader imageReader){
        Renderable model = new AnimationRenderable(MODEL_PATH, imageReader, true, TIME_BETWEEN_MODEL);
        Avatar avatar = new Avatar(topLeftCorner, Vector2.ONES.mult(AVATAR_SIZE), model,
                inputListener, imageReader, gameObjects, layer);
        avatar.transform().setAccelerationY(GRAVITY);
        avatar.setTag(AVATAR_TAG);
        avatar.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    } // end of method create

    /**
     * allows to set Sound Reader and sounds to the avatar and it's weapons.
     * @param soundReader SoundReader type.
     */
    public void setSounds(SoundReader soundReader) {
        this.soundReader = soundReader;
        this.jumpSound = soundReader.readSound(JUMP_SOUND_PATH);
        this.flightSound = soundReader.readSound(FLIGHT_SOUND_PATH);
    } // end of class setSounds

    /**
     * sets the layer on which projectiles will be places
     * @param projectileLayer the layer
     */
    public void setProjectileLayer(int projectileLayer) { this.projectileLayer = projectileLayer; }

    public void setTerrain(Terrain terrain) { this.terrain = terrain;}

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        // if Avatar touches an enemy or gets hit by an enemy projectile, lower HP by 1.
        if (other instanceof Enemy || other instanceof Projectile)
            hpBar.removeHearts(1);
    }

    public boolean isDead() { return isDead; }

    /**
     * movement left/right/jump/fly logic, and also fire weapons logic.
     * @param deltaTime game time
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (hpBar.getCurrHP() == 0)
            die();
        if (getVelocity().y() > MAX_SPEED)
            setVelocity(new Vector2(getVelocity().x(), MAX_SPEED));
        float xVel = 0;
        // walk left
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            xVel -= VELOCITY_X;
            renderer().setIsFlippedHorizontally(true);
            if (getVelocity().y() == 0) {
                renderer().setRenderable(walkAnimation);
                this.renderer().setIsFlippedHorizontally(true);
            }
        }
        // walk right
        else if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            xVel += VELOCITY_X;
            renderer().setIsFlippedHorizontally(false);
            if (getVelocity().y() == 0) {
                renderer().setRenderable(walkAnimation);
                renderer().setIsFlippedHorizontally(false);
            }
        }
        // move in xVel velocity
        transform().setVelocityX(xVel);
        // fly
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && inputListener.isKeyPressed(KeyEvent.VK_SHIFT)) {
            if (this.energy > 0) {
                // play flightSound, only at the start of the flight.
                if (flightSound != null && !inFlight)
                    flightSound.playLooped();
                inFlight = true;
                this.renderer().setRenderable(this.flyAnimation);
                transform().setVelocityY(VELOCITY_Y);
                // energy consumption
                this.energy -= 0.5;
            }
        }
        // jump
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0) {
            this.renderer().setRenderable(this.jumpAnimation);
            transform().setVelocityY(VELOCITY_Y);
            if (jumpSound != null)
                jumpSound.play();
        }
        // fire a fireball from the character
        if (inputListener.isKeyPressed(KeyEvent.VK_G) && energy >= 10) {
            energy -= 10;
            Vector2 startingLocation = this.getCenter();
            if (renderer().isFlippedHorizontally())
                startingLocation = startingLocation.add(PROJECTILE_EXIT_LOCATION);
            Fireball.create(startingLocation, renderer().isFlippedHorizontally(),
                                gameObjects, projectileLayer, imageReader, soundReader);
        }
        // if stops flying (by energy consumption on stop hitting shift), stop sound.
        if ((!inputListener.isKeyPressed(KeyEvent.VK_SHIFT) || energy == 0) && flightSound != null) {
            flightSound.stopAllOccurences();
            inFlight = false;
        }
        // regenerate energy while standing on something.
        if (getVelocity().y() == 0) {
            this.energy += 0.5;
            if (getVelocity().x() == 0)
                this.renderer().setRenderable(modelAnimation);
        }
    } // end of method update

    private void die() {
        gameObjects.removeGameObject(this, selfLayer);
        Renderable graveRender = imageReader.readImage(GRAVE_PATH, true);
        GameObject grave = new GameObject(this.getTopLeftCorner(), GRAVE_DIMENSIONS, graveRender);
        gameObjects.addGameObject(grave, Layer.STATIC_OBJECTS);
        grave.transform().setAccelerationY(GRAVITY);
        grave.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        // delete bones after BONES_DURATION seconds.
        new ScheduledTask(
                grave,
                DEATH_DURATION,
                false,
                () -> isDead = true
        );
    }
} // end of class Avatar
