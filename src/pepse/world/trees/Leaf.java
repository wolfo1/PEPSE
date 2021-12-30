package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

public class Leaf extends GameObject {
    private Transition<Float> verticalTransition;

    /**
     * Construct a new Leaf instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     */
    public Leaf(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
        this.setTag("leafBlock");
    }

    /**
     * Overrides on collision
     * @param other The other object for the collision
     * @param collision The type of collision
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (other.getTag().equals("upper terrain")){
            this.transform().setVelocity(0,0);
            this.removeComponent(verticalTransition);
        }
    }

    /**
     * Creates a Transition for vertical movement.
     * @param leaf the leaf to append the Transition to.
     * @return the transition
     */
    public Transition<Float> initLeafVerticalFallTransition(GameObject leaf, int transitionTime) {
        verticalTransition = new Transition<>(
                leaf,
                (val) -> {
                    if(val < 2) {
                        leaf.transform().setVelocity(20, 25);
                    }
                    if(val > 7) {
                        leaf.transform().setVelocity(-20, 25);
                    }
                },
                0f, 10f, Transition.CUBIC_INTERPOLATOR_FLOAT, transitionTime,
                Transition.TransitionType.TRANSITION_LOOP, null );
        return verticalTransition;
    } // end of initLeafVerticalFallTransition
} // end of class leaf

