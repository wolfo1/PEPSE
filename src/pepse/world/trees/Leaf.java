package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

public class Leaf extends GameObject {
    //fields
    private final String groundTag;
    // constants
    private boolean transitionExist = false;
    private final int xNewVelocity = 0;
    private final int yNewVelocity = 0;
    private final int MINIAML_VALUE = 2;
    private final int MAXIMAL_VALUE = 7;
    private final int NEGATIVE = -1;
    private final int xVelocity = 20;
    private final int yVelocity = 25;
    private Transition<Float> horizontalTransition;

    /**
     * Constructs a new Leaf instance.
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     * @praam leafTag Tag of the leaves
     * @praam groundTag Tag of the terrain
     */
    public Leaf(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, String leafTag, String groundTag) {
        super(topLeftCorner, dimensions, renderable);
        this.groundTag = groundTag;
        this.setTag(leafTag);
    } // end of constructor

    /**
     * Overrides on collision
     * @param other The other object for the collision
     * @param collision The type of collision
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (other.getTag().equals(groundTag)){
            this.transform().setVelocity(xNewVelocity,yNewVelocity);
            removeComponent(this.horizontalTransition);
        } // end of if
    } // end of onCollisionEnter override method

    /**
     * Creates a Transition for vertical movement.
     * @param leaf the leaf to append the Transition to.
     * @return The horizontal transition
     */
    public Transition<Float> leafFallTransition(GameObject leaf, int transitionTime) {
        if(!transitionExist) {
            this.horizontalTransition = new Transition<>(
                    leaf,
                    (val) -> {
                        if (val < MINIAML_VALUE)
                            leaf.transform().setVelocity(xVelocity, yVelocity);
                        if (val > MAXIMAL_VALUE)
                            leaf.transform().setVelocity(NEGATIVE* xVelocity, yVelocity);
                    },
                    0f, 10f, Transition.CUBIC_INTERPOLATOR_FLOAT, transitionTime,
                    Transition.TransitionType.TRANSITION_LOOP, null);
            transitionExist = true;
        } // end of if
        return this.horizontalTransition;
    } // end of initLeafVerticalFallTransition
} // end of class leaf

