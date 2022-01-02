package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

public class Avatar extends GameObject {
    private static final float VELOCITY_X = 400;
    private static final float VELOCITY_Y = -400;
    private static final float GRAVITY = 600;
    private static final float MAX_SPEED = 300;
    private static final String JUMP_SOUND_PATH = "assets/jump.wav";
    private static final String FLIGHT_SOUND_PATH = "assets/fly.wav";
    private static final String[] WALK_PATH =  {"assets/walk1.png", "assets/walk2.png"};
    private static final String[] MODEL_PATH = {"assets/model1.png", "assets/model2.png"};
    private static final String JUMP_PATH = "assets/jump.png";
    private static final String FLY_PATH = "assets/fly.png";

    // used for flight.
    private float energy = 100;
    // used for sound management
    private boolean inFlight = false;
    private final UserInputListener inputListener;
    private final Renderable walkAnimation;
    private final Renderable modelAnimation;
    private final Renderable jumpAnimation;
    private final Renderable flyAnimation;
    private Sound jumpSound = null;
    private Sound flightSound = null;

    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener, ImageReader imageReader) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
        this.modelAnimation = renderable;
        this.jumpAnimation = imageReader.readImage(JUMP_PATH, true);
        this.walkAnimation = new AnimationRenderable(WALK_PATH, imageReader, true, 0.1);
        this.flyAnimation = imageReader.readImage(FLY_PATH, true);
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
        Renderable model = new AnimationRenderable(MODEL_PATH, imageReader, true, 0.3);
        Avatar avatar = new Avatar(topLeftCorner, Vector2.ONES.mult(80), model,
                inputListener, imageReader);
        avatar.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        avatar.transform().setAccelerationY(GRAVITY);
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    } // end of method create

    public void setSounds(SoundReader soundReader) {
        this.jumpSound = soundReader.readSound(JUMP_SOUND_PATH);
        this.flightSound = soundReader.readSound(FLIGHT_SOUND_PATH);
    } // end of class setSounds

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // max vertical speed to mitigate collision problems with the ground
        if (this.getVelocity().y() > MAX_SPEED) {
            this.setVelocity(new Vector2(getVelocity().x(), MAX_SPEED));
        }
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
        else if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0) {
            transform().setVelocityX(xVel);
            this.renderer().setRenderable(this.jumpAnimation);
            transform().setVelocityY(VELOCITY_Y);
            if (jumpSound != null)
                jumpSound.play();
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
    }
} // end of class Avatar
